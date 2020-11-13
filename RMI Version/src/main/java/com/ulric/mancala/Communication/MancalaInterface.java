package com.ulric.mancala.Communication;

import java.rmi.Remote; 
import java.rmi.RemoteException;

/**
 *
 * @author Ulric
 */
public interface MancalaInterface extends Remote {
    public void connectToOpponent() throws  RemoteException;
    public void updateChat(String playerName, String text) throws  RemoteException;
    public void updateBoardState(int[] boardState, boolean switchTurn) throws  RemoteException;
    public void handleSurrender() throws  RemoteException;
    public void handleRestart() throws  RemoteException;
}


