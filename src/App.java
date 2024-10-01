import javax.swing.*;
import java.net.* ;
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
    private Gui gui;

    public App() {
        super("Minesweeper");
        initializeGame();
        configureWindow();
    }

    private void initializeGame() {
        champ = new Champ(this);
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

    /**
     * Connects to server
     */

    public void connect() {
        try {// ouverture de la socket et des streams

            Socket sock = new Socket("localhost",1234);
            DataOutputStream out =new DataOutputStream(sock.getOutputStream()); 
            DataInputStream in = new DataInputStream(sock.getInputStream());

            out.writeUTF("Antoine"); // envoi d’un nom au serveur
            int numJoueur = in.readInt(); // reception d’un nombre
             
            System.out.println("Joueur n°:"+numJoueur); 
            in.close(); // fermeture Stream
            out.close();
            sock.close() ; // fermeture Socket
            
        } catch (UnknownHostException e) {
            System.out.println("R2D2 est inconnue");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Quit the application.
     */
    public void quit() {
        System.exit(0);
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

    public void propagation(int x, int y) {
        gui.propagation(x, y);
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
}