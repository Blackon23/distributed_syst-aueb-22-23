package common;

import java.io.IOException;
import java.io.Serializable;
import java.net.*;
import java.util.ArrayList;
import java.net.ServerSocket;
import java.net.Socket;

public class Node implements Serializable {
    
    private static final long serialVersionUID = -304193945227516524L;
    
    protected String ipAddress ;
    protected int port;
    
    public Node(){
    }
    
    public Node(int port , String ipAddress)  {
        this.port=port;
        this.ipAddress=ipAddress;
    }

    public  Socket connect(String ip ,int port){
        
        Socket socket = null;
        
        try {

            InetAddress host = Inet4Address.getByName(ip);
            socket = new Socket(host, port);

            return socket;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println(e);
        }

        return socket;
    }

    public void Disconnect(Socket socket){
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
    public void DisconnectServer(ServerSocket socket){
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
    /**informs the other node for the system instance **/
    public void UpdateNodes(){
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

}