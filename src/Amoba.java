/**
 * Created by ocsmen on 5/22/2017.
 */
public class Amoba {

    public static final int PLAYER = 1;
    public static final int BOT = 2;

    public static final int X = 1;
    public static final int O = 2;

    public static final long WIN = 200000;

    private int t[][];
    private int k;
    private Validator validator;
    public Grafikus grafikus;
    private Move move;
    private boolean first;

    public Amoba(int k, int j, boolean useAlphaBeta, String title) {
        this.k = k;
        t = new int[k][k];
        for (int i = 0; i < k; i++) {
            t[i] = new int[k];
            for (int jj = 0; jj < k; jj++)
                t[i][jj] = 0;
        }
        grafikus = new Grafikus(k, title);
        validator = new Validator(k, j, t);
        move = new Move(k, j, t, useAlphaBeta);
        first = true;
    }

    private boolean step(int x, int y, int player) {
        boolean success = false;
        if (validator.safeGet(x, y) == 0) {
            t[x][y] = player;
            success = true;
        }
        grafikus.setButtonValue(x, y, player);
        return success;
    }

    public boolean playerStep(int x, int y) {
        boolean success = step(x, y, PLAYER);
        if (success) {
            if (first) {
                move.search(k * k - 1, BOT);
                first = false;
            }
            move.next(x, y);
        }
        return success;
    }

    public void botStep() {
        if (first) {
            move.search(k * k, BOT);
            first = false;
        }
        Coord coord = move.next();
        step(coord.x, coord.y, BOT);
    }

    public boolean multiplayerStep(int x, int y, int player) {
        boolean success = false;
        if (validator.safeGet(x, y) == 0) {
            t[x][y] = player;
            success = true;
        }
        grafikus.setButtonValue(x, y, player);
        return success;
    }

    public boolean isOver() {
        return validator.isOver();
    }

    public int winner() {
        if (validator.isOver(BOT)) return BOT;
        else if (validator.isOver(PLAYER)) return PLAYER;
        else if (validator.isOver()) return 0;
        else return -1;
    }

    public void showTable() {
        for (int j = 0; j < k; j++) System.out.print("+--");
        System.out.println("+");
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < k; j++)
                if (t[i][j] == PLAYER) System.out.print("| X");
                else if (t[i][j] == BOT) System.out.print("| O");
                else System.out.print("|  ");
            System.out.println("|");
            for (int j = 0; j < k; j++) System.out.print("+--");
            System.out.println("+");
        }
    }

    public static void main(String[] args) {
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
//        int x, y;
//        Scanner scanner = new Scanner(System.in);
//        while (!amoba.isOver()) {
//            System.out.print("Add meg a lepesed: ");
//            do {
//                x = scanner.nextInt();
//                y = scanner.nextInt();
//            } while (!amoba.playerStep(x, y));
//            amoba.showTable();
//            if (!amoba.isOver()) {
//                amoba.botStep();
//                amoba.showTable();
//            }
//        }
//        System.out.println("A jatek veget ert, a gyoztes: " + amoba.winner());
    }
}
