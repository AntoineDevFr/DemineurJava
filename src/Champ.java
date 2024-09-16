import java.util.Random;

/**
 * Data
 * @author AntoineB
 * @version 0.0
 */

public class Champ {
    
    private final static int DEF_WIDTH = 10;
    private final static int DEF_HEIGHT = 10;
    private final static int DEF_NBMINES = 8;

    private boolean[][] champ = new boolean[DEF_WIDTH][DEF_HEIGHT]; 
    private int []  tabSize = {5, 10, 15};
    private int []  tabNbMines = {3, 7, 9};

    Random random = new Random();
    
      /**
     * Initialize the field
     */
    public void init(int startX, int startY) {
        for (int n = DEF_NBMINES; n !=0;) {
            int x = random.nextInt(champ.length);
            int y = random.nextInt(champ[0].length);

            if(!champ[x][y] && (x != startX || y != startY)) {
                champ[x][y] = true;
                n--;
            }
        }
    }

    /**
     * Display all the field
     */
    public void display() {
        for (int i = 0; i < champ.length; i++) {
            for (int j = 0; j < champ[i].length; j++) {
                if(isMine(i, j)) {
                    System.out.print("x");
                } else {
                    System.out.print(nbMinesaround(i, j));
                }
                //System.out.print(champ[i][j] ? "x" : "o");
            }
            System.out.println();
        }
    }

    /**
     * @return is a Mine ?
     */
    public boolean isMine(int i, int j) {
        return champ[i][j];
    }

    /**
     * Calcul le nb de n
     */
    public int nbMinesaround(int x, int y) {
        int n = 0;
        for (int i = x-1; i <= x+1; i++) {
            for (int j = y-1; j <= y+1; j++) {
                if (i!=-1 && i!=champ.length && j != -1 && j != champ[0].length && champ[i][j]) {
                    n++;
                }
            }
        }
        return n;
    }

    /**
     * newPartie
     */
    public void newPartie(int indexLevel) {
        champ = new boolean[tabSize[indexLevel]][tabSize[indexLevel]];
        display();
    }

    /**
     * @return the width
     */
    public int getWidth() {
        return champ.length;
    }

    /**
     * @return the height
     */
    public int getHeight() {
        return champ[0].length;
    }
} 