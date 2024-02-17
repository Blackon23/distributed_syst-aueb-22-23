package common;

import firstpart.MasterNode;
import java.io.*;
import java.net.*;
import java.util.*;

public class Waypoint implements Serializable {

    private static final long serialVersionUID = 1L;
    
    // fields for latitude, longitude, elevation and time
    private double latitude;
    private double longitude;
    private double elevation;
    private String time;
	
	public Waypoint() {
    }

    public Waypoint(double latitude, double longitude, double elevation, String time) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.elevation = elevation;
        this.time = time;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getElevation() {
        return elevation;
    }

    public String getTime() {
        return time;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setElevation(double elevation) {
        this.elevation = elevation;
    }

    public void setTime(String time) {
        this.time = time;
    }

}