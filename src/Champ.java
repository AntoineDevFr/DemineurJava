import java.util.Random;

/**
 * Data
 * @author AntoineB
 * @version 0.0
 */

public class Champ {
    private boolean[][] champ;
   
    private int []  tabSize = {5, 10, 15};
    private int []  tabNbMines = {3, 7, 20};

    private int indexLevel = 0;
    Random random = new Random();
    
    public Champ(App app) {
    }

      /**
     * Initialize the field
     */
    public void init(int indexLevel) {
        this.indexLevel = indexLevel;
        champ = new boolean[tabSize[indexLevel]][tabSize[indexLevel]];
        for (int i = 0; i < tabNbMines[indexLevel]; i++) {
            int x = random.nextInt(champ.length);
            int y = random.nextInt(champ[0].length);
            champ[x][y] = true;
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

    public int getMineCount() {
        return tabNbMines[indexLevel];
    }

    /**
     * newPartie
     */
    public void newPartie(int indexLevel) {
        this.init(indexLevel);
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