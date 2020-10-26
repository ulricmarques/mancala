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
    
    public Packet(String type, int[] boardState, boolean switchTurn){
        this.type = type;
        this.boardState = boardState;
        this.switchTurn = switchTurn;
    }
    
    public Packet(String type, String text){
        this.type = type;
        this.text = text;
    }
    
    public Packet(String type){
        this.type = type;
    }
    
}
