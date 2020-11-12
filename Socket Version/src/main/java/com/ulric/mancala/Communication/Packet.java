package com.ulric.mancala.Communication;

import java.io.Serializable;
/**
 *
 * @author Ulric
 */
public class Packet implements Serializable{
    
    public String type;
    public String text;
    public int[] boardState;
    public boolean switchTurn;
    public String playerName;
    
    public Packet(String type, String playerName, int[] boardState, boolean switchTurn){
        this.playerName = playerName;
        this.type = type;
        this.boardState = boardState;
        this.switchTurn = switchTurn;
        this.playerName = playerName;
    }
    
    public Packet(String type, String playerName, String text){
        this.type = type;
        this.text = text;
        this.playerName = playerName;
    }
    
    public Packet(String type, String playerName){
        this.type = type;
        this.playerName = playerName;
    }
    
}
