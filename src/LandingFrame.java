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
                    server.listen();
                    final Amoba[] amoba2 = new Amoba[1];
                    final int[] tableSize = {0};
                    final int[] gameLength = {0};
                    server.setMessageReceivedListener(message -> {
                        Message m = gson.fromJson(message, Message.class);
                        switch (m.cmd) {
                            case Message.SIZE:
                                tableSize[0] = m.p1;
                                if (gameLength[0] != 0) {
                                    amoba2[0] = createServerAmoba(server, tableSize[0], gameLength[0]);
                                }
                                break;
                            case Message.LENGTH:
                                gameLength[0] = m.p1;
                                if (tableSize[0] != 0) {
                                    amoba2[0] = createServerAmoba(server, tableSize[0], gameLength[0]);
                                }
                                break;
                            case Message.MOVE:
                                if (amoba2[0] != null) {
                                    amoba2[0].multiplayerStep(m.p1, m.p2, Amoba.X);
                                    if (amoba2[0].isOver()) {
                                        amoba2[0].grafikus.showWinner(amoba2[0].winner());
                                    }
                                    amoba2[0].grafikus.enableButtons(true);
                                }
                                break;
                        }
                    });

                    break;
                case 1:
                    System.out.println("Kliens");
                    String portClient = JOptionPane.showInputDialog("Add meg a port-ot:");
                    String host = JOptionPane.showInputDialog("Add meg a host-ot:");
                    client = new Client(host, Integer.parseInt(portClient));
                    int size = Integer.parseInt(JOptionPane.showInputDialog("A jatek merete:"));
                    int length = Integer.parseInt(JOptionPane.showInputDialog("A sorozat hossza:"));
                    client.send(gson.toJson(new Message(Message.SIZE, size, size)));
                    client.send(gson.toJson(new Message(Message.LENGTH, length, 0)));
                    Amoba amoba = new Amoba(size, length, true, "Kliens");
                    amoba.grafikus.setButtonClickListener((x, y) -> {
                        amoba.multiplayerStep(x, y, Amoba.X);
                        amoba.grafikus.enableButtons(false);
                        client.send(gson.toJson(new Message(Message.MOVE, x, y)));
                        if (amoba.isOver()) {
                            amoba.grafikus.showWinner(amoba.winner());
                        }
                    });

                    LandingFrame.this.setVisible(false);
                    LandingFrame.this.dispose();
                    client.setMessageReceivedListener(message -> {
                        Message m = gson.fromJson(message, Message.class);
                        if (m.cmd.equals(Message.MOVE)) {
                            amoba.multiplayerStep(m.p1, m.p2, Amoba.O);
                            if (amoba.isOver()) {
                                amoba.grafikus.showWinner(amoba.winner());
                            }
                            amoba.grafikus.enableButtons(true);
                        }
                    });

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

    private Amoba createServerAmoba(Server server, int size, int length) {
        Amoba amoba = new Amoba(size, length, true, "Szerver");
        amoba.grafikus.setButtonClickListener((x, y) -> {
            amoba.multiplayerStep(x, y, Amoba.O);
            server.send(gson.toJson(new Message(Message.MOVE, x, y)));
            if (amoba.isOver()) {
                amoba.grafikus.showWinner(amoba.winner());
            }
            amoba.grafikus.enableButtons(false);
        });
        LandingFrame.this.setVisible(false);
        LandingFrame.this.dispose();
        return amoba;
    }
}
