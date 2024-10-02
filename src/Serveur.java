import java.net.*; // Sockets
import java.io.*; // Streams
import java.util.ArrayList;
import java.util.List;

public class Serveur {
    // Liste pour stocker les clients connectés
    private static List<ClientHandler> clients = new ArrayList<>();
    public static Champ champ = new Champ();
    public static Level level = Level.MEDIUM;

    public Serveur() {
        champ.init(1);
        System.out.println("Champ initialisé du serveur");
        champ.display();
        System.out.println("Serveur starting on 1234");
        try {
            ServerSocket gestSock = new ServerSocket(1234);

            while (true) { 
                Socket socket = gestSock.accept();
                System.out.println("Nouveau client connecté");

                // Créer un gestionnaire de client dans un thread
                ClientHandler clientHandler = new ClientHandler(socket);
                clients.add(clientHandler); // Ajouter le client à la liste
                new Thread(clientHandler).start(); // Démarrer le thread
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
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

                while(connected) {
                    String message = entree.readUTF();
                    // Traiter les messages du client
                    switch (message) {
                        case "cord":
                            System.out.println("Serveur : Envoie des coordonnées");
                            String x = entree.readUTF();
                            String y = entree.readUTF();
                            System.out.println(nomJoueur + " : " + x + " " + y);
                            break;
                        case "auth":
                            nomJoueur = entree.readUTF();
                            System.out.println("Serveur : " + nomJoueur + " connecté");
                            sortie.writeInt(clients.size());
                            break;
                        case "init":
                            sortie.writeInt(level.ordinal());
                            System.out.println("Serveur : Envoie du niveau de difficulté"+level.ordinal());
                            break;
                        case "isMine":
                            String i = entree.readUTF();
                            String j = entree.readUTF();
                            sortie.writeBoolean(champ.isMine(Integer.parseInt(i), Integer.parseInt(j)));
                            System.out.println(nomJoueur + "is Mine pour les cases : " + i + " " + j);
                            break;
                        case "nbMinesaround":
                            String x1 = entree.readUTF();
                            String y1 = entree.readUTF();
                            sortie.writeInt(champ.nbMinesaround(Integer.parseInt(x1), Integer.parseInt(y1)));
                            System.out.println(nomJoueur + "nbMinesAround : " + x1 + " " + y1);
                            break;
                        case "exit":
                            connected = false;
                             // Fermeture des streams et du socket
                            sortie.close();
                            entree.close();
                            socket.close();
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
}