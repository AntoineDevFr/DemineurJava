import java.net.*; // Sockets
import java.io.*; // Streams
import java.util.ArrayList;
import java.util.List;

public class Serveur {
    // Liste pour stocker les clients connectés
    private static List<ClientHandler> clients = new ArrayList<>();
    public static Champ champ = new Champ();
    public static Level level = Level.EASY;
    public static boolean champSuivis[][];

    private static int totalNonMineCasesOnline; 
    private static int revealedCasesOnline = 0;
    private static int nbJoeursWantStart = 0;
    private static int wantReplay = 0;

    private final int[] tabSize = {5, 10, 15, 0};  // Last element is for CUSTOM
    private final int[] tabNbMines = {3, 7, 20, 0};

    public Serveur() {
        champ.init(level.ordinal());
        champSuivis = new boolean[tabSize[level.ordinal()]][tabSize[level.ordinal()]];
        System.out.println(champSuivis.length);
        System.out.println("Champ initialisé du serveur");
        totalNonMineCasesOnline = champ.getWidth() * champ.getHeight() - tabNbMines[level.ordinal()];

        champ.display();
        System.out.println("Serveur starting on 1234");
        try {
            ServerSocket gestSock = new ServerSocket(1234);

            while (true) {
                // Vérifier s'il y a déjà 2 clients connectés
                if (clients.size() < 2) {
                    Socket socket = gestSock.accept();
                    System.out.println("Nouveau client connecté");

                    // Créer un gestionnaire de client dans un thread
                    ClientHandler clientHandler = new ClientHandler(socket);
                    clients.add(clientHandler); // Ajouter le client à la liste
                    new Thread(clientHandler).start(); // Démarrer le thread
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void newPartie(int indexLevel) {
        totalNonMineCasesOnline = champ.getWidth() * champ.getHeight() - tabNbMines[indexLevel];
        revealedCasesOnline = 0;
        champ.newPartie(indexLevel);
        champSuivis = new boolean[tabSize[indexLevel]][tabSize[indexLevel]];
    }

    public static void main(String[] args) {
        new Serveur();
    }

    // Classe interne pour gérer chaque client
    private static class ClientHandler implements Runnable {
        private Socket socket;
        private DataInputStream entree;
        private DataOutputStream sortie;
        private boolean connected = true;
        private String nomJoueur;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                // Ouverture des streams
                entree = new DataInputStream(socket.getInputStream());
                sortie = new DataOutputStream(socket.getOutputStream());

                while (connected) {
                    String message = entree.readUTF();
                    // Traiter les messages du client
                    switch (message) {
                        case "cord":
                            System.out.println("Serveur : Envoie des coordonnées");
                            String x = entree.readUTF();
                            String y = entree.readUTF();

                            if (champSuivis[Integer.parseInt(x)][Integer.parseInt(y)]) {
                                System.out.println("Serveur : " + nomJoueur + " a déjà cliqué sur cette case");
                            } else {
                                champSuivis[Integer.parseInt(x)][Integer.parseInt(y)] = true;
                                broadcastMessage("revealCase");
                                broadcastMessage(x);
                                broadcastMessage(y);
                                System.out.println("Serveur : " + nomJoueur + " a cliqué sur " + x + " " + y);
                                revealedCasesOnline++;
                            }
                            System.out.println(nomJoueur + " : " + x + " " + y);

                            if (champ.isMine(Integer.parseInt(x), Integer.parseInt(y))) {
                                System.out.println("Serveur : " + nomJoueur + " a perdu");
                            }

                            if (revealedCasesOnline == totalNonMineCasesOnline) {
                                System.out.println("Serveur : Partie terminée");
                                broadcastMessage("end");
                            }
                            break;
                        case "auth":
                            nomJoueur = entree.readUTF();
                            System.out.println("Serveur : " + nomJoueur + " connecté");
                            sortie.writeInt(clients.size());
                            break;
                        case "init":
                            sortie.writeInt(level.ordinal());
                            System.out.println("Serveur : Envoie du niveau de difficulté " + level.ordinal());
                            break;
                        case "isMine":
                            int i = entree.readInt();
                            int j = entree.readInt();
                            sortie.writeBoolean(champ.isMine(i,j));
                            break;
                        case "nbMinesaround":
                            int x1 = entree.readInt();
                            int y1 = entree.readInt();
                            sortie.writeInt(champ.nbMinesaround(x1, y1));
                            break;
                        case "newGame":
                            wantReplay++;
                            System.out.println("Serveur : " + nomJoueur + " veut rejouer");
                            if (wantReplay < 2) {
                                champ.newPartie(level.ordinal());
                            }
                            else if (wantReplay == 2) {
                                wantReplay = 0;
                            }
                            sortie.writeInt(level.ordinal());
                            break;
                        case "exit":
                            connected = false;
                            // Fermeture des streams et du socket
                            sortie.close();
                            entree.close();
                            socket.close();
                            clients.remove(this); // Retirer le client de la liste
                            break;
                        case "wantstart":
                            System.out.println("Serveur : " + nomJoueur + " veut commencer");
                            nbJoeursWantStart++;
                            if (nbJoeursWantStart == 2) {
                                nbJoeursWantStart = 0;
                                broadcastMessage("start");
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

    public static void broadcastMessage(String message) {
        for (ClientHandler client : clients) {
            try {
                client.sortie.writeUTF(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
