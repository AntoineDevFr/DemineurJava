import javax.swing.*;
import java.io.*;

/**
 * Class App (Controller)
 * Manages the main application flow and interaction with the GUI.
 * 
 * @author Antoine Banchet
 * @version 1.0
 */
public class App extends JFrame {

    // Déclare les objets principaux pour le jeu : champ (terrain de jeu), gui (interface utilisateur), et networkManager (gestion du réseau)
    private Champ champ;
    public Gui gui;
    public NetworkManager networkManager;
    
    // Indique si le jeu est en mode en ligne ou hors ligne
    public boolean online = false;
    public boolean isWaiting = false; // Indique si le joueur attend un adversaire
    public String playerName; // Nom du joueur

    // Constructeur principal de l'application, qui initialise le jeu et configure la fenêtre GUI
    public App() {
        super("Minesweeper");
        initializeGame(); // Initialise le champ de mines et l'interface
        configureWindow(); // Configure les propriétés de la fenêtre (taille, position, etc.)
    }

    // Méthode privée pour initialiser le jeu
    private void initializeGame() {
        champ = new Champ(); // Crée un nouvel objet Champ (terrain de jeu)
        champ.init(Level.EASY.ordinal()); // Initialise le jeu au niveau facile
        champ.display(); // Affiche le terrain de jeu
        gui = new Gui(this, champ); // Associe l'interface utilisateur avec le champ de jeu
    }

    // Méthode privée pour configurer la fenêtre de l'application
    private void configureWindow() {
        setContentPane(gui); // Associe l'interface utilisateur à la fenêtre
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Ferme l'application quand on clique sur la croix
        setSize(800, 600); // Définit la taille de la fenêtre
        setLocationRelativeTo(null); // Centre la fenêtre sur l'écran
        pack(); // Ajuste la taille des composants
        setVisible(true); // Rend la fenêtre visible
    }

    // Méthode principale qui lance l'application
    public static void main(String[] args) {
        new App(); // Crée et lance une nouvelle instance de l'application
    }

    // Démarre une nouvelle partie en fonction du niveau sélectionné
    public void newPartie(int indexLevel) {
        if (indexLevel == Level.CUSTOM.ordinal()) { // Si un niveau personnalisé est sélectionné
            promptCustomLevelSettings(); // Affiche un dialogue pour choisir les paramètres du niveau personnalisé
        }
        champ.newPartie(indexLevel); // Redémarre le champ de jeu au niveau sélectionné
        gui.newPartie(indexLevel); // Réinitialise l'interface utilisateur pour la nouvelle partie
    }

    // Méthode pour demander à l'utilisateur de définir un niveau personnalisé
    private void promptCustomLevelSettings() {
        String sizeInput = JOptionPane.showInputDialog("Enter the size of the field"); // Demande la taille du terrain
        String minesInput = JOptionPane.showInputDialog("Enter the number of mines"); // Demande le nombre de mines
        try {
            champ.setCustomSize(Integer.parseInt(sizeInput)); // Définit la taille du terrain
            champ.setCustomNbMines(Integer.parseInt(minesInput)); // Définit le nombre de mines
            gui.setCustomSize(Integer.parseInt(sizeInput)); // Met à jour l'interface utilisateur avec la nouvelle taille
            gui.setCustomNbMines(Integer.parseInt(minesInput)); // Met à jour l'interface utilisateur avec le nouveau nombre de mines
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid input. Please enter numeric values."); // Gère les erreurs de saisie non numérique
        }
    }

    // Gère la fin de la partie (défaite)
    public void gameOver() {
        gui.revealedCases(); // Révèle toutes les cases
        gui.stopTimer(); // Arrête le chronomètre
        showGameOverDialog(); // Affiche une boîte de dialogue de fin de jeu
    }

    // Affiche un dialogue de fin de partie, propose de rejouer ou de quitter
    private void showGameOverDialog() {
        Icon gameOverIcon = new ImageIcon("./src/resources/game-over.png"); // Icone affichée lors de la défaite
        int response = JOptionPane.showOptionDialog(
            null,
            "You Lose! Would you like to play again or quit?", // Message affiché
            "Game Over",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            gameOverIcon,
            new Object[]{"Play Again", "Quit"},
            "Play Again"
        );
        if (response == JOptionPane.YES_OPTION) {
            if (online) { // Si en mode en ligne
                networkManager.newGame(); // Lance une nouvelle partie en ligne
                gui.hideCases(); // Cache les cases
                gui.disableCases(); // Désactive les interactions avec les cases en attendant l'adversaire
                isWaiting = true;
            } else {
                newPartie(gui.getLevelComboBox().getSelectedIndex()); // Redémarre une partie hors ligne
            }
        } else {
            quit(); // Quitte l'application
        }
    }

    // Propage une action (clic sur une case) localement ou en ligne
    public void propagation(int x, int y) {
        if (!online) {
            gui.propagation(x, y); // Propagation locale
        } else { // Envoi des coordonnées au serveur
            if (!gui.champCases[x][y].getIsMine() && gui.champCases[x][y].getIsFill()) {
                gui.scoreOnline++; // Augmente le score en ligne
                gui.updateScoreValue(); // Met à jour l'affichage du score
                networkManager.sendMoveOnline(x, y); // Envoie le coup au serveur
            }
        }
    }

    // Révèle une case en ligne
    public synchronized void revealCaseOnline(int x, int y) {
        gui.revealCase(x, y); // Révèle la case à la position (x, y)
    }

    // Gère la victoire du joueur
    public void winGame() {
        if (online) {
            gui.revealedCases(); // Révèle toutes les cases
            Icon winIcon = new ImageIcon("./src/resources/you-win.png");
            int response = JOptionPane.showOptionDialog(
            this,
            "End of the game, you finished with " + gui.scoreOnline + " points! Would you like to play again or quit?",
            "End of the game",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            winIcon,
            new Object[]{"Play Again", "Quit"},
            "Play Again"
        );

        if (response == JOptionPane.YES_OPTION) {
            networkManager.newGame(); // Relance une nouvelle partie en ligne
            gui.hideCases(); // Cache les cases
            gui.disableCases(); // Désactive les interactions en attendant un autre joueur
            isWaiting = true;
        } else {
            quit(); // Quitte l'application
        }

        } else {
            gui.revealedCases(); // Révèle toutes les cases
            gui.stopTimer(); // Arrête le chronomètre
            showWinDialog(); // Affiche un dialogue de victoire
        }
    }

    // Affiche une boîte de dialogue de victoire (hors ligne)
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
            newPartie(gui.getLevelComboBox().getSelectedIndex()); // Démarre une nouvelle partie
        } else {
            quit(); // Quitte l'application
        }
    }

    // Getter pour récupérer le champ de mines
    public Champ getChamp() {
        return champ;
    }

    // Récupère le niveau actuel sélectionné dans l'interface utilisateur
    public int getCurrentLevel() {
        return gui.getLevelComboBox().getSelectedIndex();
    }

    // Quitte l'application, avec une gestion spéciale si en mode en ligne
    public void quit() {
        if (online) {
            networkManager.exit(); // Ferme la connexion réseau
        }
        System.exit(0); // Ferme l'application
    }

    // Méthode pour se connecter à un serveur en ligne
    public void connect() {
        try {
            // Demande à l'utilisateur de saisir un nom de joueur
            playerName = JOptionPane.showInputDialog(null, "Entrez votre nom de joueur :", "Nom du joueur", JOptionPane.QUESTION_MESSAGE);

            if (playerName != null && !playerName.trim().isEmpty()) {
                networkManager = new NetworkManager(this); // Initialise le gestionnaire réseau
                networkManager.connect("localhost", 1234); // Se connecte au serveur local
                networkManager.auth(playerName); // Envoie le nom du joueur au serveur pour authentification
            } else {
                System.out.println("Le nom du joueur est invalide."); // Gestion d'un nom vide ou invalide
            }
        } catch (IOException e) {
            e.printStackTrace(); // Gère les exceptions liées à la connexion réseau
        }
    }
}
