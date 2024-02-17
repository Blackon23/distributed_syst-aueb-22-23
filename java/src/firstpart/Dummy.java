package firstpart;

import common.ResultObject;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Dummy {

    public static void main(String[] args) {
        
        try {
            String masterIP = "localhost";
            int masterPort = 9000;
            
            // Συνδέουμε τον client στον server
            Socket socket = new Socket(masterIP, masterPort);
            
            // Στέλνουμε το αρχείο GPX στον server
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            
            outputStream.writeObject(readFile("route2.gpx"));
            
            // Παίρνουμε τα αποτελέσματα από τον server
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            
            // Παίρνει τα ατομικά αποτελέσματα
            ResultObject ro = (ResultObject) inputStream.readObject();
            
            // Παίρνει τα συνολικά αποτελέσματα
            ResultObject tro = (ResultObject) inputStream.readObject();
            
            // Κλείνουμε το socket
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(Dummy.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Dummy.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
	
    private static String readFile(String gpxFilePath) throws IOException {
        File file = new File(gpxFilePath);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String gpx = "", line;
        while ((line = br.readLine()) != null) {
			gpx += line + "\n";
        }
        br.close();
		
		return gpx;
    }
	
}
		