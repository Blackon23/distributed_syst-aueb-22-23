package firstpart;

import common.Node;
import common.Waypoint;
import common.Chunk;
import common.ResultObject;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class MasterNode extends Node {

    // Port number for communication and chunk size
    private static final int GPX_PORT = 9000;
    private static final int CHUNK_PORT = 8100;
    private static final int CHUNK_SIZE = 35;
    
    private ServerSocket workerserverSocket;
    private List<Socket> WorkerSockets;
    private List<Chunk> outgoing_chunks;
    private List<ResultObject> ingoing_results;
    private HashMap<String, ResultObject> reduced_results;

    private List<Waypoint> tempChunk;
    private int count;      //ΠΛήθος worker
    private Object lock;
    private ServerSocket androidserverSocket;
    private Map map;
    
    public MasterNode() {
        super();
        WorkerSockets = new ArrayList<>();
        outgoing_chunks = new ArrayList<>();
        ingoing_results = new ArrayList<>();
        reduced_results = new HashMap<>();
        map = new Map();
        
        count = 0;
        lock = new Object();
    }

    //copy constructor 
    public MasterNode(int port, String ip) {
        this.ipAddress = ip;
        this.port = port;
        
        WorkerSockets = new ArrayList<>();
        outgoing_chunks = new ArrayList<>();
        ingoing_results = new ArrayList<>();
        map = new Map();
        reduced_results = new HashMap<>();
        
        count = 0;
        lock = new Object();
    }


    public void start() {
        try {

            //dexetai ta waypoints kai ftiaxnei chunks
            System.out.println("Master Node is ready to receive chunks...");

            // Server για τα κινητά. Ένα νήμα για κάθε κινητό.
            androidserverSocket = new ServerSocket(GPX_PORT);
            new Thread(() -> {      gpxServer();      }).start();
            
            // Server για τους worker. Ένα νήμα για κάθε worker
            workerserverSocket = new ServerSocket(CHUNK_PORT);
            new Thread(() -> {      workerServer();      }).start();
            
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            
        }

    }

    public void workerServer() {

        
        while(true) {
            
            try {
                Socket Workersocket = workerserverSocket.accept();
                System.out.println("Worker connected");

                // Νήμα για εξερχόμενη σύνδεση με τον Worker
                new Thread(() -> {
                                
                    int current_worker=0;
                            
                    // κλειδώνει τους κοινούς πόρους
                    synchronized (lock) {
                        current_worker = count;
                        count++;
                    }
                    
                    // Μοιράζει τα chunks στον συγκεκριμένο worker
                    shareChunksToWorker(Workersocket, current_worker);
                    
                }).start();
                
                
                // Νήμα για εισερχόμενη σύνδεση με τον Worker
                new Thread( () -> {
                    
                    receiveResults(Workersocket);
                    
                }).start();
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        
    }
    
    // Συνάρτηση που μοιράζει τα Chunks στο συγκεκριμένο worker
    public void shareChunksToWorker(Socket workerSocket, int current_worker) {
        
        ObjectOutputStream objectOutputStream = null;
        try {
            // Αποστολη chunk στον Worker
            objectOutputStream = new ObjectOutputStream(workerSocket.getOutputStream());
            
            do{
            
                // Κλειδώνει την κρίσιμη περιοχή
                synchronized (lock) {
                    
                    // Περιμένει μέχρι να ειδοποιηθεί ότι υπάρχουν Chunks
                    lock.wait();
                    

                    for( Chunk ck : outgoing_chunks )
                    {
                        // αν ο κωδικός του chunk αντιστοιχεί με το id του Worker τότεο το παίρνει
                        if( current_worker == ck.getChunkid()%count ) {
                            
                            objectOutputStream.writeObject( ck );
                            objectOutputStream.flush();
                            
                            // αφαιρεί το chunk γιατί στάλθηκε
                            outgoing_chunks.remove(ck);
                            
                            System.out.println("Chunk has been sent to worker.");
                            break;
                        }
                    }
                
                }
                
            }while(true);
            
            
            
        } catch (IOException ex) {
            Logger.getLogger(MasterNode.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(MasterNode.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                objectOutputStream.close();
            } catch (IOException ex) {
                Logger.getLogger(MasterNode.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    public void gpxServer(){
        
        while (true) {
            try {

                Socket gpxSocket = androidserverSocket.accept();

                ObjectInputStream gpxInputStream = new ObjectInputStream(gpxSocket.getInputStream());
                ObjectOutputStream gpxOutputStream = new ObjectOutputStream(gpxSocket.getOutputStream());

                // Ληψη gpx απο το Android
                String gpx = (String) gpxInputStream.readObject();
                //gpxInputStream.close();

                //διαχωρίζει τις τιμές lan lon ele time
                List<Object> users = getTags(gpx,"gpx", "creator", null);
                List<Object> latitude = getTags(gpx,"wpt", "lat", null);
                List<Object> longitude = getTags(gpx, "wpt","lon", null);
                List<Object> elevation = getTags(gpx, "wpt",null, "ele");
                List<Object> time = getTags(gpx, "wpt", null, "time");

                List<Waypoint> allWaypoints = new ArrayList<>(); // List to store all waypoints
                
                //gemisma allWaypoints apo to parsing tou gpx
                for (int i = 0; i < latitude.size(); i++) {
                    Waypoint w = new Waypoint();
                    w.setLatitude((double) latitude.get(i));
                    w.setLongitude((double) longitude.get(i));
                    w.setElevation((double) elevation.get(i));
                    w.setTime((String) time.get(i));
                    allWaypoints.add(w);
                }

                int parts=0;
                
                synchronized (lock) {
                    
                    //δημιουργια chunk απο waypoints
                    tempChunk = new ArrayList<>();
                    for (Waypoint wp : allWaypoints) {
                        tempChunk.add(wp);

                        // Ισομερισμός των μεγεθών
                        if (tempChunk.size() == CHUNK_SIZE) {
                            outgoing_chunks.add(new Chunk((String) users.get(0), tempChunk));
                            tempChunk = new ArrayList<>();
                            parts++;
                        }
                    }

                    // αν υπάρχει περίσευμα στο τελευταίο
                    if (!tempChunk.isEmpty()) {
                        outgoing_chunks.add(new Chunk((String) users.get(0),tempChunk));
                        parts++;
                    }
                
                    System.out.println("Total parts "+parts);
                
                    map.put( (String) users.get(0), parts );
                    
                    lock.notifyAll();
                }
                
                
                
                boolean readyToReduceResults = false;
                do{
                    
                    synchronized (lock) {
                        lock.wait();
                        readyToReduceResults = map.isReady((String) users.get(0));
                    }
                    
                }while(readyToReduceResults);

                
                String username = (String) users.get(0);
                
                ResultObject reducedObject = new ResultObject((String) users.get(0));
                
                
                synchronized (lock) {
                
                    // Κάνει reduced τα αποτελεσματα
                    double totalDistanceR = ingoing_results.stream()
                            .filter((x) -> ( x.getCreator().equals(username) ))
                            .mapToDouble(ResultObject::getTotalDistance)
                            .sum();


                    // Reduce function: calculate average speed
                    double averageSpeedR = ingoing_results.stream()
                            .filter((x) -> ( x.getCreator().equals(username) ))
                            .mapToDouble(ResultObject::getAverageSpeed)
                            .sum() / ingoing_results.stream()
                            .filter((x) -> ( x.getCreator().equals(username) )).count();

                    
                    // Reduce function: calculate total elevation
                    double totalElevationR = ingoing_results.stream()
                            .filter((x) -> ( x.getCreator().equals(username) ))
                            .mapToDouble(ResultObject::getTotalElevation)
                            .sum();

                    // Reduce function: calculate total time
                    double totalTimeR = ingoing_results.stream()
                            .filter((x) -> ( x.getCreator().equals(username) ))
                            .mapToDouble(ResultObject::getTotalTime)
                            .sum();

                    reducedObject = new ResultObject(username, totalDistanceR, averageSpeedR, totalElevationR, (long)totalTimeR);
                    
                    // Βάζει το αποτέλεσμα στο Map με τα τελικά αποτελέσματα του κάθε χρηστη
                    reduced_results.put(username, reducedObject);
                    
                    // Σβήνει τα επεξεργασμένα
                    ingoing_results.removeIf( ((x) -> ( x.getCreator().equals(username) )));
                }
                
                // Στέλνει πίσω το reduced αποτέλεσμα του χρήστη
                gpxOutputStream.writeObject( reducedObject );
                
                
                // Υπολογίζει τα συνολικά αποτελεσματα
                synchronized (lock) {
                
                    
                    double totalDistanceR = reduced_results.values().stream()
                            .mapToDouble(ResultObject::getTotalDistance)
                            .sum();


                    // Reduce function: calculate average speed
                    double averageSpeedR = reduced_results.values().stream()
                            .mapToDouble(ResultObject::getAverageSpeed)
                            .sum()
                            / reduced_results.values().stream().count();

                    
                    // Reduce function: calculate total elevation
                    double totalElevationR = reduced_results.values().stream()
                            .mapToDouble(ResultObject::getTotalElevation)
                            .sum();

                    // Reduce function: calculate total time
                    double totalTimeR = reduced_results.values().stream()
                            .mapToDouble(ResultObject::getTotalTime)
                            .sum();

                    reducedObject = new ResultObject(username, totalDistanceR, averageSpeedR, totalElevationR, (long)totalTimeR);
                }
                
                // Στέλνει πίσω τα συνολικά αποτελέσματα
                gpxOutputStream.writeObject( reducedObject );

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
    }
    
    public void receiveResults(Socket workerSocket) {
        
        ObjectInputStream objectInputStream = null;
        
        try {
            // Ανάγνωση του chunk από τον Worker
            objectInputStream = new ObjectInputStream(workerSocket.getInputStream());
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
        do{
            try{
                
                // Διαβάζει το αποτέλεσμα απο τον Worker
                ResultObject ro = (ResultObject) objectInputStream.readObject();
                
                // Κλειδώνει τους κοινούς πόρους
                synchronized (lock) {
                
                    System.out.println("Result has been read");
                    ingoing_results.add(ro);
                    
                    map.reduce_user_results(ro.getCreator());
                    
                    lock.notifyAll();
                }
                
                
                
            }catch(Exception e) {
                e.printStackTrace();
            }
            
        }while (true);
    }

    /* Ξεχωρίζει από το αρχείο τα tags με βάση τα attributes */
    private List<Object> getTags(String gpx, String tag, String attribute, String subtag) {
        
        List<Object> list = new ArrayList<>();
        
        try {
        
            DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = fact.newDocumentBuilder();
            Document document;
        
            document = dBuilder.parse(new InputSource(new StringReader(gpx)));
            document.getDocumentElement().normalize();    
            
            NodeList wptList = document.getElementsByTagName(tag);
                
            for (int i = 0; i < wptList.getLength(); i++) {
                
                Element wpt = (Element) wptList.item(i);
                
                if(attribute != null){
                    
                    if(attribute.equals("creator"))
                        list.add((wpt.getAttribute(attribute)));
                    else
                        list.add(Double.parseDouble(wpt.getAttribute(attribute)));
                }
                else {
                    if(subtag.equals("time"))
                        list.add((wpt.getElementsByTagName(subtag).item(0).getTextContent()));
                    else
                        list.add(Double.parseDouble(wpt.getElementsByTagName(subtag).item(0).getTextContent()));
                }
            }
            
            
        } catch (SAXException ex) {
            Logger.getLogger(MasterNode.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MasterNode.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(MasterNode.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally{
            return list;
        }
    }
    
    public static void main(String[] args) throws IOException {
        MasterNode master = new MasterNode();
        master.start();
    }
}