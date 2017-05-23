import java.util.ArrayList;
import java.util.List;

/**
 * Created by ocsmen on 5/22/2017.
 */
public class Move {
    private Node first;
    private Node actual;
    private int k;
    private int j;
    private int t[][];
    private Validator validator;
    private boolean useAlphaBeta;

    public Move(int k, int j, int t[][], boolean useAlphaBeta) {
        this.k = k;
        this.j = j;
        this.useAlphaBeta = useAlphaBeta;
        this.t = t;
        validator = new Validator(k, j, t);
    }

    void search(int depth, int player) {
        first = new Node();
        if (!useAlphaBeta) searching(first, k * k, player, depth);
        actual = first;
    }

    Coord next() {
        if (useAlphaBeta) {
            int n = 0;
            for (int i = 0; i < k; i++)
                for (int j = 0; j < k; j++)
                    if (t[i][j] == 0) n++;
            actual = new Node();
            alphabeta(actual, n, Amoba.BOT, 4, Integer.MAX_VALUE);
        }
        //printf("%d\n",actual.n);
        actual = actual.possibilities.get(actual.next);
        Coord coord = new Coord(actual.x, actual.y);
        t[coord.x][coord.y] = Amoba.BOT;
        System.out.println("BOT lepese: " + actual.x + " " + actual.y);
        return coord;
    }

    void next(int x, int y) {
        t[x][y] = Amoba.PLAYER;
        if (!useAlphaBeta) {
            for (int i = 0; i < actual.n; i++) {
                if (actual.possibilities.get(i).x == x && actual.possibilities.get(i).y == y) {
                    actual = actual.possibilities.get(i);
                }
            }
        }
    }

    //elvileg nem kell
    void show(Node node) {
        if (node.n != 0)
            for (int i = 0; i < node.n; i++) {
                show((node.possibilities.get(i)));
            }
    }

    //elvileg nem kell
    void show() {
        show(first);
    }

    int maximum(List<Node> p, int n) {
        int maxIndex = 0;
        for (int i = 1; i < n; i++)
            if (p.get(i).value > p.get(maxIndex).value) maxIndex = i;
        return maxIndex;
    }

    //n-et ki lehet szurni, veszem az arraylist.length-et
    int minimum(List<Node> p, int n) {
        int minIndex = 0;
        for (int i = 1; i < n; i++)
            //possibility i-dik elemenek a value-ja
            if (p.get(i).value < p.get(minIndex).value) minIndex = i;
        return minIndex;
    }

    //alfabeta nelkul keres (bejarja az egesz fat)
    void searching(Node node, int n, int player, int depth) {
        if (depth >= 0) {
            //depth - hany szint van meg
            if (depth == 0 || validator.isOver()) {
                node.n = 0;
                if (validator.isOver(Amoba.BOT)) node.value = 10;
                else if (validator.isOver(Amoba.PLAYER)) node.value = -10;
                else node.value = 0;
            } else {
                node.n = n;
                node.possibilities = new ArrayList<>(n);
                int l = 0;
                for (int i = 0; i < k; i++)
                    for (int j = 0; j < k; j++) {
                        if (t[i][j] == 0) {
                            t[i][j] = player;
                            node.possibilities.get(l).x = i;
                            node.possibilities.get(l).y = j;
                            //csak atadom az objektumot node.possibilities.get(l)
                            //3-player . ellenkezo jatekos . Amoba.BOT.PLAYER, PLAYER.Amoba.BOT
                            searching((node.possibilities.get(l)), n - 1, 3 - player, depth - 1);
                            t[i][j] = 0;
                            l++;
                        }
                    }
                if (player == Amoba.BOT)
                    node.next = maximum(node.possibilities, node.n);
                else
                    node.next = minimum(node.possibilities, node.n);
                node.value = node.possibilities.get(node.next).value;
            }
        }
    }

    int max(int x, int y) {
        if (x > y) return x;
        else return y;
    }

    int min(int x, int y) {
        if (x < y) return x;
        else return y;
    }

    void alphabeta(Node node, int n, int player, int depth, int ab) {
        if (depth >= 0) {
            if (depth == 0 || validator.isOver()) {
                //printf("end %d\n",validator.isOver());
                node.n = 0;
                node.value = validator.heuristicValue(Amoba.BOT);
                if (node.value == Amoba.WIN) node.value *= (depth - 1);
                //printf("end - %d,%d\n",node.x,node.y);
                //printf("end %d - %d\n",node.value,validator.isOver());
            } else {
                node.n = n;
                node.possibilities = new ArrayList<>(n);

                boolean cut = false;
                if (player == Amoba.BOT) {
                    //printf("max - %d,%d\n",node.x,node.y);
                    int v = Amoba.NINF;
                    int index = 0;
                    //osszevonni a for-okat Amoba.BOT es PLAYER eseten (if player == Amoba.BOT-ot bevinni a for-ba)
                    for (int i = 0; i < k && !cut; i++)
                        for (int j = 0; j < k && !cut; j++)
                            if (t[i][j] == 0) {
                                t[i][j] = player;
                                Node possible = new Node();
                                possible.x = i;
                                possible.y = j;
                                //BOT agon vagyunk, atadhatjuk a PLAYER-t a 3-player eseten
                                alphabeta((possible), n - 1, 3 - player, depth - 1, v);
                                if (possible.value > v) index = node.possibilities.size();
                                v = max(v, possible.value);
                                t[i][j] = 0;
                                node.possibilities.add(possible);
                                if (ab <= v) cut = true;
                            }
                    node.value = v;
                    node.next = index;
                    //printf("max - %d %d\n",v,index);
                } else {
                    //printf("min - %d,%d\n",node.x,node.y);
                    int v = Integer.MAX_VALUE;
                    int index = 0;
                    for (int i = 0; i < k && !cut; i++)
                        for (int j = 0; j < k && !cut; j++)
                            if (t[i][j] == 0) {
                                t[i][j] = player;
                                Node possible = new Node();
                                possible.x = i;
                                possible.y = j;
                                alphabeta((possible), n - 1, 3 - player, depth - 1, v);
                                if (possible.value < v) index = node.possibilities.size();
                                v = min(v, possible.value);
                                t[i][j] = 0;
                                node.possibilities.add(possible);
                                if (ab >= v) cut = true;
                            }
                    node.value = v;
                    node.next = index;
                    //printf("min - %d %d\n",v,index);
                }
            }
        }
    }
}
