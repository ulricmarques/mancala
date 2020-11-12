package com.ulric.mancala.Communication;

import java.rmi.Remote; 
import java.rmi.RemoteException;

/**
 *
 * @author Ulric
 */
public interface MancalaInterface extends Remote {
    public void connectToServer() throws  RemoteException;
    public void connectToClient() throws  RemoteException;
    public void sendMessage(String playerName, String text) throws  RemoteException;
}


