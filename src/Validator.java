/**
 * Created by ocsmen on 5/22/2017.
 */
public class Validator {
    private int k;
    private int j;
    private int t[][];

    public Validator(int k, int j, int[][] t) {
        this.k = k;
        this.j = j;
        this.t = t;
    }

    int valueByLength(int length){
        int v = 1;
        for(int i = 1; i < length; i++) v *= 8;
        return v;
    }

    int heuristicValueOnePlayer(int player){
        int value = 0;
        int sx[] = {-1,0,1,1};
        int sy[] = { 1,1,1,0};
        for(int i = 0; i < k && value < Amoba.WIN; i++){
            for(int j = 0; j < k && value < Amoba.WIN; j++)
                if (t[i][j] == player){
                    for(int c = 0; c < 4; c++){
                        //l - length
                        int l = search(i,j,sx[c],sy[c]);
                        int lock1 = safeGet(i-sx[c],j-sy[c]);
                        int lock2 = safeGet(i+l*sx[c],j+l*sy[c]);
                        if (l == j) value = Amoba.WIN;
                        else if (lock1 == 0 && lock2 == 0) value += valueByLength(l);
                        else if (lock1 != 0 && lock2 == 0) value += valueByLength(l)/2;
                        else if (lock1 == 0 && lock2 != 0) value += valueByLength(l)/2;
                    }
                }
        }
        return value;
    }

    boolean safeCoord(int c){
        return c >= 0 && c < k;
    }

    int safeGet(int x,int y){
        if (safeCoord(x) && safeCoord(y))
            return t[x][y];
        else
            return -1;
    }

    //adott iranyba (sx, sy) (x,y) pontbol megnezi milyen hosszu megoldas huzhato
    int search(int x,int y,int sx,int sy){
        int l = 0;
        int v = t[x][y];
        //megnezi az ellenkezo iranyt: ha egyezik a jelenlegi ponttal, nem szamolja tovabb, mert azt majd szamolja a kovetkezo pontbol
        if (safeGet(x - sx, y - sy) != v){
            while (l <= j && safeGet(x,y) == v){
                x += sx;
                y += sy;
                l++;
            }
        }

        return l;
    }

    //megnezi hogy ebbol a pontbol meg van-e az elegendo ertek (j)
    boolean goodLine(int x,int y,int sx,int sy){
        return search(x,y,sx,sy) == j;
    }

    //megnezi a play nyert-e
    boolean isOver(int player){
        boolean isOver = false;
        for(int i = 0; i < k && !isOver; i++)
            for(int j = 0; j < k && !isOver; j++){
                if (t[i][j] == player){
                    isOver = goodLine(i,j,-1,1) || goodLine(i,j,0,1) || goodLine(i,j,1,1) || goodLine(i,j,1,0);
                }
            }

        return isOver;
    }

    boolean isOver(){
        return isOver(Amoba.PLAYER) || isOver(Amoba.BOT);
    }


    int heuristicValue(int player){
        int hPlayer = heuristicValueOnePlayer(player);
        int hPlayer2 = heuristicValueOnePlayer(3 - player);
        if (hPlayer == Amoba.WIN && hPlayer2 != Amoba.WIN) return Amoba.WIN;
        else return hPlayer - hPlayer2;
    }
}
