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
    private JComboBox<Level> levelComboBox;
    
    private App app;
    private Champ champ;

    private JLabel scoreValue = new JLabel("0");
    JPanel panelMines = new JPanel();

    Gui(App app, Champ champ) {
        setLayout(new BorderLayout());
        this.app = app;
        this.champ = champ;

        /**
         * JPanel CENTER
         */
        majPanelMines();

        /**
         * JPanel NORTH
         */
        JPanel panelNorth = new JPanel();

        JLabel scoreLabel = new JLabel("Score : ");
        JLabel levLabel = new JLabel("Level : ");
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
        
        buttonQuit = new JButton("Quit");
        buttonQuit.addActionListener(this);
        panelSouth.add(buttonQuit);

        buttonNew = new JButton("New");
        buttonNew.addActionListener(this);
        panelSouth.add(buttonNew);
       
        add(panelSouth, BorderLayout.SOUTH);
    }

    public void newPartie(int indexLevel) {
        scoreValue.setText("0");
        panelMines.removeAll();
        majPanelMines();
        app.pack();
    }

    public void majPanelMines() {
        panelMines.setLayout(new GridLayout(champ.getWidth(), champ.getHeight()));
        panelMines.setBackground(Color.GRAY);
        for (int i = 0; i < champ.getWidth(); i++) {
            for (int j = 0; j < champ.getHeight(); j++) {
            JLabel label;
            if(champ.isMine(i, j)) {
                label = new JLabel("x");
                label.setForeground(Color.RED);
            } else {
                label = new JLabel(Integer.toString(champ.nbMinesaround(i,j))); 
            }
            panelMines.add(label);
            }
        }
        add(panelMines, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == buttonQuit) {
            app.quit();
        }
        else if (e.getSource() == buttonNew) {
            //On réinitialise le champ
            app.newPartie(levelComboBox.getSelectedIndex());
        }
    }
}
