import javax.swing.*;

/**
 * Class App (Controller)
 * @author Antoine Banchet
 * @version 0.0
 */

public class App extends JFrame{

    private Champ champ;
    private Gui gui;
    private int score;
    
    App() {
        super("Démineur");

        champ = new Champ();
        champ.init(0, 0);

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
        this.score = 0;
        champ.newPartie(indexLevel);
        gui.newPartie(indexLevel);
    }
}
