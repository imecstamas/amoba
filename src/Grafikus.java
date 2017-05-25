
import javax.swing.*;
import java.awt.*;

public class Grafikus extends JFrame {

    interface ButtonClickListener {
        void onButtonClicked(int x, int y);
    }

    private PositionButton[][] matrixButtons;
    private ButtonClickListener buttonClickListener;

    public Grafikus(int dim) {
        matrixButtons = new PositionButton[dim][dim];
        setLayout(new GridLayout(dim, dim));
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                matrixButtons[i][j] = new PositionButton("", i, j);
                matrixButtons[i][j].addActionListener(e -> {
                    PositionButton p = (PositionButton) e.getSource();
                    buttonClickListener.onButtonClicked(p.x, p.y);
                });
                add(matrixButtons[i][j]);
            }
        }
        setTitle("Amoba");
        setBounds(200, 0, 600, 600);
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public void setButtonClickListener(ButtonClickListener buttonClickListener) {
        this.buttonClickListener = buttonClickListener;
    }

    public void setButtonValue(int x, int y, int player) {
        matrixButtons[x][y].setText(player == Amoba.PLAYER ? "X" : "O");
    }

    public void showWinner(int winner) {
        if (winner == Amoba.PLAYER) {
            JOptionPane.showMessageDialog(this, "A gyoztes: X");
        } else if (winner == Amoba.BOT) {
            JOptionPane.showMessageDialog(this, "A gyoztes: O");
        } else {
            JOptionPane.showMessageDialog(this, "A jatek dontetlen");
        }
    }
}
