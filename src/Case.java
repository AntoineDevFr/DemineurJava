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
    private static final int DIM = 70; // Dimension de la cellule (carrée de 70x70 pixels)
    private Color color = Color.GRAY; // Couleur de fond par défaut

    // Attributs représentant l'état de la cellule
    private boolean isMine = false; // Indique si la cellule contient une mine
    private boolean isFill = true; // Indique si la cellule est encore "remplie" (non révélée)
    private boolean flag = false; // Indique si un drapeau est placé sur la cellule
    public boolean clickEnabler = true; // Indique si la cellule peut être cliquée
    private String nbMinesAround = ""; // Nombre de mines autour de la cellule

    // Coordonnées de la cellule dans la grille
    private int x;
    private int y;

    private App app; // Référence à l'application principale
    private Toolkit toolKit = Toolkit.getDefaultToolkit(); // Outil pour charger des images

    // Constructeur de la cellule
    public Case(App app) {
        this.app = app;
        setBorder(BorderFactory.createLineBorder(Color.WHITE)); // Bordure blanche autour de chaque cellule
        setPreferredSize(new Dimension(DIM, DIM)); // Taille préférée de la cellule
        addMouseListener(this); // Ajouter un écouteur de souris pour gérer les interactions
    }

    // Méthode pour définir si la cellule contient une mine
    public void setMine(boolean isMine) {
        this.isMine = isMine;
    }

    // Méthode pour définir le nombre de mines autour de la cellule
    public void setNbMinesAround(String value) {
        this.nbMinesAround = value;
    }

    // Méthode pour définir les coordonnées de la cellule dans la grille
    public void setCoordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // Méthode pour définir si la cellule est encore "remplie" (non révélée)
    public void setFill(boolean isFill) {
        this.isFill = isFill;
    }

    // Méthode pour définir si un drapeau est placé sur la cellule
    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    // Accesseurs pour l'état de la cellule (si elle est remplie ou non, si c'est une mine, etc.)
    public boolean getIsFill() {
        return isFill;
    }

    public boolean getIsMine() {
        return isMine;
    }

    public String getNbMinesAround() {
        return nbMinesAround;
    }

    // Méthode pour redessiner la cellule
    @Override
    protected void paintComponent(Graphics gc) {
        super.paintComponent(gc);
        drawBackground(gc); // Dessine l'arrière-plan de la cellule
        drawContent(gc); // Dessine le contenu de la cellule (mine, nombre de mines autour, drapeau)
    }

    // Méthode pour dessiner l'arrière-plan de la cellule
    private void drawBackground(Graphics gc) {
        gc.setColor(color); // Définit la couleur de remplissage
        if (isFill) { // Si la cellule est encore remplie (non révélée)
            gc.fillRect(0, 0, getWidth(), getHeight()); // Remplit la cellule avec la couleur
        }
    }

    // Méthode pour dessiner le contenu de la cellule (mine, nombre, drapeau)
    private void drawContent(Graphics gc) {
        if (!isFill) { // Si la cellule est révélée
            drawCaseContent(gc); // Dessine le contenu (mine ou nombre de mines autour)
        }

        if (flag) { // Si un drapeau est placé sur la cellule
            drawFlag(gc); // Dessine le drapeau
        }
    }

    // Méthode pour dessiner le contenu d'une cellule révélée
    private void drawCaseContent(Graphics gc) {
        gc.setFont(new Font("Arial", Font.BOLD, 25)); // Définir la police d'écriture

        if (isMine) { // Si la cellule contient une mine
            gc.drawImage(toolKit.getImage("./src/resources/bombe.png"), 0, 0, this); // Affiche l'image d'une bombe
        } else { // Si la cellule ne contient pas de mine
            setColorBasedOnMinesAround(gc); // Définir la couleur du texte en fonction du nombre de mines autour
            drawMinesAround(gc); // Dessiner le nombre de mines autour
        }
    }

    // Méthode pour définir la couleur du texte en fonction du nombre de mines autour
    private void setColorBasedOnMinesAround(Graphics gc) {
        switch (nbMinesAround) {
            case "1":
                gc.setColor(Color.GREEN); // Vert si 1 mine autour
                break;
            case "2":
                gc.setColor(Color.YELLOW); // Jaune si 2 mines autour
                break;
            case "3":
                gc.setColor(Color.RED); // Rouge si 3 mines autour
                break;
            default:
                gc.setColor(Color.BLACK); // Noir pour 0 ou plus de 3 mines
                break;
        }
    }

    // Méthode pour dessiner le nombre de mines autour de la cellule
    private void drawMinesAround(Graphics gc) {
        if (!nbMinesAround.isEmpty()) { // Si le nombre de mines autour est défini
            int textWidth = gc.getFontMetrics().stringWidth(nbMinesAround); // Calcul de la largeur du texte
            int textHeight = gc.getFontMetrics().getHeight(); // Calcul de la hauteur du texte
            gc.drawString(nbMinesAround, (getWidth() - textWidth) / 2, (getHeight() + textHeight / 4) / 2); // Dessine le texte centré dans la cellule
        }
    }

    // Méthode pour dessiner un drapeau sur la cellule
    private void drawFlag(Graphics gc) {
        Image img = toolKit.getImage("./src/resources/flag.png"); // Charger l'image du drapeau
        int imgWidth = img.getWidth(this); // Largeur de l'image
        int imgHeight = img.getHeight(this); // Hauteur de l'image

        int x = (getWidth() - imgWidth) / 2; // Position x pour centrer l'image
        int y = (getHeight() - imgHeight) / 2; // Position y pour centrer l'image

        gc.drawImage(img, x, y, this); // Dessiner l'image du drapeau
    }

    // Gestion des clics sur la cellule
    @Override
    public void mousePressed(MouseEvent e) {
        if (clickEnabler) { // Vérifier si les clics sont autorisés sur cette cellule
            if (SwingUtilities.isLeftMouseButton(e)) { // Si clic gauche
                if (isMine) { // Si la cellule contient une mine
                    app.gameOver(); // Appelle la méthode gameOver() dans l'application
                } else {
                    app.propagation(x, y); // Propage l'action de révélation à partir de cette cellule
                }
                repaint(); // Redessine la cellule
            } else if (SwingUtilities.isRightMouseButton(e)) { // Si clic droit
                if (isFill || flag) { // Si la cellule est encore remplie ou si un drapeau est placé
                    flag = !flag; // Bascule l'état du drapeau
                    repaint(); // Redessine la cellule
                }
            }
        }
    }

    // Changements de couleur lorsque la souris entre dans la cellule
    @Override
    public void mouseEntered(MouseEvent e) {
        if (isFill) { // Si la cellule est encore remplie
            color = Color.LIGHT_GRAY; // Change la couleur en gris clair
            repaint(); // Redessine la cellule
        }
    }

    // Changements de couleur lorsque la souris sort de la cellule
    @Override
    public void mouseExited(MouseEvent e) {
        if (isFill) { // Si la cellule est encore remplie
            color = Color.GRAY; // Remet la couleur d'origine (gris)
            repaint(); // Redessine la cellule
        }
    }

    // Méthodes vides pour d'autres événements de souris
    @Override
    public void mouseClicked(MouseEvent e) {
        // Pas d'action requise lors du clic
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // Pas d'action requise lors du relâchement du clic
    }
}
