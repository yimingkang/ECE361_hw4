import java.awt.Color;
import java.awt.image.SampleModel;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;



public class CCClient {

	static String host;
	static int port;
	final static String CRLF="\r\n";
	public static int wstart;
	static long totalTime;
	static int timeOut;
	public static int lastAck = 0;
	static int sent = 1;
	static long[] send_timer;
	
	static long startTime;
	static long endTime;
	public static int EstimatedRTT;
	public static int DevRTT;
	public static int SampleRTT;
	public static final double alpha=0.125;
	public static final double beta=0.25;
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		host="localhost";
		port=9876;
		try 
		{
            Socket socket = new Socket(host, port);
			socket.setTcpNoDelay(true); 
			System.out.println("Connected to : "+ host+ ":"+socket.getPort());

			//reader and writer:
			//BufferedReader reader=...
            DataOutputStream writer = new DataOutputStream(socket.getOutputStream());
			Scanner scr = new Scanner(System.in);

			System.out.println("Enter number of packets to be sent to the server [0-127], 0 to Quit: ");
			int noPackets = scr.nextInt();

			//define the thread and start it
            Thread thread = new Thread(new Listener(socket, noPackets));
            thread.start();
                
			 

			
			//send the noPackets to the server
            writer.write(noPackets);
			
			
			EstimatedRTT=1200;
			DevRTT=0;
			timeOut = EstimatedRTT+4*DevRTT; //in milliseconds
			lastAck=0;
			sent=0;
			int cwnd = 1;
			int ssthresh = 8;
            int second_last_ack = 0;
			int RTT_count = 0;

			startTime=System.currentTimeMillis();
			try {
				while(sent<noPackets)
				{
                    if (lastAck != second_last_ack){
                        // adjust cwnd size (slow start)
                        int diff = lastAck - second_last_ack;
                        System.out.println("Got " + diff + " more acks, before=" + cwnd);
                        while (diff > 0 && cwnd <= ssthresh){
                            cwnd *= 2;
                            diff -= 1;
                        }

                        // adjust cwnd size (avoidance)
                        cwnd += diff/cwnd;
                        System.out.println("Adjusted cwnd=" + cwnd);

                        // calculate 'unused' diff' and subtract from lastAck
                        second_last_ack = lastAck - (diff % cwnd);
                    }
                    while (sent - lastAck <= cwnd && sent <= noPackets){
                        System.out.println("Client sending packet: " + sent);
                        writer.write(sent);
                        sent +=1;
                        startTime = System.currentTimeMillis();
                    }
                    if ((System.currentTimeMillis() - startTime) > timeOut){
                        System.out.println("Timeout!");
                        
                        // reset timer
                        startTime = System.currentTimeMillis();
                        
                        // reset sent
                        sent = lastAck+1;

                        // reset sshthresh and cwnd
                        cwnd = 1;
                        ssthresh /= 2;
                    }
					//THE MAIN PART OF THE CODE!
					//send the packets with congestion control using the given instructions
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			finally {
				endTime = System.currentTimeMillis();
				totalTime = endTime - startTime;
			}
			
			
			//print the total taken time, number of sucessfully sent packets, etc. 
			//...
			
			writer.flush();
			socket.close();
			System.out.println("Quitting...");
			socket.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static void update(int ackNum)
	{
		//update lastAck here. note that last ack is accumulative, 
		//i.e., if ack for packet 10 is previously received and now ack for packet 7 is received, lastAck will remain 10
        System.out.println("Setting ack to " + ackNum);
        CCClient.lastAck = ackNum;
	}

}
