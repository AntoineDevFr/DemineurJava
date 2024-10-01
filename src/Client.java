import java.net.* ;
import java.io.* ;
public class Client {
    public static void main(String [] args ) {
    try {// ouverture de la socket et des streams

        Socket sock = new Socket("localhost",1234);
        DataOutputStream out =new DataOutputStream(sock.getOutputStream()); DataInputStream in = new DataInputStream(sock.getInputStream());
        if (args.length > 0) // envoi du nom
        out.writeUTF(args[0]); else
        out.writeUTF("Gros Bill");
        int numJoueur = in.readInt(); // reception d’un nombre System.out.println("Joueur n°:"+numJoueur); in.close(); // fermeture Stream
        out.close();
        sock.close() ; // fermeture Socket
        
    } catch (UnknownHostException e) {
        System.out.println("R2D2 est inconnue");
    } catch (IOException e) {
        e.printStackTrace();}
    }

}