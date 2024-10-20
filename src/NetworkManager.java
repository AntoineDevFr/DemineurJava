import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class NetworkManager {
    private Socket sock;
    private DataOutputStream out;
    private DataInputStream in;
    private App app;
    private int nbJoeurs;

    public NetworkManager(App app){
        this.app = app;

    }

    public void connect(String host, int port) throws IOException {
        sock = new Socket(host, port);
        app.online = true;
        out = new DataOutputStream(sock.getOutputStream());
        in = new DataInputStream(sock.getInputStream());
        System.out.println("Connected to server at " + host + ":" + port);
    }

    public void auth(String namePlayer) throws IOException
    {
        out.writeUTF("auth");
        out.writeUTF(app.playerName);

        int numJoueur = in.readInt(); 
        nbJoeurs = in.readInt();

        System.out.println("Joueur n°:"+numJoueur); 
        System.out.println("Connecté au serveur");

        out.writeUTF("init");
        int level = in.readInt();
        System.out.println("Level : " + level);

        //Lance une nouvelle partie en fonction du niveau
        app.gui.newPartie(level);
    } 

    public void sendStart() {
        try {
            out.writeUTF("wantstart");
            System.out.println("Je veux commencer");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isMineOnline(int i, int j) {
        boolean response = false;
        try {
            out.writeUTF("isMine");
            out.writeInt(i);
            out.writeInt(j);
            response = in.readBoolean();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    
    public int nbMinesaroundOnline(int i, int j) {
        int count = 0;
        try {
            out.writeUTF("nbMinesaround");
            out.writeInt(i);
            out.writeInt(j);
            count = in.readInt();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return count;
    }

    public void sendMoveOnline(int x, int y)
    {
        try {
            out.writeUTF("cord");
            out.writeUTF(String.valueOf(x));
            out.writeUTF(String.valueOf(y));

            // if(in.readUTF().equals("revealCase"))
            // {
            //     app.revealCaseOnline(x, y);
            // }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void newGame() {
        try {
            out.writeUTF("newGame");
            //int level = in.readInt();
            //System.out.println("je veux refaire une partie avec le niveau: " + level);
            //app.gui.newPartie(level);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void exit() {
        try {
            out.writeUTF("exit");
            sock.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startListening() {
        Thread listenerThread = new Thread(() -> {
            try {
                while (!sock.isClosed()) {  // Check if the socket is still open
                    if (in.available() > 0) {  // Check if there is data to read
                        String message = in.readUTF();
                        
                        switch (message) {
                            case "revealCase":
                                int x = Integer.parseInt(in.readUTF());  // Assuming the server sends coordinates
                                int y = Integer.parseInt(in.readUTF());
                                if (!app.isWaiting) {
                                    app.revealCaseOnline(x, y);
                                }
                                break;
                            case "start" :
                                System.out.println("Je reçois start");
                                app.gui.startgame = true;
                                app.gui.waitingDialog.dispose();
                                break;
                            case "newchamp":
                                int level = Integer.parseInt(in.readUTF());
                                app.gui.newPartie(level);
                                break;
                            case "namesPlayers":
                                String[] playersName = new String[nbJoeurs];
                                for (int i = 0; i < nbJoeurs; i++) {
                                    playersName[i] = in.readUTF();
                                    System.out.println("Je recois: le nom de "+playersName[i]);
                                }
                                app.gui.addPlayers(playersName);
                                break;
                            case "end":
                                app.winGame();
                                break;
                            default:
                                break;
                        }
                        // Handle other potential messages
                    } else {
                        // No data to read, sleep briefly to avoid busy-waiting
                        Thread.sleep(100);
                    }
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
        listenerThread.start();
    }    

}
