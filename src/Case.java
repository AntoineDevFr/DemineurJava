import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseListener;

import javax.swing.JPanel;
import java.awt.event.MouseEvent;
import java.awt.Font;

public class Case extends JPanel implements MouseListener {
    private Color color = Color.gray;
    private boolean isMine = false;
    public boolean isFill = true;

    private String nbMinesAround = "";
    private final static int DIM=50 ;

    private App app;

    public Case (App app) {
        this.app = app;
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
        if (isFill) {
            gc.fillRect(0, 0, getWidth(), getHeight());  
        }

        if (!isFill) {
            gc.setFont(new Font("Arial", Font.BOLD, 20));
            gc.setColor(Color.GREEN);

            if (this.isMine) {
                nbMinesAround = "BOOM";
            }

            if (nbMinesAround.equals("BOOM")) {
                gc.setColor(Color.RED); 
            }
           
            // Centrer le texte dans la case -> CHatgpt
            int textWidth = gc.getFontMetrics().stringWidth(nbMinesAround);
            int textHeight = gc.getFontMetrics().getHeight();
            gc.drawString(nbMinesAround, (getWidth() - textWidth) / 2, (getHeight() + textHeight / 4) / 2);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        isFill = false;  
        if (this.isMine) {
            app.gameOver();
        }
        repaint();  
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if (isFill) {
            color = Color.LIGHT_GRAY;
            repaint();
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (isFill) {
            color = Color.GRAY;
            repaint();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
       ;
    }


    @Override
    public void mouseReleased(MouseEvent e) {
        ;
    }

}
