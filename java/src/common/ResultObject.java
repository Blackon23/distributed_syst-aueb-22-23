package common;

import java.io.Serializable;

public class ResultObject implements Serializable{
    
    private String creator;
    private double totalDistance;
    private double averageSpeed;
    private double totalElevation;
    private long totalTime;
	
    public ResultObject() {
    }
    
    public ResultObject(String creat) {
        creator = creat;
    }

    public ResultObject(String creator, double totalDistance, double averageSpeed, double totalElevation, long totalTime) {
        this.totalDistance = totalDistance;
        this.averageSpeed = averageSpeed;
        this.totalElevation = totalElevation;
        this.totalTime = totalTime;
        this.creator = creator;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public double getAverageSpeed() {
        return averageSpeed;
    }

    public void setAverageSpeed(double averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    public double getTotalElevation() {
        return totalElevation;
    }

    public void setTotalElevation(double totalElevation) {
        this.totalElevation = totalElevation;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }

    public String getCreator() {
        return creator;
    }
    
    
}