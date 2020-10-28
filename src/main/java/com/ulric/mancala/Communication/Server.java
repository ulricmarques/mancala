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
    
    public boolean initializeServer(){
        try {
            serverSocket = new ServerSocket(this.portNumber);
        } catch (IOException e) {
            return false;
        }
        return true;
    }
    
    public void listenForRequest() {
        try {
            socket = serverSocket.accept();
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());
            this.connectionAccepted = true;
        } catch (IOException e) {
        }
    }
}
