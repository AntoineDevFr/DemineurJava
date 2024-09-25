import javax.swing.*;

/**
 * Class App (Controller)
 * @author Antoine Banchet
 * @version 0.0
 */

public class App extends JFrame{

    private Champ champ;
    private Gui gui;
    //private int score;
    
    App() {
        super("Démineur");

        champ = new Champ(this);
        champ.init(Level.EASY.ordinal());
        champ.display();

        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600); // Set the size of the window
        setLocationRelativeTo(null);
        
        gui = new Gui(this, champ);
        setContentPane(gui);
        
        pack();
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public static void main(String[] args) throws Exception {
        new App();
    }

    /**
     * Quit the application
     */
    public void quit() {
        System.exit(0);
    }

    public void newPartie(int indexLevel) {
        //this.score = 0;
        champ.newPartie(indexLevel);
        gui.newPartie(indexLevel);
    }

    public void gameOver() {
        gui.gameover(); 
        Icon gameOverIcon = new ImageIcon("./src/game-over.png");

        int response = JOptionPane.showOptionDialog(
                null,
                "Game Over! Would you like to play again or quit?",
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
        gui.propagation(x,y);
    }
}
