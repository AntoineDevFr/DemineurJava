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
    private JButton buttonQuit, buttonNew;
    JMenu menu;
    private JMenuItem mQuitter, mNewPartie, mConnexion; 
    private JComboBox<Level> levelComboBox;
    private JPanel panelNorth, panelSouth; 
    public JDialog waitingDialog;
    private int indexLevel;
    public boolean startgame = false;

    private int customSize;
    private int customNbMines;
    private final int[] tabSize = {5, 10, 15, 0};  // Last element is for CUSTOM
    private final int[] tabNbMines = {3, 7, 20, 0};

    private App app;
    private Champ champ;

    public int score;
    public int scoreOnline =0;
    private Compteur compteur;

    private int totalNonMineCases; 
    private int revealedCases;

    private JLabel scoreValue;
    private JPanel panelMines = new JPanel();
    public Case[][] champCases;

    public Gui(App app, Champ champ) {
        this.app = app;
        this.champ = champ;
        
        this.compteur = new Compteur(this);
        this.scoreValue = new JLabel(String.valueOf(score));
        compteur.start();

        setLayout(new BorderLayout());
        initializePanels();
        initializeMenu();
    }

    public void updateScoreValue() {
        if (app.online) {
            scoreValue.setText(String.valueOf(scoreOnline));
        } else {
            scoreValue.setText(String.valueOf(score));
        }
    }

    private void initializePanels() {
        initializeMinesPanel();
        initializeNorthPanel();
        initializeSouthPanel();
        initializeWestPanel();
    }

    private void initializeMinesPanel() {
        updateMinesPanel(Level.EASY.ordinal());
        add(panelMines, BorderLayout.CENTER);
    }

    private void initializeNorthPanel() {
        panelNorth = new JPanel();
        panelNorth.setBackground(new Color(60, 63, 65));

        JLabel scoreLabel = new JLabel("Score : ");
        scoreLabel.setForeground(Color.WHITE);
        JLabel levLabel = new JLabel("Level : ");
        levLabel.setForeground(Color.WHITE);
        scoreValue.setForeground(Color.CYAN);

        panelNorth.add(scoreLabel);
        panelNorth.add(scoreValue);
        panelNorth.add(levLabel);

        // ComboBox for levels
        levelComboBox = new JComboBox<>(Level.values());
        levelComboBox.addActionListener(this);
        panelNorth.add(levelComboBox);

        add(panelNorth, BorderLayout.NORTH);
    }

    private void initializeSouthPanel() {
        panelSouth = new JPanel();
        panelSouth.setBackground(new Color(43, 43, 43));

        buttonQuit = new JButton("Quit");
        buttonQuit.setBackground(new Color(255, 69, 58));
        buttonQuit.addActionListener(this);
        panelSouth.add(buttonQuit);

        buttonNew = new JButton("New");
        buttonNew.setBackground(new Color(50, 205, 50)); 
        buttonNew.addActionListener(this);
        panelSouth.add(buttonNew);
       
        add(panelSouth, BorderLayout.SOUTH);
    }

    private void initializeWestPanel() {
        JPanel panelWest = new JPanel();
        panelWest.setBackground(new Color(43, 43, 43));
        JLabel labelUsers = new JLabel("Users connected : ");
        labelUsers.setForeground(Color.WHITE);
        panelWest.add(labelUsers);


        add(panelWest, BorderLayout.WEST);
    }
    private void initializeMenu() {
        JMenuBar menuBar = new JMenuBar();
        menu = new JMenu("Options");
        menuBar.add(menu);

        mNewPartie = new JMenuItem("New Game");
        menu.add(mNewPartie);
        mNewPartie.addActionListener(this);

        mQuitter = new JMenuItem("Quit");
        menu.add(mQuitter);
        mQuitter.addActionListener(this);

        mConnexion = new JMenuItem("Connect");
        menu.add(mConnexion);
        mConnexion.addActionListener(this);

        app.setJMenuBar(menuBar);
    }

    public void newPartie(int indexLevel) {
        tabSize[3] = customSize;
        tabNbMines[3] = customNbMines;

        compteur.stop();
        compteur.reset();
        compteur.start();
        scoreOnline = 0;
        updateScoreValue();
        revealedCases = 0;
        updateMinesPanel(indexLevel);
        app.pack();

        if (app.online) {
            app.networkManager.startListening();
            // Remove elements from the UI
            panelNorth.remove(levelComboBox);
            panelSouth.remove(buttonNew);
            menu.remove(mConnexion);
            compteur.stop();
            scoreValue.setText(String.valueOf(scoreOnline));


            // Create a waiting dialog
            waitingDialog = new JDialog(app, "Waitingq for Players", true);
            waitingDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            waitingDialog.setSize(200, 100);
            waitingDialog.setLocationRelativeTo(app);
    
            // Add a simple label to indicate waiting status
            JLabel waitingLabel = new JLabel("Waiting for other players ...", SwingConstants.CENTER);
            waitingDialog.add(waitingLabel);
            System.out.println("J'envoie start");
            app.networkManager.sendStart();
            // Add a window listener to handle the dialog close event
            waitingDialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    // If the dialog is closed, switch back to solo mode
                    app.online = false; // Switch back to solo mode
                    app.networkManager.exit();
                    startgame = true; // Exit the waiting loop
                    panelNorth.add(levelComboBox);
                    panelSouth.add(buttonNew);
                    menu.add(mConnexion);
                    compteur.stop();
                    compteur.reset();
                    compteur.start();
                    revealedCases = 0;
                }
            });
    
            // Show the waiting dialog
            waitingDialog.setVisible(true);
    
            // Wait until the server sends the "start" signal or the dialog is closed
            System.out.println("Valeur de "+ startgame);
    
            // Hide the waiting dialog when startgame is true
            waitingDialog.dispose();
        }
    
        // Continue with game setup
       
    }
    

    public void stopTimer() {
        compteur.stop();
    }

    public int getScore() {
        return score;
    }

    public void updateMinesPanel(int indexLevel) {
        this.indexLevel = indexLevel;
        int sizeNewChamp = tabSize[indexLevel];
        champCases = new Case[sizeNewChamp][sizeNewChamp];

        panelMines.removeAll();
        panelMines.setLayout(new GridLayout(sizeNewChamp,sizeNewChamp));
        panelMines.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        for (int i = 0; i < sizeNewChamp; i++) {
            for (int j = 0; j < sizeNewChamp; j++) {
                champCases[i][j] = createCasePanel(i, j);
                panelMines.add(champCases[i][j]);
            }
        }
        
        //displayChampInfo();

        panelMines.setBackground(Color.LIGHT_GRAY);
        //panelMines.revalidate(); 
        panelMines.repaint();

        totalNonMineCases = sizeNewChamp * sizeNewChamp - tabNbMines[indexLevel];
    }

    private Case createCasePanel(int i, int j) {
        Case casePanel = new Case(app);
        casePanel.setCoordinates(i, j);

        if (!app.online) {
            if (champ.isMine(i, j)) {
                casePanel.setMine(true);
            } else {
                int minesAround = champ.nbMinesaround(i, j);
                if (minesAround != 0) {
                    casePanel.setNbMinesAround(String.valueOf(minesAround));
                }
            }
        } else {
            if (app.networkManager.isMineOnline(i, j)) {
                casePanel.setMine(true);
            } else {
                int minesAround = app.networkManager.nbMinesaroundOnline(i, j);
                if (minesAround != 0) {
                    casePanel.setNbMinesAround(String.valueOf(minesAround));
                }
            }

        }
        return casePanel;
    }

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


    public JComboBox<Level> getLevelComboBox() {
        return levelComboBox;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == buttonQuit || e.getSource() == mQuitter) {
            app.quit();
        } else if (e.getSource() == buttonNew || e.getSource() == mNewPartie) {
            app.newPartie(levelComboBox.getSelectedIndex());
        } else if (e.getSource() == mConnexion) {
            app.connect();
        }
    }

    public void revealedCases() {
        for (int i = 0; i < tabSize[indexLevel]; i++) {
            for (int j = 0; j < tabSize[indexLevel]; j++) {
                champCases[i][j].setFill(false);
                champCases[i][j].setFlag(false);
                champCases[i][j].repaint();
            }
        }
    }

    public void propagation(int x, int y) {
        if (x >= 0 && x < champ.getWidth() && y >= 0 && y < champ.getHeight()) { //Check if the case is in the field
            Case currentCase = champCases[x][y];
            if (!currentCase.getIsMine() && currentCase.getIsFill()) { //Check if the case is not a mine and is not already revealed
                currentCase.setFill(false);
                currentCase.setFlag(false);
                currentCase.repaint(); 
                revealedCases++; 

                // Check win condition
                if (revealedCases == totalNonMineCases) {
                    app.winGame();
                }

                if (currentCase.getNbMinesAround().isEmpty()) { //Check if there are no mines around
                    // Propagate if no mines around
                    propagation(x - 1, y); // Up
                    propagation(x + 1, y); // Down
                    propagation(x, y - 1); // Left
                    propagation(x, y + 1); // Right
                    propagation(x - 1, y - 1); // Top-left
                    propagation(x - 1, y + 1); // Top-right
                    propagation(x + 1, y - 1); // Bottom-left
                    propagation(x + 1, y + 1); // Bottom-right
                }
            }
        }
    }

    public void revealCase(int x, int y) {
        champCases[x][y].setFill(false);
        champCases[x][y].setFlag(false);
        champCases[x][y].repaint();
    }

    public void setCustomSize(int int1) {
        this.customSize = int1;
    }

    public void setCustomNbMines(int int1) {
        this.customNbMines = int1;
    }
}