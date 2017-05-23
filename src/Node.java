import java.util.ArrayList;
import java.util.List;

/**
 * Created by ocsmen on 5/22/2017.
 */
public class Node {
    public int x;
    public int y;
    public int n;
    public int next;
    public List<Node> possibilities;
    public int value;

    public Node() {
        x = -1;
        y = -1;
        possibilities = new ArrayList<>();
    }
}
