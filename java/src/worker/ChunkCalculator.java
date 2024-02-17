package worker;


import java.io.IOException;
import java.io.ObjectOutputStream;
import common.ResultObject;
import common.Chunk;
import common.Waypoint;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;


public class ChunkCalculator implements Runnable {
    
    protected final Chunk chunk;
    protected ResultObject result;
    protected final ObjectOutputStream out;

    public ChunkCalculator(Chunk chunk, ObjectOutputStream out)
    {
        
        this.out = out;
        this.chunk = chunk;
    }

    @Override
    public void run() {
        
        result = new ResultObject();
        result = Calculate(chunk);

        //Στη μέθοδο run(), το αντικείμενο out περικλείεται σε synchronized block, έτσι ώστε μόνο ένα νήμα να μπορεί να το προσπελάσει κάθε φορά και να αποφεύγεται η ανταγωνιστική πρόσβαση.
        synchronized (out) {
            try {
                System.out.println("Calculating chunk No: "+chunk.getChunkid());
                out.writeObject(result);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
	
    private ResultObject Calculate(Chunk chunk){
            double distance = 0;
            long totalTime = 0;
            double totalAscent = 0;
            double totalSpeed = 0;

            // Υπολογίζει την απόσταση, τον συνολικό χρόνο, την συνολική ανάβαση και τη μέση ταχύτητα
            distance = calculateTotalDistance(chunk);
            totalTime = calculateTotalTime(chunk);
            totalAscent = calculateTotalElevation(chunk);
            totalSpeed = distance / totalTime;

            return new ResultObject(chunk.getCreator(), distance, totalSpeed, totalAscent, totalTime);
    }

    private double calculateTotalDistance(Chunk chunk) {
            double totalDistance = 0;
            List<Waypoint> waypoints = chunk.getWaypoints();

            // Υπολογισμός της απόστασης μεταξύ των συνεχόμενων waypoints και προσθήκη στον συνολικό υπολογισμό
            for (int i = 0; i < waypoints.size() - 1; i++) {
                    Waypoint w1 = waypoints.get(i);
                    Waypoint w2 = waypoints.get(i + 1);
                    double distance = distanceBetweenPoints(w1.getLatitude(), w1.getLongitude(), w2.getLatitude(), w2.getLongitude());
                    totalDistance += distance;
            }

            return totalDistance;
    }

    // Υπολογισμός της απόστασης μεταξύ δύο σημείων στην επιφάνεια της γης με την φόρμουλα του Haversine
    private double distanceBetweenPoints(double lat1, double lon1, double lat2, double lon2) {
            final int R = 6371; // Μέση ακτίνα της Γης σε χιλιόμετρα
            double latDistance = Math.toRadians(lat2 - lat1);
            double lonDistance = Math.toRadians(lon2 - lon1);
            double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                            + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                            * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            double distance = R * c;
            return distance;
    }

    private static double calculateTotalElevation(Chunk chunk) {
            double totalElevation = 0.0;
            List<Waypoint> waypoints = chunk.getWaypoints();
            for (int i = 1; i < waypoints.size(); i++) {
                    double elevationDiff = waypoints.get(i).getElevation() - waypoints.get(i-1).getElevation();
                    if (elevationDiff > 0) {
                            totalElevation += elevationDiff;
                    }
            }
            return totalElevation;
    }

    private static long calculateTotalTime(Chunk chunk) {

        DateTimeFormatter DtFormat =  DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        List<Waypoint> waypoints = chunk.getWaypoints();

        int size = waypoints.size();

        if (size <= 1) {
            return 0;
        }
        LocalDateTime dt1, dt2;
        Waypoint first = waypoints.get(0);
        Waypoint last = waypoints.get(size - 1);


        // Υπολογίζει τις αντιστοιχες LocalDateTime
        dt1 = LocalDateTime.parse(first.getTime(), DtFormat);
        dt2 = LocalDateTime.parse(last.getTime(), DtFormat);

        return Duration.between(dt1, dt2).getSeconds();
    }

}