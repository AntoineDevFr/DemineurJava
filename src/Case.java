import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.Font;
import javax.swing.*;

/**
 * Case represents a single cell in the Minesweeper game.
 * It handles the display and interaction for that cell.
 * 
 * @author Antoine Banchet
 * @version 1.0
 */
public class Case extends JPanel implements MouseListener {
    private static final int DIM = 70;
    private Color color = Color.GRAY;

    private boolean isMine = false;
    private boolean isFill = true;
    private boolean flag = false;
    private String nbMinesAround = "";
    
    private int x;
    private int y;

    private App app;
    private Toolkit toolKit = Toolkit.getDefaultToolkit();

    public Case(App app) {
        this.app = app;
        setBorder(BorderFactory.createLineBorder(Color.WHITE));
        setPreferredSize(new Dimension(DIM, DIM));
        addMouseListener(this);
    }

    public void setMine(boolean isMine) {
        this.isMine = isMine;
    }

    public void setNbMinesAround(String value) {
        this.nbMinesAround = value;
    }

    public void setCoordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setFill(boolean isFill) {
        this.isFill = isFill;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public boolean getIsFill() {
        return isFill;
    }

    public boolean getIsMine() {
        return isMine;
    }

    public String getNbMinesAround() {
        return nbMinesAround;
    }

    @Override
    protected void paintComponent(Graphics gc) {
        super.paintComponent(gc);
        drawBackground(gc);
        drawContent(gc);
    }

    private void drawBackground(Graphics gc) {
        gc.setColor(color);
        if (isFill) {
            gc.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    private void drawContent(Graphics gc) {
        if (!isFill) {
            drawCaseContent(gc);
        }

        if (flag) {
            drawFlag(gc);
        }
    }

    private void drawCaseContent(Graphics gc) {
        gc.setFont(new Font("Arial", Font.BOLD, 25));

        if (isMine) {
            gc.drawImage(toolKit.getImage("./src/resources/bombe.png"), 0, 0, this);
        } else {
            setColorBasedOnMinesAround(gc);
            drawMinesAround(gc);
        }
    }

    private void setColorBasedOnMinesAround(Graphics gc) {
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
    }

    private void drawMinesAround(Graphics gc) { // Draw the number of mines around the cell
        if (!nbMinesAround.isEmpty()) {
            int textWidth = gc.getFontMetrics().stringWidth(nbMinesAround);
            int textHeight = gc.getFontMetrics().getHeight();
            gc.drawString(nbMinesAround, (getWidth() - textWidth) / 2, (getHeight() + textHeight / 4) / 2);
        }
    }

    private void drawFlag(Graphics gc) {
        Image img = toolKit.getImage("./src/resources/flag.png");
        int imgWidth = img.getWidth(this);
        int imgHeight = img.getHeight(this);

        int x = (getWidth() - imgWidth) / 2;
        int y = (getHeight() - imgHeight) / 2;

        gc.drawImage(img, x, y, this);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            if (isMine) {
                app.gameOver();
            } else {
                app.propagation(x, y);
            }
            repaint();
        } else if (SwingUtilities.isRightMouseButton(e)) {
            if (isFill || flag) {
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
        // No action needed for mouse click
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // No action needed for mouse release
    }
}