import com.google.gson.Gson;

import javax.swing.*;
import java.awt.*;

/**
 * Created by tamas on 25/05/2017.
 */
public class LandingFrame extends JFrame {


    private static final int SERVER = 0;
    private static final int CLIENT = 1;

    Server server;
    Client client;
    Gson gson;

    public LandingFrame() {
        gson = new Gson();
        setLayout(new GridLayout(1, 2));
        JButton singlePlayer = new JButton("Egyszemelyes");
        singlePlayer.addActionListener(e -> {
            //Egyszemelyes jatek
            Amoba amoba = new Amoba(5, 4, true, "Amoba");
            amoba.showTable();
            amoba.grafikus.setButtonClickListener((x, y) -> {
                amoba.playerStep(x, y);
                amoba.showTable();
                if (!amoba.isOver()) {
                    amoba.botStep();
                    amoba.showTable();

                    if (amoba.isOver()) {
                        amoba.grafikus.showWinner(amoba.winner());
                    }
                } else {
                    amoba.grafikus.showWinner(amoba.winner());
                }
            });
        });
        JButton multiPlayer = new JButton("Ketszemelyes");
        Object[] lehetosegek = new Object[2];
        lehetosegek[0] = "Szerver";
        lehetosegek[1] = "Kliens";
        multiPlayer.addActionListener(e -> {
            //Ketszemelyes jatek kliens-szerver
            int result = JOptionPane.showOptionDialog(LandingFrame.this, "Szerver vagy kliens?", "Valasztas", JOptionPane.YES_NO_OPTION, 0, null, lehetosegek, 0);
            switch (result) {
                case 0:
                    System.out.println("Szerver");
                    String portServer = JOptionPane.showInputDialog("Add meg a port-ot:");
                    server = new Server(Integer.parseInt(portServer));
                    Amoba amoba = new Amoba(5, 4, true, "Szerver");
                    amoba.showTable();
                    amoba.grafikus.setButtonClickListener((x, y) -> {
                        amoba.multiplayerStep(x, y, Amoba.O);
                        amoba.grafikus.enableButtons(false);
                        server.send(gson.toJson(new Message("move", x, y)));
                        if (amoba.isOver()) {
                            amoba.grafikus.showWinner(amoba.winner());
                        }
                    });

                    LandingFrame.this.setVisible(false);
                    LandingFrame.this.dispose();
                    server.setMessageReceivedListener(message -> {
                        Message m = gson.fromJson(message, Message.class);
                        if (m.cmd.equals("move")) {
                            amoba.multiplayerStep(m.p1, m.p2, Amoba.X);
                            if (amoba.isOver()) {
                                amoba.grafikus.showWinner(amoba.winner());
                            }
                            amoba.grafikus.enableButtons(true);
                        }
                    });
                    server.listen();

                    break;
                case 1:
                    System.out.println("Kliens");
                    String portClient = JOptionPane.showInputDialog("Add meg a port-ot:");
                    String host = JOptionPane.showInputDialog("Add meg a host-ot:");
                    client = new Client(host, Integer.parseInt(portClient));
                    Amoba amoba2 = new Amoba(5, 4, true, "Kliens");
                    amoba2.showTable();
                    amoba2.grafikus.setButtonClickListener((x, y) -> {
                        amoba2.multiplayerStep(x, y, Amoba.X);
                        client.send(gson.toJson(new Message("move", x, y)));
                        if (amoba2.isOver()) {
                            amoba2.grafikus.showWinner(amoba2.winner());
                        }
                        amoba2.grafikus.enableButtons(false);
                    });
                    client.setMessageReceivedListener(message -> {
                        Message m = gson.fromJson(message, Message.class);
                        if (m.cmd.equals("move")) {
                            amoba2.multiplayerStep(m.p1, m.p2, Amoba.O);
                            if (amoba2.isOver()) {
                                amoba2.grafikus.showWinner(amoba2.winner());
                            }
                            amoba2.grafikus.enableButtons(true);
                        }
                    });
                    LandingFrame.this.setVisible(false);
                    LandingFrame.this.dispose();
                    break;
            }
        });
        add(singlePlayer);
        add(multiPlayer);

        setTitle("Amoba");
        setBounds(200, 0, 600, 300);
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        new LandingFrame();
    }
}
