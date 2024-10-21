/**
 * Serveur.java
 * Classe principale pour le serveur du jeu Minesweeper en ligne.
 * Gère les connexions des clients, les parties en ligne et les communications.
 */

import java.net.*; // Sockets
import java.io.*; // Streams
import java.util.ArrayList;
import java.util.List;

public class Serveur {
    // Liste pour stocker les clients connectés
    private static List<ClientHandler> clients = new ArrayList<>();
    public static Champ champ = new Champ(); // Champ de jeu (terrain)
    public static Level level = Level.EASY; // Niveau de difficulté initial
    public static boolean champSuivis[][]; // Tableau pour suivre les cases révélées
    public static int nbJoeur = 2; // Nombre de joueurs (fixé à 2)

    // Variables pour suivre l'état du jeu
    private static int totalNonMineCasesOnline; // Nombre total de cases sans mines
    private static int revealedCasesOnline = 0; // Compteur de cases révélées
    private static int nbJoeursWantStart = 0; // Compteur de joueurs voulant commencer la partie
    private static int wantReplay = 0; // Compteur de joueurs voulant rejouer
    private static String[] playersName; // Tableau pour stocker les noms des joueurs
    private static boolean firstStart = true; // Indique si c'est le premier lancement de la partie

    // Tailles et nombre de mines en fonction du niveau de difficulté
    private final static int[] tabSize = {5, 10, 15, 0};  // Le dernier élément est pour le niveau personnalisé
    private final static int[] tabNbMines = {3, 7, 20, 0};

    // Constructeur du serveur
    public Serveur() {
        // Initialisation du champ de jeu et des cases suivies
        champ.init(level.ordinal());
        champSuivis = new boolean[tabSize[level.ordinal()]][tabSize[level.ordinal()]];
        System.out.println(champSuivis.length);
        System.out.println("Champ initialisé du serveur");
        totalNonMineCasesOnline = champ.getWidth() * champ.getHeight() - tabNbMines[level.ordinal()];
        playersName = new String[nbJoeur];

        // Affichage du champ de jeu et démarrage du serveur
        champ.display();
        System.out.println("Serveur starting on 1234");
        try {
            ServerSocket gestSock = new ServerSocket(1234);

            while (true) {
                // Accepter les connexions tant que le nombre de joueurs est inférieur à la limite
                if (clients.size() < nbJoeur) {
                    Socket socket = gestSock.accept();
                    System.out.println("Nouveau client connecté");

                    // Création et démarrage d'un thread pour chaque client connecté
                    ClientHandler clientHandler = new ClientHandler(socket);
                    clients.add(clientHandler); // Ajout du client à la liste
                    new Thread(clientHandler).start(); // Lancement du thread
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour démarrer une nouvelle partie avec un niveau spécifique
    public static void newPartie(int indexLevel) {
        totalNonMineCasesOnline = champ.getWidth() * champ.getHeight() - tabNbMines[indexLevel];
        revealedCasesOnline = 0; // Réinitialiser le compteur de cases révélées
        champ.newPartie(indexLevel); // Démarrer une nouvelle partie
        champSuivis = new boolean[tabSize[indexLevel]][tabSize[indexLevel]]; // Réinitialiser les cases suivies
    }

    // Méthode principale pour démarrer le serveur
    public static void main(String[] args) {
        System.out.println("Serveur started");
        new Serveur(); // Démarrer une instance du serveur
    }

    // Classe interne pour gérer chaque client connecté
    private static class ClientHandler implements Runnable {
        private Socket socket;
        private DataInputStream entree; // Flux d'entrée pour lire les messages du client
        private DataOutputStream sortie; // Flux de sortie pour envoyer des messages au client
        private boolean connected = true; // Indique si le client est toujours connecté
        private String nomJoueur; // Nom du joueur associé à ce client

        // Constructeur qui prend un socket et le stocke
        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                // Ouverture des flux d'entrée et de sortie
                entree = new DataInputStream(socket.getInputStream());
                sortie = new DataOutputStream(socket.getOutputStream());

                // Boucle principale pour écouter les messages du client
                while (connected) {
                    String message = entree.readUTF(); // Lire le message envoyé par le client
                    // Traitement des différents types de messages reçus du client
                    switch (message) {
                        case "cord": // Cas où le client envoie des coordonnées
                            System.out.println("Serveur : Envoie des coordonnées");
                            int x = entree.readInt();
                            int y = entree.readInt();

                            // Vérifier si la case a déjà été cliquée
                            if (champSuivis[x][y]) {
                                System.out.println("Serveur : " + nomJoueur + " a déjà cliqué sur cette case");
                            } else {
                                // Marquer la case comme révélée et envoyer l'information aux autres joueurs
                                champSuivis[x][y] = true;
                                broadcastMessage("revealCase");
                                broadcastMessageInt(x);
                                broadcastMessageInt(y);
                                System.out.println("Serveur : " + nomJoueur + " a cliqué sur " + x + " " + y);
                                revealedCasesOnline++; // Incrémenter le compteur de cases révélées
                            }

                            // Vérifier si la case cliquée est une mine
                            if (champ.isMine(x, y)) {
                                System.out.println("Serveur : " + nomJoueur + " a perdu");
                            }

                            // Vérifier si toutes les cases sans mines ont été révélées
                            if (revealedCasesOnline == totalNonMineCasesOnline) {
                                System.out.println("Serveur : Partie terminée");
                                broadcastMessage("end"); // Envoyer un message de fin de partie
                            }
                            break;
                        case "auth": // Cas où le client envoie son nom pour l'authentification
                            nomJoueur = entree.readUTF();
                            System.out.println("Serveur : " + nomJoueur + " connecté");
                            playersName[clients.size() - 1] = nomJoueur; // Stocker le nom du joueur
                            sortie.writeInt(clients.size() - 1); // Envoyer le numéro du joueur au client
                            sortie.writeInt(nbJoeur); // Envoyer le nombre total de joueurs au client
                            break;
                        case "init": // Cas où le client demande à initialiser le niveau
                            sortie.writeInt(level.ordinal()); // Envoyer le niveau actuel au client
                            System.out.println("Serveur : Envoie du niveau de difficulté " + level.ordinal());
                            break;
                        case "isMine": // Cas où le client vérifie si une case est une mine
                            int i = entree.readInt();
                            int j = entree.readInt();
                            sortie.writeBoolean(champ.isMine(i, j)); // Répondre au client si la case est une mine
                            break;
                        case "nbMinesaround": // Cas où le client demande le nombre de mines autour d'une case
                            int x1 = entree.readInt();
                            int y1 = entree.readInt();
                            sortie.writeInt(champ.nbMinesaround(x1, y1)); // Envoyer le nombre de mines adjacentes
                            break;
                        case "newGame": // Cas où le client souhaite rejouer
                            wantReplay++;
                            System.out.println("Serveur : " + nomJoueur + " veut rejouer");
                            // Si tous les joueurs veulent rejouer, démarrer une nouvelle partie
                            if (wantReplay == nbJoeur) {
                                newPartie(level.ordinal());
                                wantReplay = 0;
                                broadcastMessage("newchamp");
                                broadcastMessage(Integer.toString(level.ordinal()));
                            }
                            break;
                        case "exit": // Cas où le client quitte le jeu
                            connected = false; // Marquer le client comme déconnecté
                            sortie.close();
                            entree.close();
                            socket.close();
                            clients.remove(this); // Retirer le client de la liste
                            System.out.println("Serveur : " + nomJoueur + " déconnecté");
                            nbJoeursWantStart--;
                            wantReplay--;
                            break;
                        case "wantstart": // Cas où le client veut commencer une partie
                            System.out.println("Serveur : " + nomJoueur + " veut commencer");
                            nbJoeursWantStart++;
                            // Si tous les joueurs sont prêts, démarrer la partie
                            if (nbJoeursWantStart == nbJoeur) {
                                nbJoeursWantStart = 0;
                                broadcastMessage("start");

                                // Envoie des noms de joueurs la première fois que la partie commence
                                if (firstStart) {
                                    firstStart = false;
                                    broadcastMessage("namesPlayers");
                                    for (int k = 0; k < nbJoeur; k++) {
                                        broadcastMessage(playersName[k]);
                                    }
                                }
                            }
                            break;
                        default:
                            break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    // Méthode pour envoyer un message à tous les clients connectés
    public synchronized static void broadcastMessage(String message) {
        for (ClientHandler client : clients) {
            try {
                client.sortie.writeUTF(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Méthode pour envoyer un entier à tous les clients connectés
    public synchronized static void broadcastMessageInt(int message) {
        for (ClientHandler client : clients) {
            try {
                client.sortie.writeInt(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
