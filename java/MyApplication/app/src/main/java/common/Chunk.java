package common;


import java.io.*;

import java.util.*;

// Class to represent a chunk of waypoints
public class Chunk implements Serializable {

    private static final long serialVersionUID = 1L;
    private List<Waypoint> waypoints;
    private String creator;
    
    private static int chunkCounter = 0;
    private int chunkID;            // Chunk Id

    public Chunk(String cr, List<Waypoint> waypoints) {
        this.waypoints = new ArrayList<>(waypoints);
        creator = cr;
        
        // Χρησιμο για το Round Robin.
        // Κάθε chunk % πλήθος worker μας λέει, ποιός Worker θα το αναλάβει
        // ώστε να είναι μοιρασμένο και κυκλικό
        chunkID = chunkCounter;
        chunkCounter++;
    }
    
    // Getters
    public List<Waypoint> getWaypoints() {
        return waypoints;
    }
    
    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getCreator() {
        return creator;
    }

    public int getChunkid() {
        return chunkID;
    }
    
    
}
