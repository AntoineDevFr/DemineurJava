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
    public boolean online = false;
    public DataOutputStream out;
    public DataInputStream in;
    private Socket sock;

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

    /**
     * Connects to server
     */

    public void connect() {
        try {// ouverture de la socket et des streams

            sock = new Socket("localhost",1234);
            online = true;
            out =new DataOutputStream(sock.getOutputStream()); 
            in = new DataInputStream(sock.getInputStream());

            out.writeUTF("auth");
            out.writeUTF("Antoine"); // envoi d’un nom au serveur
            //Je demande la dimmension du champ
            int numJoueur = in.readInt(); // reception d’un nombre



            System.out.println("Joueur n°:"+numJoueur); 
            System.out.println("Connecté au serveur");

            initChampOnline();
            // in.close(); // fermeture Stream
            // out.close();
            // sock.close() ; // fermeture Socket
            
        } catch (UnknownHostException e) {
            System.out.println("R2D2 est inconnue");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initChampOnline() {
        try {
            out.writeUTF("init");
            int level = in.readInt();
            System.out.println("Level : " + level);
            gui.newPartie(level);
        } catch (UnknownHostException e) {
            System.out.println("Inconnue");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isMineOnline(int i, int j) {
        boolean response = false;
        try {
            out.writeUTF("isMine");
            out.writeUTF(String.valueOf(i));
            out.writeUTF(String.valueOf(j));
            response = in.readBoolean();
        } catch (UnknownHostException e) {
            System.out.println("Inconnue");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public int nbMinesaroundOnline(int i, int j) {
        int count = 0;
        try {
            out.writeUTF("nbMinesaround");
            out.writeUTF(String.valueOf(i));
            out.writeUTF(String.valueOf(j));
            count = in.readInt();
        } catch (UnknownHostException e) {
            System.out.println("Inconnue");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return count;
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
        if (!online)
        {
            gui.propagation(x, y);
        } else { // envoyer x et y au serveur
            try {
                out.writeUTF("cord");
                out.writeUTF(String.valueOf(x));
                out.writeUTF(String.valueOf(y));
            } catch (UnknownHostException e) {
                System.out.println("Inconnue");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
       
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