/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ulric.mancala;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ulric
 */
public class Client {
    
    protected Socket socket;
    
    protected DataInputStream inputStream;
    protected DataOutputStream outputStream;
    
    protected ObjectInputStream objectInputStream;
    protected ObjectOutputStream objectOutputStream;
    
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
            Logger.getLogger(GameController.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Unable to connect to the address: " + this.ip + ":" + this.portNumber + " | Starting a server");
            return false;
        }
        System.out.println("Successfully connected to the server.");
        return true;
    }
    
    
}
