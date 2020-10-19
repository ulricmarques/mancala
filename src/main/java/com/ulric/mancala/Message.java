
package com.ulric.mancala;

import java.io.Serializable;
/**
 *
 * @author Ulric
 */
public class Message implements Serializable{
    
    protected String type;
    protected String text;
    protected int[] boardState;
    protected boolean switchTurn;
    
    public Message(String type, int[] boardState, boolean switchTurn){
        this.type = type;
        this.boardState = boardState;
        this.switchTurn = switchTurn;
    }
    
    public Message(String type, String text){
        this.type = type;
        this.text = text;
    }
    
}
