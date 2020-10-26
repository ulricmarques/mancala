package com.ulric.mancala.Communication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 *
 * @author Ulric
 */
public class Client {
    
    protected Socket socket;
    
    protected DataInputStream inputStream;
    protected DataOutputStream outputStream;
    
    public ObjectInputStream objectInputStream;
    public ObjectOutputStream objectOutputStream;
    
    protected int portNumber;
    protected String ip;
    
    public Client(String ip, int portNumber){
        this.ip = ip;
        this.portNumber = portNumber;
    }
    
    public boolean connect(){
        try {
            System.out.println("IP: " + this.ip + " Port: " + this.portNumber);
            socket = new Socket(this.ip, this.portNumber);
            outputStream = new DataOutputStream(socket.getOutputStream());
            inputStream = new DataInputStream(socket.getInputStream());
            objectOutputStream = new ObjectOutputStream(outputStream);
            objectInputStream = new ObjectInputStream(inputStream);
        } catch (IOException ex) {
            System.out.println("Unable to connect to the address: " + this.ip + ":" + this.portNumber);
            return false;
        }
        System.out.println("Successfully connected to the server.");
        return true;
    }
    
    
}
