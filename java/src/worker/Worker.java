package worker;

import common.Node;
import worker.ChunkCalculator;
import common.Chunk;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;


public class Worker extends Node{

    private Socket socket;
    private ObjectInputStream in;
        
    private ObjectOutputStream out;
	
    public Worker() {
        super();
    }

    public Worker(String brokerIp, int brokerPort) {
        this.ipAddress = brokerIp;
        this.port = brokerPort;
    }
		
    public void start() {
        try {
            // Σύνδεση με τον Master
            socket = connect(ipAddress, port);
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
            

            // Δημιουργεί νήματα για τον υπολογισμό των chunks
            while (true) {
                
                Chunk chunk;
                
                synchronized (in) {
                    chunk = (Chunk) in.readObject();
                    System.out.println("Chucnk "+chunk.getChunkid()+"has been arrived");
                }
                //δημιουργειται νημα που κανει τους υπολογισμους
                Thread thread = new Thread(new ChunkCalculator(chunk, out));
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            Disconnect(socket);
        }
    }


    public static void main(String[] args) {
        
        String masterIp = "localhost";
        int masterPort = 8100;

        Worker worker = new Worker(masterIp, masterPort);
        worker.start();
    }

}  