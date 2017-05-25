
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    Socket client;
    ServerSocket ss;

    BufferedReader in;
    PrintWriter out;

    private MessageReceivedListener messageReceivedListener;

    public Server(int port) {
        System.out.println("A kommunikacio a kovetkezo porton tortenik: " + port);
        try {
            ss = new ServerSocket(port);
            System.out.println("A szerver sikeresen elindult!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listen() {
        new Thread(() -> {
            try {
                client = ss.accept();
                System.out.println("Kliens csatlakozott");
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                out = new PrintWriter(new OutputStreamWriter(client.getOutputStream()));
                while (true) {
                    String msg = in.readLine();
                    if (msg != null) {
                        messageReceivedListener.onMessageReceived(msg);
                        System.out.println("A klienstol kapott uzenet: " + msg);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    in.close();
                    out.close();
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void setMessageReceivedListener(MessageReceivedListener messageReceivedListener) {
        this.messageReceivedListener = messageReceivedListener;
    }

    public void send(String message) {
        out.println(message);
        out.flush();
    }
}
