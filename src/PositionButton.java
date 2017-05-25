import javax.swing.*;

/**
 * Created by tamas on 24/05/2017.
 */
public class PositionButton extends JButton {

    public PositionButton(String text, int x, int y) {
        super(text);
        this.x = x;
        this.y = y;
    }

    public int x, y;

}
