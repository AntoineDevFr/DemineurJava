import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseListener;

import javax.swing.JPanel;
import java.awt.event.MouseEvent;

public class Case extends JPanel implements MouseListener {
    private Color color = Color.gray;
    private boolean isMine = false;
    private boolean isFill = true;

    private String nbMinesAround = "";
    private final static int DIM=50 ;


    public Case () {
        setPreferredSize(new Dimension(DIM, DIM)); // taille de la case 
        addMouseListener(this); // ajout listener souris
    }
  
    public void setMine(boolean isMine) {
        this.isMine = isMine;
    }

    public void setNbMinesAround(String valueOf) {
        nbMinesAround = valueOf;
    }

    @Override
    public void paintComponent(Graphics gc) {
        super.paintComponent(gc);
        gc.setColor(color);
        if(isFill) {
            gc.fillRect(5,5, 40,40);
        }
        gc.drawString(nbMinesAround, 25, 25);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        isFill = false;
        if(this.isMine) {
            nbMinesAround = "BOOM";
        }
        repaint(); // comme on veut redessiner, on force l’appel de paintComponent()
    }


    @Override
    public void mouseClicked(MouseEvent e) {
       ;
    }


    @Override
    public void mouseReleased(MouseEvent e) {
        ;
    }


    @Override
    public void mouseEntered(MouseEvent e) {
        ;
    }


    @Override
    public void mouseExited(MouseEvent e) {
        ;
    }
    
}
