import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class NetworkManager {
    private Socket sock;
    private DataOutputStream out;
    private DataInputStream in;
    private App app;

    public NetworkManager(App app){
        this.app = app;

    }

    public synchronized void connect(String host, int port) throws IOException {
        sock = new Socket(host, port);
        app.online = true;
        out = new DataOutputStream(sock.getOutputStream());
        in = new DataInputStream(sock.getInputStream());
        System.out.println("Connected to server at " + host + ":" + port);
        startListening();
    }

    public void auth(String namePlayer) throws IOException
    {
        out.writeUTF("auth");
        out.writeUTF("Antoine");

        int numJoueur = in.readInt(); 

        System.out.println("Joueur n°:"+numJoueur); 
        System.out.println("Connecté au serveur");

        out.writeUTF("init");
        int level = in.readInt();
        System.out.println("Level : " + level);
        app.gui.newPartie(level);
    } 

    public boolean isMineOnline(int i, int j) {
        boolean response = false;
        try {
            out.writeUTF("isMine");
            out.writeUTF(String.valueOf(i));
            out.writeUTF(String.valueOf(j));
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
            out.writeUTF(String.valueOf(i));
            out.writeUTF(String.valueOf(j));
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

    public void startListening() {
        Thread listenerThread = new Thread(() -> {
            try {
                while (!sock.isClosed()) {  // Check if the socket is still open
                    if (in.available() > 0) {  // Check if there is data to read
                        String message = in.readUTF();
                        
                        // Handle broadcast message
                        if (message.equals("revealCase")) {
                            int x = Integer.parseInt(in.readUTF());  // Assuming the server sends coordinates
                            int y = Integer.parseInt(in.readUTF());
                            app.revealCaseOnline(x, y);  // Call the method to reveal the case on the GUI
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
