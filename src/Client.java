
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private String host;
    private int port;
    BufferedReader in;
    PrintWriter out;
    Socket s;

    private MessageReceivedListener messageReceivedListener;

    public Client(String host, int port) {
        System.out.println("Letrejott a kliens.");
        this.host = host;
        this.port = port;
        try {
            s = new Socket(host, port);
            in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            out = new PrintWriter(s.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    String msg = null;
                    try {
                        msg = in.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (msg != null) {
                        messageReceivedListener.onMessageReceived(msg);
                        System.out.println("A szerver valasza: " + msg);
                    }

                }
            }
        }).start();
    }

    public void send(String message) {
        System.out.println("UZENET ELKULDVE: " + message);
        out.println(message);
        out.flush();
//            s.close();
    }

    public void setMessageReceivedListener(MessageReceivedListener messageReceivedListener) {
        this.messageReceivedListener = messageReceivedListener;
    }
}
