import java.net.* ; // Sockets
import java.io.* ; // Streams

public class Serveur {
    public static void main(String [] args) {
    System.out.println("Démarrage du serveur") ; try {// gestionnaire de socket, port 10000
    ServerSocket gestSock=new ServerSocket(10000);
    Socket socket=gestSock.accept() ; //attente // ouverture des streams
    DataInputStream entree = new DataInputStream(socket.getInputStream());
    DataOutputStream sortie = new DataOutputStream(socket.getOutputStream()); // lecture d’une donnée
    String nomJoueur = entree.readUTF() ;
    System.out.println(nomJoueur+"connected"); // envoi d ’une donnée : 0 par exemple
    sortie.writeInt(0); // un peu de ménage
    sortie.close() ; entree.close() ;
    socket.close();
    gestSock.close() ;
    } catch (IOException e) {e.printStackTrace( );}
    }
}