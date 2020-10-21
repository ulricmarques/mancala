package com.ulric.mancala;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Ulric
 */
public class Server {
    
    protected int portNumber;
    protected ServerSocket serverSocket;
    protected Socket socket;
    
    protected DataInputStream inputStream;
    protected DataOutputStream outputStream;
    
    protected ObjectInputStream objectInputStream;
    protected ObjectOutputStream objectOutputStream;
    
    protected boolean connectionAccepted = false;
    
    public Server(int portNumber) {
        
        this.portNumber = portNumber;
    }
    
    public void initializeServer(){
        try {
            serverSocket = new ServerSocket(this.portNumber);
            System.out.println("Server initialized");
        } catch (IOException e) {
        }
    }
    
    public void listenForRequest() {
        try {
            socket = serverSocket.accept();
            outputStream = new DataOutputStream(socket.getOutputStream());
            inputStream = new DataInputStream(socket.getInputStream());
            objectOutputStream = new ObjectOutputStream(outputStream);
            objectInputStream = new ObjectInputStream(inputStream);
            this.connectionAccepted = true;
            System.out.println("Client has requested to join and the server accepted");
        } catch (IOException e) {
        }
    }
}
