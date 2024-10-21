import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * NetworkManager
 * Gère la connexion et la communication réseau entre le client (joueur) et le serveur.
 * Utilise un socket pour la communication et envoie/recevra des commandes spécifiques pour la gestion du jeu en ligne.
 */
public class NetworkManager {
    private Socket sock; // Socket pour la connexion réseau
    private DataOutputStream out; // Flux de sortie pour envoyer des données au serveur
    private DataInputStream in; // Flux d'entrée pour recevoir des données du serveur
    private App app; // Référence à l'application principale (contrôleur)
    private int nbJoeurs; // Nombre de joueurs connectés

    // Constructeur qui prend une référence à l'application principale
    public NetworkManager(App app){
        this.app = app; // Initialise la référence à l'application
    }

    // Méthode pour se connecter au serveur en spécifiant l'hôte et le port
    public void connect(String host, int port) throws IOException {
        sock = new Socket(host, port); // Crée une nouvelle connexion au serveur
        app.online = true; // Indique que le jeu est maintenant en mode en ligne
        out = new DataOutputStream(sock.getOutputStream()); // Initialise le flux de sortie
        in = new DataInputStream(sock.getInputStream()); // Initialise le flux d'entrée
        System.out.println("Connected to server at " + host + ":" + port); // Affiche un message de confirmation
    }

    // Méthode pour envoyer les informations d'authentification au serveur (nom du joueur)
    public void auth(String namePlayer) throws IOException
    {
        out.writeUTF("auth"); // Envoie la commande d'authentification
        out.writeUTF(app.playerName); // Envoie le nom du joueur

        int numJoueur = in.readInt(); // Reçoit le numéro de joueur
        nbJoeurs = in.readInt(); // Reçoit le nombre total de joueurs connectés

        System.out.println("Joueur n°:"+numJoueur); // Affiche le numéro du joueur
        System.out.println("Connecté au serveur"); // Confirme la connexion au serveur

        out.writeUTF("init"); // Envoie la commande d'initialisation du jeu
        int level = in.readInt(); // Reçoit le niveau de difficulté choisi
        System.out.println("Level : " + level); // Affiche le niveau

        // Lance une nouvelle partie avec le niveau reçu du serveur
        app.gui.newPartie(level);
    } 

    // Méthode pour signaler au serveur que le joueur souhaite commencer la partie
    public void sendStart() {
        try {
            out.writeUTF("wantstart"); // Envoie la demande de début de partie
            System.out.println("Je veux commencer"); // Message de confirmation
        } catch (IOException e) {
            e.printStackTrace(); // Gère les erreurs de connexion
        }
    }

    // Méthode pour demander au serveur si une case contient une mine
    public boolean isMineOnline(int i, int j) {
        boolean response = false;
        try {
            out.writeUTF("isMine"); // Envoie la commande pour vérifier une mine
            out.writeInt(i); // Coordonnée x
            out.writeInt(j); // Coordonnée y
            response = in.readBoolean(); // Reçoit la réponse (true si mine, false sinon)
        } catch (IOException e) {
            e.printStackTrace(); // Gère les erreurs d'I/O
        }
        return response; // Retourne la réponse du serveur
    }

    // Méthode pour demander au serveur combien de mines entourent une case spécifique
    public int nbMinesaroundOnline(int i, int j) {
        int count = 0;
        try {
            out.writeUTF("nbMinesaround"); // Envoie la commande pour obtenir le nombre de mines environnantes
            out.writeInt(i); // Coordonnée x
            out.writeInt(j); // Coordonnée y
            count = in.readInt(); // Reçoit le nombre de mines environnantes
        } catch (IOException e) {
            e.printStackTrace(); // Gère les erreurs d'I/O
        }
        return count; // Retourne le nombre de mines autour de la case
    }

    // Méthode pour envoyer les coordonnées d'un coup joué en ligne
    public void sendMoveOnline(int x, int y) {
        try {
            out.writeUTF("cord"); // Envoie la commande pour transmettre les coordonnées
            out.writeInt(x); // Coordonnée x
            out.writeInt(y); // Coordonnée y
        } catch (IOException e) {
            e.printStackTrace(); // Gère les erreurs d'I/O
        }
    }

    // Méthode pour envoyer une requête au serveur pour commencer une nouvelle partie
    public void newGame() {
        try {
            out.writeUTF("newGame"); // Envoie la commande pour démarrer une nouvelle partie
        } catch (IOException e) {
            e.printStackTrace(); // Gère les erreurs d'I/O
        }
    }

    // Méthode pour quitter la partie en ligne et fermer la connexion
    public void exit() {
        try {
            out.writeUTF("exit"); // Envoie la commande pour quitter
            sock.close(); // Ferme la connexion socket
        } catch (IOException e) {
            e.printStackTrace(); // Gère les erreurs d'I/O
        }
    }

    // Méthode pour démarrer un thread qui écoute les messages reçus du serveur
    public synchronized void startListening() {
        Thread listenerThread = new Thread(() -> {
            try {
                while (!sock.isClosed()) { // Boucle tant que le socket est ouvert
                    if (in.available() > 0) { // Vérifie si des données sont disponibles
                        String message = in.readUTF(); // Lit le message reçu

                        // Gère les différentes commandes reçues du serveur
                        switch (message) {
                            case "revealCase":
                                System.out.println("Je reçois revealCase");
                                int x = in.readInt(); // Reçoit la coordonnée x
                                int y = in.readInt(); // Reçoit la coordonnée y
                                System.out.println("Je reçois les coordonnées: x = " + x + ", y = " + y);
                                if (!app.isWaiting) {
                                    System.out.println("Je révèle la case");
                                    app.revealCaseOnline(x, y); // Révèle la case dans l'interface graphique
                                }
                                break;
                            case "start":
                                System.out.println("Je reçois start");
                                app.gui.startgame = true; // Démarre le jeu dans l'interface graphique
                                app.gui.waitingDialog.dispose(); // Ferme la boîte de dialogue d'attente
                                break;
                            case "newchamp":
                                int level = Integer.parseInt(in.readUTF()); // Reçoit le nouveau niveau
                                app.gui.newPartie(level); // Démarre une nouvelle partie avec le niveau reçu
                                break;
                            case "namesPlayers":
                                String[] playersName = new String[nbJoeurs];
                                for (int i = 0; i < nbJoeurs; i++) {
                                    playersName[i] = in.readUTF(); // Reçoit et stocke les noms des joueurs
                                    System.out.println("Je recois: le nom de " + playersName[i]);
                                }
                                app.gui.addPlayers(playersName); // Met à jour l'interface avec les noms des joueurs
                                break;
                            case "end":
                                app.winGame(); // Déclenche la fin de partie (victoire)
                                break;
                            default:
                                break;
                        }
                    } else {
                        // Si aucune donnée, le thread dort brièvement pour éviter une boucle intensive
                        Thread.sleep(100);
                    }
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace(); // Gère les erreurs d'I/O ou d'interruption de thread
            }
        });
        listenerThread.start(); // Démarre le thread de l'écoute réseau
    }
}
