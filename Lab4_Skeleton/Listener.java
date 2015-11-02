import java.net.*;
import java.io.*;

public class Listener implements Runnable {
    private Socket socket;
    private int nPack;

    public Listener (Socket s, int n){
        System.out.println("Listener object is created!");
        socket = s; 
        nPack = n;
    }

    @Override 
    public void run() {
        System.out.println("Listener object run() method is called!");
        try{ 
            // BufferedReader socket_reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            DataInputStream readInputStream = new DataInputStream(socket.getInputStream());

            while(true){
                byte[] buffer = new byte[8];
                readInputStream.read(buffer);
                int ackNum = (int)(buffer[0]);
                System.out.println("Client is setting ack to " + ackNum);
                CCClient.update(ackNum);
                if (ackNum == nPack)
                    break;
            }
        }
        catch (Exception e) {e.getStackTrace();}
        
    }
}
