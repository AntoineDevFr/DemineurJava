import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.Font;
import javax.swing.*;


public class Case extends JPanel implements MouseListener {
    private Color color = Color.gray;
    public boolean isMine = false;
    public boolean isFill = true;
    public boolean flag = false;
    public int x;
    public int y;

    Toolkit toolKit = getToolkit();

    public String nbMinesAround = "";
    private final static int DIM=70 ;

    private App app;

    public Case (App app) {
        this.app = app;
        setBorder(BorderFactory.createLineBorder(Color.WHITE));
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
            gc.setFont(new Font("Arial", Font.BOLD, 25));
            gc.setColor(Color.GREEN);

            if (this.isMine) {
                gc.drawImage(toolKit.getImage("./src/bombe.png"), 0, 0, this);
            }

            switch (nbMinesAround) {
                case "1":
                    gc.setColor(Color.GREEN);
                    break;
                case "2":
                    gc.setColor(Color.YELLOW);
                    break;
                case "3":
                    gc.setColor(Color.RED);
                    break;
                default:
                    gc.setColor(Color.BLACK);
                    break;
            }

            if(!this.isMine) {
                // Centrer le texte dans la case -> CHatgpt
                int textWidth = gc.getFontMetrics().stringWidth(nbMinesAround);
                int textHeight = gc.getFontMetrics().getHeight();
                gc.drawString(nbMinesAround, (getWidth() - textWidth) / 2, (getHeight() + textHeight / 4) / 2);
            }
            
        }

        if(flag) {

            Image img = toolKit.getImage("./src/flag.png");
            int imgWidth = img.getWidth(this);
            int imgHeight = img.getHeight(this);

            int x = (getWidth() - imgWidth) / 2; // Center horizontally
            int y = (getHeight() - imgHeight) / 2; // Center vertically

            // Draw the image
            gc.drawImage(img, x, y, this);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            if (this.isMine) {
                app.gameOver();
            } else {
                app.propagation(x,y);
            }
            repaint();  
        }

        if (SwingUtilities.isRightMouseButton(e)) {
            if(isFill) {
                flag = !flag;
                repaint();
            }
         
        }
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
