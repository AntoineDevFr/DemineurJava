import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Graphical User Interface (View)
 * Manages the presentation layer of the game.
 * 
 * @author Antoine Banchet
 * @version 1.0
 */
public class Gui extends JPanel implements ActionListener {

    // Déclaration des composants de l'interface graphique
    private JButton buttonQuit, buttonNew; // Boutons pour quitter ou démarrer une nouvelle partie
    JMenu menu; // Menu principal de l'application
    private JMenuItem mQuitter, mNewPartie, mConnexion; // Options dans le menu (Quitter, Nouvelle partie, Connexion)
    private JComboBox<Level> levelComboBox; // ComboBox pour sélectionner le niveau de difficulté
    private JPanel panelNorth, panelSouth, panelWest; // Panneaux pour organiser la mise en page
    private JLabel levLabel, player; // Labels pour afficher des informations (niveau, joueur)
    public JDialog waitingDialog; // Fenêtre de dialogue pour l'attente d'autres joueurs
    private int indexLevel; // Niveau de difficulté sélectionné
    public boolean startgame = false; // Indicateur si la partie a commencé
    private boolean namePlayerOnScreen = false; // Indicateur si le nom du joueur est affiché à l'écran

    private int customSize; // Taille personnalisée du champ de mines
    private int customNbMines; // Nombre personnalisé de mines
    private final int[] tabSize = {5, 10, 15, 0};  // Tableau des tailles en fonction du niveau
    private final int[] tabNbMines = {3, 7, 20, 0}; // Tableau du nombre de mines en fonction du niveau

    private App app; // Référence à l'application principale
    private Champ champ; // Champ de jeu (terrain)

    public int score; // Score du joueur
    public int scoreOnline = 0; // Score en ligne
    private Compteur compteur; // Chronomètre pour suivre le temps écoulé

    private int totalNonMineCases; // Nombre total de cases sans mines
    private int revealedCases; // Nombre de cases révélées

    private JLabel scoreValue; // Label pour afficher le score
    private JPanel panelMines = new JPanel(); // Panneau pour le champ de mines
    public Case[][] champCases; // Tableau des cases du champ de mines

    // Constructeur de la classe Gui
    public Gui(App app, Champ champ) {
        this.app = app;
        this.champ = champ;

        this.compteur = new Compteur(this); // Initialisation du chronomètre
        this.scoreValue = new JLabel(String.valueOf(score)); // Initialisation du label pour afficher le score
        compteur.start(); // Démarrer le chronomètre

        setLayout(new BorderLayout()); // Définir la mise en page en BorderLayout
        initializePanels(); // Initialisation des différents panneaux
        initializeMenu(); // Initialisation du menu de l'application
    }

    // Met à jour l'affichage du score, en tenant compte du mode en ligne ou hors ligne
    public void updateScoreValue() {
        if (app.online) {
            scoreValue.setText(String.valueOf(scoreOnline));
        } else {
            scoreValue.setText(String.valueOf(score));
        }
    }

    // Initialisation des panneaux (Nord, Sud, Ouest, Centre)
    private void initializePanels() {
        initializeMinesPanel(); // Initialiser le panneau du champ de mines
        initializeNorthPanel(); // Initialiser le panneau Nord (score, niveau)
        initializeSouthPanel(); // Initialiser le panneau Sud (boutons Quitter, Nouvelle partie)
        initializeWestPanel(); // Initialiser le panneau Ouest (joueurs connectés)
    }

    // Initialisation du panneau central (champ de mines)
    private void initializeMinesPanel() {
        updateMinesPanel(Level.EASY.ordinal()); // Initialiser le champ avec le niveau Facile par défaut
        add(panelMines, BorderLayout.CENTER); // Ajouter le panneau au centre de la fenêtre
    }

    // Initialisation du panneau Nord (score, niveau)
    private void initializeNorthPanel() {
        panelNorth = new JPanel(); // Créer un panneau
        panelNorth.setBackground(new Color(60, 63, 65)); // Définir la couleur d'arrière-plan

        JLabel scoreLabel = new JLabel("Score : "); // Label pour le score
        scoreLabel.setForeground(Color.WHITE); // Couleur du texte
        levLabel = new JLabel("Level : "); // Label pour le niveau
        levLabel.setForeground(Color.WHITE);
        scoreValue.setForeground(Color.CYAN); // Couleur du texte pour le score

        // Ajouter les éléments au panneau Nord
        panelNorth.add(scoreLabel);
        panelNorth.add(scoreValue);
        panelNorth.add(levLabel);

        // ComboBox pour sélectionner le niveau
        levelComboBox = new JComboBox<>(Level.values());
        levelComboBox.addActionListener(this); // Ajouter un écouteur d'événements pour les changements de niveau
        panelNorth.add(levelComboBox);

        add(panelNorth, BorderLayout.NORTH); // Ajouter le panneau Nord à la fenêtre
    }

    // Initialisation du panneau Sud (boutons Quitter, Nouvelle partie)
    private void initializeSouthPanel() {
        panelSouth = new JPanel(); // Créer un panneau
        panelSouth.setBackground(new Color(43, 43, 43)); // Définir la couleur d'arrière-plan

        // Bouton Quitter
        buttonQuit = new JButton("Quit");
        buttonQuit.setBackground(new Color(255, 69, 58)); // Couleur de fond du bouton
        buttonQuit.addActionListener(this); // Ajouter un écouteur d'événements pour gérer le clic
        panelSouth.add(buttonQuit); // Ajouter le bouton au panneau Sud

        // Bouton Nouvelle partie
        buttonNew = new JButton("New");
        buttonNew.setBackground(new Color(50, 205, 50)); // Couleur de fond du bouton
        buttonNew.addActionListener(this); // Ajouter un écouteur d'événements pour gérer le clic
        panelSouth.add(buttonNew); // Ajouter le bouton au panneau Sud

        add(panelSouth, BorderLayout.SOUTH); // Ajouter le panneau Sud à la fenêtre
    }

    // Initialisation du panneau Ouest (joueurs connectés)
    private void initializeWestPanel() {
        panelWest = new JPanel(); // Créer un panneau
        panelWest.setBackground(new Color(43, 43, 43)); // Définir la couleur d'arrière-plan
        panelWest.setLayout(new BoxLayout(panelWest, BoxLayout.Y_AXIS)); // Définir la disposition en colonne
        JLabel labelUsers = new JLabel("Users connected : "); // Label pour afficher les joueurs connectés
        labelUsers.setForeground(Color.WHITE); // Couleur du texte
        panelWest.add(labelUsers); // Ajouter le label au panneau Ouest
        JLabel space = new JLabel(" "); // Espacement supplémentaire
        panelWest.add(space);
        add(panelWest, BorderLayout.WEST); // Ajouter le panneau Ouest à la fenêtre
    }

    // Ajoute les noms des joueurs au panneau Ouest
    public void addPlayers(String[] players) {
        for (String player : players) {
            JLabel playerLabel = new JLabel(player); // Crée un label pour chaque joueur
            playerLabel.setForeground(Color.WHITE); // Couleur du texte
            panelWest.add(playerLabel); // Ajoute le label au panneau Ouest
        }
        panelWest.revalidate();  // Réactualise le panneau après l'ajout des joueurs
        panelWest.repaint();     // Redessine le panneau pour afficher les changements
    }

    // Initialisation du menu de l'application
    private void initializeMenu() {
        JMenuBar menuBar = new JMenuBar(); // Créer une barre de menu
        menu = new JMenu("Options"); // Créer un menu "Options"
        menuBar.add(menu); // Ajouter le menu à la barre de menu

        mNewPartie = new JMenuItem("New Game"); // Option pour démarrer une nouvelle partie
        menu.add(mNewPartie); // Ajouter l'option au menu
        mNewPartie.addActionListener(this); // Ajouter un écouteur d'événements

        mQuitter = new JMenuItem("Quit"); // Option pour quitter l'application
        menu.add(mQuitter); // Ajouter l'option au menu
        mQuitter.addActionListener(this); // Ajouter un écouteur d'événements

        mConnexion = new JMenuItem("Connect"); // Option pour se connecter
        menu.add(mConnexion); // Ajouter l'option au menu
        mConnexion.addActionListener(this); // Ajouter un écouteur d'événements

        app.setJMenuBar(menuBar); // Définir la barre de menu dans l'application
    }

    // Démarre une nouvelle partie en fonction du niveau sélectionné
    public void newPartie(int indexLevel) {
        tabSize[3] = customSize; // Mettre à jour la taille pour le niveau personnalisé
        tabNbMines[3] = customNbMines; // Mettre à jour le nombre de mines pour le niveau personnalisé
        app.isWaiting = false;

        compteur.stop(); // Arrêter le chronomètre
        compteur.reset(); // Réinitialiser le chronomètre
        compteur.start(); // Redémarrer le chronomètre
        scoreOnline = 0; // Réinitialiser le score en ligne
        updateScoreValue(); // Mettre à jour l'affichage du score
        revealedCases = 0; // Réinitialiser le nombre de cases révélées
        updateMinesPanel(indexLevel); // Mettre à jour le champ de mines en fonction du niveau sélectionné
        app.pack(); // Redimensionner la fenêtre

        // Si le mode en ligne est activé
        if (app.online) {
            app.networkManager.startListening(); // Démarrer l'écoute des messages réseau
            panelNorth.remove(levelComboBox); // Retirer la ComboBox des niveaux de l'interface
            panelNorth.remove(levLabel); // Retirer le label du niveau
            panelSouth.remove(buttonNew); // Retirer le bouton "Nouvelle partie"
            menu.remove(mConnexion); // Retirer l'option de connexion du menu

            if (!namePlayerOnScreen) { // Si le nom du joueur n'est pas encore affiché
                levLabel = new JLabel("Player :  "); // Afficher le label "Player"
                levLabel.setForeground(Color.WHITE);
                panelNorth.add(levLabel);
                player = new JLabel(app.playerName); // Afficher le nom du joueur
                player.setForeground(Color.WHITE);
                panelNorth.add(player);
                namePlayerOnScreen = true; // Indiquer que le nom du joueur est affiché
            }

            // Afficher une boîte de dialogue d'attente pour les autres joueurs
            waitingDialog = new JDialog(app, "Waitingq for Players", true);
            waitingDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            waitingDialog.setSize(200, 100); // Taille de la boîte de dialogue
            waitingDialog.setLocationRelativeTo(app); // Centrer la boîte de dialogue

            JLabel waitingLabel = new JLabel("Waiting for other players ...", SwingConstants.CENTER);
            waitingDialog.add(waitingLabel); // Ajouter un label dans la boîte de dialogue
            System.out.println("J'envoie start");
            app.networkManager.sendStart(); // Envoyer un message au serveur pour démarrer la partie

            // Ajout d'un écouteur d'événements pour gérer la fermeture de la boîte de dialogue
            waitingDialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    // Si la boîte de dialogue est fermée, repasser en mode solo
                    app.online = false; // Désactiver le mode en ligne
                    app.networkManager.exit(); // Quitter la connexion réseau
                    startgame = true; // Sortir de la boucle d'attente
                    panelNorth.remove(levLabel); // Réinitialiser les éléments de l'interface
                    panelNorth.remove(player);
                    levLabel = new JLabel("Level : ");
                    levLabel.setForeground(Color.WHITE);
                    panelNorth.add(levLabel);
                    panelNorth.add(levelComboBox);
                    panelSouth.add(buttonNew);
                    menu.add(mConnexion);
                    compteur.stop(); // Réinitialiser le chronomètre
                    compteur.reset();
                    compteur.start();
                    revealedCases = 0;
                    namePlayerOnScreen = false;
                }
            });

            // Afficher la boîte de dialogue d'attente
            waitingDialog.setVisible(true);

            // Masquer la boîte de dialogue lorsque la partie commence
            waitingDialog.dispose();
        }
    }

    // Arrêter le chronomètre
    public void stopTimer() {
        compteur.stop();
    }

    // Obtenir le score actuel
    public int getScore() {
        return score;
    }

    // Mettre à jour le panneau des mines en fonction du niveau sélectionné
    public void updateMinesPanel(int indexLevel) {
        this.indexLevel = indexLevel; // Mise à jour de l'indice du niveau
        int sizeNewChamp = tabSize[indexLevel]; // Taille du nouveau champ
        champCases = new Case[sizeNewChamp][sizeNewChamp]; // Créer un tableau de cases

        panelMines.removeAll(); // Supprimer les éléments actuels du panneau des mines
        panelMines.setLayout(new GridLayout(sizeNewChamp, sizeNewChamp)); // Disposer les cases en grille
        panelMines.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Ajouter une bordure

        // Initialisation des cases du champ de mines
        for (int i = 0; i < sizeNewChamp; i++) {
            for (int j = 0; j < sizeNewChamp; j++) {
                champCases[i][j] = createCasePanel(i, j); // Créer une case
                champCases[i][j].clickEnabler = true; // Activer les clics sur les cases
                panelMines.add(champCases[i][j]); // Ajouter la case au panneau des mines
            }
        }

        panelMines.setBackground(Color.LIGHT_GRAY); // Définir la couleur de fond du panneau
        panelMines.repaint(); // Redessiner le panneau

        totalNonMineCases = sizeNewChamp * sizeNewChamp - tabNbMines[indexLevel]; // Calculer le nombre de cases sans mines
    }

    // Créer une case du champ de mines en fonction des coordonnées (i, j)
    private Case createCasePanel(int i, int j) {
        Case casePanel = new Case(app); // Créer une nouvelle case
        casePanel.setCoordinates(i, j); // Définir les coordonnées de la case

        // Si le mode en ligne n'est pas activé
        if (!app.online) {
            if (champ.isMine(i, j)) {
                casePanel.setMine(true); // Si la case est une mine
            } else {
                int minesAround = champ.nbMinesaround(i, j); // Nombre de mines autour de la case
                if (minesAround != 0) {
                    casePanel.setNbMinesAround(String.valueOf(minesAround)); // Définir le nombre de mines autour
                }
            }
        } else {
            // Si le mode en ligne est activé, demander les informations au serveur
            if (app.networkManager.isMineOnline(i, j)) {
                casePanel.setMine(true); // Si la case est une mine
            } else {
                int minesAround = app.networkManager.nbMinesaroundOnline(i, j); // Nombre de mines autour de la case
                if (minesAround != 0) {
                    casePanel.setNbMinesAround(String.valueOf(minesAround)); // Définir le nombre de mines autour
                }
            }
        }
        return casePanel; // Retourner la case créée
    }

    // Affiche les informations du champ (pour débogage)
    public void displayChampInfo() {
        for (int i = 0; i < tabSize[indexLevel]; i++) {
            for (int j = 0; j < tabSize[indexLevel]; j++) {
                Case currentCase = champCases[i][j];
                if (currentCase.getIsMine()) {
                    System.out.print("X ");
                } else if (currentCase.getNbMinesAround().isEmpty()) {
                    System.out.print("0 ");
                } else {
                    System.out.print(currentCase.getNbMinesAround() + " ");
                }
            }
            System.out.println();
        }
    }

    // Obtenir le ComboBox du niveau
    public JComboBox<Level> getLevelComboBox() {
        return levelComboBox;
    }

    // Gérer les événements d'action (clics sur les boutons, menu)
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == buttonQuit || e.getSource() == mQuitter) {
            app.quit(); // Quitter l'application
        } else if (e.getSource() == buttonNew || e.getSource() == mNewPartie) {
            app.newPartie(levelComboBox.getSelectedIndex()); // Démarrer une nouvelle partie
        } else if (e.getSource() == mConnexion) {
            app.connect(); // Se connecter en ligne
        }
    }

    // Révèle toutes les cases du champ de mines
    public void revealedCases() {
        for (int i = 0; i < tabSize[indexLevel]; i++) {
            for (int j = 0; j < tabSize[indexLevel]; j++) {
                champCases[i][j].setFill(false); // Révèle la case
                champCases[i][j].setFlag(false); // Supprime le drapeau (si présent)
                champCases[i][j].repaint(); // Redessine la case
            }
        }
    }

    // Cache toutes les cases du champ de mines
    public void hideCases() {
        for (int i = 0; i < tabSize[indexLevel]; i++) {
            for (int j = 0; j < tabSize[indexLevel]; j++) {
                champCases[i][j].setFill(true); // Cache la case
                champCases[i][j].setFlag(false); // Supprime le drapeau (si présent)
                champCases[i][j].repaint(); // Redessine la case
            }
        }
    }

    // Propagation de la révélation des cases
    public void propagation(int x, int y) {
        // Vérifie si les coordonnées sont valides
        if (x >= 0 && x < champ.getWidth() && y >= 0 && y < champ.getHeight()) {
            Case currentCase = champCases[x][y]; // Case actuelle

            // Vérifie si la case n'est pas une mine et n'a pas encore été révélée
            if (!currentCase.getIsMine() && currentCase.getIsFill()) {
                currentCase.setFill(false); // Révèle la case
                currentCase.setFlag(false); // Supprime le drapeau
                currentCase.repaint(); // Redessine la case
                revealedCases++; // Incrémente le compteur de cases révélées

                // Vérifie la condition de victoire
                if (revealedCases == totalNonMineCases) {
                    app.winGame(); // Le joueur a gagné
                }

                // Si aucune mine n'est autour, continue la propagation
                if (currentCase.getNbMinesAround().isEmpty()) {
                    propagation(x - 1, y); // Haut
                    propagation(x + 1, y); // Bas
                    propagation(x, y - 1); // Gauche
                    propagation(x, y + 1); // Droite
                    propagation(x - 1, y - 1); // Haut-gauche
                    propagation(x - 1, y + 1); // Haut-droite
                    propagation(x + 1, y - 1); // Bas-gauche
                    propagation(x + 1, y + 1); // Bas-droite
                }
            }
        }
    }

    // Révèle une case spécifique en mode en ligne
    public synchronized void revealCase(int x, int y) {
        System.out.println("Reveal case " + x + " " + y); // Affiche les coordonnées révélées (pour débogage)
        champCases[x][y].setFill(false); // Révèle la case
        champCases[x][y].setFlag(false); // Supprime le drapeau
        champCases[x][y].repaint(); // Redessine la case
        repaint(); // Redessine le panneau entier
    }

    // Définit la taille personnalisée du champ de mines
    public void setCustomSize(int int1) {
        this.customSize = int1;
    }

    // Définit le nombre personnalisé de mines
    public void setCustomNbMines(int int1) {
        this.customNbMines = int1;
    }

    // Désactive les clics sur toutes les cases du champ de mines
    public void disableCases() {
        for (int i = 0; i < tabSize[indexLevel]; i++) {
            for (int j = 0; j < tabSize[indexLevel]; j++) {
                champCases[i][j].clickEnabler = false; // Désactive les clics
            }
        }
    }
}
