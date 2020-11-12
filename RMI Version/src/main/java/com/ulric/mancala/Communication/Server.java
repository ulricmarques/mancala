package com.ulric.mancala.Communication;

import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;

/**
 *
 * @author Ulric
 */
public class Server extends UnicastRemoteObject implements MancalaInterface{
    
    public boolean connectionAccepted = false;
    
    public Server() throws RemoteException {
        super();
    }

    @Override
    public void sendMessage(String playerName, String text) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void connectToServer() throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void connectToClient() throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


}
