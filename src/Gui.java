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
    private JMenuItem mQuitter, mNewPartie, mConnexion; 
    private JComboBox<Level> levelComboBox;
    private JPanel panelNorth;

    private final int[] tabSize = {5, 10, 15, 0};  // Last element is for CUSTOM
    private final int[] tabNbMines = {3, 7, 20, 0};

    private App app;
    private Champ champ;

    public int score;
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
        scoreValue.setText(String.valueOf(score));
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
        JPanel panelSouth = new JPanel();
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
        JMenu menu = new JMenu("Options");
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
        compteur.stop();
        compteur.reset();
        compteur.start();
        revealedCases = 0;
        if(app.online) {
            panelNorth.remove(levelComboBox);
        }
        updateMinesPanel(indexLevel);
        app.pack();
    }

    public void stopTimer() {
        compteur.stop();
    }

    public int getScore() {
        return score;
    }

    public void updateMinesPanel(int indexLevel) {
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
            if (app.isMineOnline(i, j)) {
                casePanel.setMine(true);
            } else {
                int minesAround = app.nbMinesaroundOnline(i, j);
                if (minesAround != 0) {
                    casePanel.setNbMinesAround(String.valueOf(minesAround));
                }
            }
        }
        return casePanel;
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
        for (int i = 0; i < champ.getWidth(); i++) {
            for (int j = 0; j < champ.getHeight(); j++) {
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
}