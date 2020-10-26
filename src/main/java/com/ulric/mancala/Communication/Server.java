package com.ulric.mancala.Communication;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
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
    
    public ObjectInputStream inputStream;
    public ObjectOutputStream outputStream;
    
    public boolean connectionAccepted = false;
    
    public Server(int portNumber) {
        
        this.portNumber = portNumber;
    }
    
    public void initializeServer(){
        try {
            serverSocket = new ServerSocket(this.portNumber);
            System.out.println("Server initialized.");
            System.out.println("Endereço: " + InetAddress.getLocalHost() );
        } catch (IOException e) {
        }
    }
    
    public void listenForRequest() {
        try {
            socket = serverSocket.accept();
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());
            this.connectionAccepted = true;
            System.out.println("Client "+ socket.getInetAddress().getHostAddress() + " has requested to join.");
        } catch (IOException e) {
        }
    }
}
