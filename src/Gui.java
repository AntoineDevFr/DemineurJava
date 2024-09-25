import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * Graphical User Interface (View)
 * @author AntoineB
 * @version 0.0
 */

public class Gui extends JPanel implements ActionListener { 
    private JButton buttonQuit, buttonNew;
    private JMenuItem mQuitter, mNewPartie; 
    private JComboBox<Level> levelComboBox;
    
    private App app;
    private Champ champ;

    private int totalNonMineCases; 
    private int revealedCases;

    private JLabel scoreValue = new JLabel("0");
    JPanel panelMines = new JPanel();

    public Case[][] champCases;


    Gui(App app, Champ champ) {
        setLayout(new BorderLayout());
        this.app = app;
        this.champ = champ;

        /**
         * JPanel CENTER
         */
        majPanelMines();
        add(panelMines, BorderLayout.CENTER);

        /**
         * JPanel NORTH
         */
        JPanel panelNorth = new JPanel();
        panelNorth.setBackground(new Color(60, 63, 65));

        JLabel scoreLabel = new JLabel("Score : ");
        scoreLabel.setForeground(Color.WHITE);
        JLabel levLabel = new JLabel("Level : ");
        levLabel.setForeground(Color.WHITE);

        scoreValue.setForeground(Color.CYAN);

        panelNorth.add(scoreLabel);
        panelNorth.add(scoreValue);
        panelNorth.add(levLabel);

        //ComboBox
        levelComboBox = new JComboBox<Level>(Level.values());
        levelComboBox.addActionListener(this);
        panelNorth.add(levelComboBox);

        add(panelNorth, BorderLayout.NORTH);

        /**
         * JPanel SOUTH
         */
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

        /**
         * Menu
         */
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Options");
        menuBar.add(menu);

        mNewPartie = new JMenuItem("New Game");
        menu.add(mNewPartie);
        mNewPartie.addActionListener(this);

        mQuitter = new JMenuItem("Quit");
        menu.add(mQuitter) ;
        mQuitter.addActionListener(this);

        app.setJMenuBar(menuBar);
    }


    public void newPartie(int indexLevel) {
        scoreValue.setText("0");
        panelMines.removeAll();
        majPanelMines();
        app.pack();
    }

    public void majPanelMines() {
        //ON récupère le champ 
        champCases = new Case[champ.getWidth()][champ.getHeight()];
        for (int i = 0; i < champ.getWidth(); i++) {
            for (int j = 0; j < champ.getHeight(); j++) {
                champCases[i][j] = new Case(app);
                if(champ.isMine(i, j)) {
                    champCases[i][j].setMine(true);
                } else if(champ.nbMinesaround(i, j) != 0){
                    champCases[i][j].setNbMinesAround(String.valueOf(champ.nbMinesaround(i, j)));
                }
            }
        }

        panelMines.setLayout(new GridLayout(champ.getWidth(), champ.getHeight()));
        panelMines.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        for (int i = 0; i < champ.getWidth(); i++) {
            for (int j = 0; j < champ.getHeight(); j++) {
                JPanel casePanel = champCases[i][j];
                champCases[i][j].x = i;
                champCases[i][j].y = j;
                //casePanel.setBorder(BorderFactory.createLineBorder(Color.WHITE));
                panelMines.add(casePanel);
            }
        }
        panelMines.setBackground(Color.LIGHT_GRAY);
        panelMines.revalidate(); 
        panelMines.repaint();

        totalNonMineCases = champ.getWidth() * champ.getHeight() - champ.getMineCount(); 
        revealedCases = 0;

    }

    public JComboBox<Level> getLevelComboBox() {
        return levelComboBox;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == buttonQuit || e.getSource() == mQuitter) {
            app.quit();
        }
        else if (e.getSource() == buttonNew || e.getSource() == mNewPartie) {
            //On réinitialise le champ
            app.newPartie(levelComboBox.getSelectedIndex());
        }
    }

    public void revealedCases() {
        for (int i = 0; i < champ.getWidth(); i++) {
            for (int j = 0; j < champ.getHeight(); j++) {
                champCases[i][j].isFill = false;
                champCases[i][j].flag = false;
                champCases[i][j].repaint();
            }
        }
    }

    public void propagation(int x, int y) {
        if (x < 0 || x >= champ.getWidth() || y < 0 || y >= champ.getHeight()) {
            return; 
        }

        Case currentCase = champCases[x][y];

        if (currentCase.isMine || !currentCase.isFill) {
            return; 
        }

      
        currentCase.isFill = false;
        currentCase.flag = false;
        currentCase.repaint(); 

        revealedCases++; 

        // Check win condition
        if (revealedCases == totalNonMineCases) {
            app.winGame();
        }

        if (currentCase.nbMinesAround.isEmpty()) {
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
