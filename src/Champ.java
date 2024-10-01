import java.util.Random;

/**
 * Champ represents the game field containing mines and manages the game logic.
 * 
 * @author Antoine Banchet
 * @version 1.0
 */
public class Champ {
    private boolean[][] champ;
    private int customSize;
    private int customNbMines;

    private final int[] tabSize = {5, 10, 15, 0};  // Last element is for CUSTOM
    private final int[] tabNbMines = {3, 7, 20, 0}; // Last element is for CUSTOM

    private int indexLevel;
    private final Random random = new Random();

    public Champ(App app) {
        
    }

    /**
     * Initializes the field based on the selected level.
     */
    public void init(int indexLevel) {
        this.indexLevel = indexLevel;
        
        //updateCustomValues;
        tabSize[3] = customSize;
        tabNbMines[3] = customNbMines;

        champ = new boolean[tabSize[indexLevel]][tabSize[indexLevel]];
        placeMines();
    }

    /**
     * Places mines randomly on the field.
     */
    private void placeMines() {
        
        int minesPlaced = 0;
        while (minesPlaced < tabNbMines[indexLevel]) {
            int x = random.nextInt(champ.length);
            int y = random.nextInt(champ[0].length);
    
            if (!isMine(x, y)) {
                champ[x][y] = true;
                minesPlaced++;
            }
        }
    }
    
    /**
     * Displays the field for debugging purposes.
     */
    public void display() {
        for (int i = 0; i < champ.length; i++) {
            for (int j = 0; j < champ[i].length; j++) {
                System.out.print(isMine(i, j) ? "x" : nbMinesaround(i, j));
            }
            System.out.println();
        }
    }

    /**
     * Checks if a cell contains a mine.
     */
    public boolean isMine(int i, int j) {
        return champ[i][j];
    }

    /**
     * Counts the number of mines around a given cell.
     */
    public int nbMinesaround(int x, int y) {
        int count = 0;
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                if (i >= 0 && i < champ.length && j >= 0 && j < champ[0].length && champ[i][j]) {
                    count++;
                }
            }
        }
        return count;
    }

    public int getMineCount() {
        return tabNbMines[indexLevel];
    }

    /**
     * Starts a new game based on the selected level.
     */
    public void newPartie(int indexLevel) {
        this.init(indexLevel);
        display();
    }

    /**
     * Gets the width of the field.
     */
    public int getWidth() {
        return champ.length;
    }

    /**
     * Gets the height of the field.
     */
    public int getHeight() {
        return champ[0].length;
    }

    // Getters and setters for customSize and customNbMines
    public void setCustomSize(int size) {
        this.customSize = size;
    }

    public void setCustomNbMines(int nbMines) {
        this.customNbMines = nbMines;
    }

}