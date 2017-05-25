/**
 * Created by tamas on 25/05/2017.
 */
public class Message {

    public static final String SIZE = "size";
    public static final String LENGTH = "length";
    public static final String MOVE = "move";
    public String cmd;
    public int p1;
    public int p2;

    public Message(String cmd, int p1, int p2) {
        this.cmd = cmd;
        this.p1 = p1;
        this.p2 = p2;
    }
}
