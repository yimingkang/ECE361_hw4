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
            BufferedReader socket_reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // DataInputStream readInputStream = new DataInputStream(socket.getInputStream());

            // byte[] buffer = new byte[8];
            while(true){
                // readInputStream.read(buffer);
                // int ackNum = (int)(buffer[0]);
                int ackNum = socket_reader.read();
                // System.out.println("BufferedReader gets:" + input);
                // int ackNum = Integer.parseInt(input);
                System.out.println("Client receives ack num: " + ackNum);
                if (ackNum > CCClient.lastAck)
                    CCClient.update(ackNum);
                if (ackNum == nPack)
                    break;
            }
        }
        catch (Exception e) {e.getStackTrace();}
        
    }
}
