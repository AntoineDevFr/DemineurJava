import javax.swing.*;
import java.io.* ;

/**
 * Class App (Controller)
 * Manages the main application flow and interaction with the GUI.
 * 
 * @author Antoine Banchet
 * @version 1.0
 */
public class App extends JFrame  {

    private Champ champ;
    public Gui gui;
    public NetworkManager networkManager;
    public boolean online = false;

    public App() {
        super("Minesweeper");
        initializeGame();
        configureWindow();
    }

    private void initializeGame() {
        champ = new Champ();
        champ.init(Level.EASY.ordinal());
        champ.display();
        gui = new Gui(this, champ);
    }

    private void configureWindow() {
        setContentPane(gui);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        pack();
        setVisible(true);
    }

    public static void main(String[] args) {
        new App();
    }

    public void newPartie(int indexLevel) {
        if (indexLevel == Level.CUSTOM.ordinal()) {
            promptCustomLevelSettings();
        }
        champ.newPartie(indexLevel);
        gui.newPartie(indexLevel);
    }

    private void promptCustomLevelSettings() {
        String sizeInput = JOptionPane.showInputDialog("Enter the size of the field");
        String minesInput = JOptionPane.showInputDialog("Enter the number of mines");
        try {
            champ.setCustomSize(Integer.parseInt(sizeInput));
            champ.setCustomNbMines(Integer.parseInt(minesInput));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid input. Please enter numeric values.");
        }
    }

    public void gameOver() {
        gui.revealedCases();
        gui.stopTimer();
        showGameOverDialog();
    }

    private void showGameOverDialog() {
        Icon gameOverIcon = new ImageIcon("./src/resources/game-over.png");
        if (online) {
            JOptionPane.showMessageDialog(this, "You Lose ! You have to wait the end of game", "Game Over", JOptionPane.INFORMATION_MESSAGE, gameOverIcon);
        } else {
            int response = JOptionPane.showOptionDialog(
                null,
                "You Lose ! Would you like to play again or quit?",
                "Game Over",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                gameOverIcon,
                new Object[]{"Play Again", "Quit"},
                "Play Again"
            );
            if (response == JOptionPane.YES_OPTION) {
                newPartie(gui.getLevelComboBox().getSelectedIndex());
            } else {
                quit();
            }
        }
    }

    public void propagation(int x, int y) {
        if (!online)
        {
            gui.propagation(x, y);
        } else { // envoyer x et y au serveur
            networkManager.sendMoveOnline(x, y);
            }
    }

    public void revealCaseOnline(int x, int y) {
        gui.revealCase(x, y);
    }

    public void winGame() {
        gui.revealedCases();
        gui.stopTimer();
        showWinDialog();
    }

    private void showWinDialog() {
        Icon winIcon = new ImageIcon("./src/resources/you-win.png");
        int response = JOptionPane.showOptionDialog(
            this,
            "Congratulations! You WIN with a score of " + gui.getScore() + " sec! Would you like to play again or quit?",
            "You Win!",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            winIcon,
            new Object[]{"Play Again", "Quit"},
            "Play Again"
        );

        if (response == JOptionPane.YES_OPTION) {
            newPartie(gui.getLevelComboBox().getSelectedIndex());
        } else {
            quit();
        }
    }

    public Champ getChamp() {
        return champ;
    }

    public int getCurrentLevel() {
        return gui.getLevelComboBox().getSelectedIndex();
    }

      /**
     * Quit the application.
     */
    public void quit() {
        if (online) {
            networkManager.exit();
        }
        System.exit(0);
    }

    public void connect() {
        try {
            networkManager = new NetworkManager(this);
            networkManager.connect("localhost", 1234);;
            networkManager.auth("Antoine");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}