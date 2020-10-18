package com.ulric.mancala;

import java.io.Serializable;

/**
 *
 * @author Ulric
 */
public class GameMessage implements Serializable {
    
    protected final int[] boardState;
    protected final boolean switchTurn;
    
    public GameMessage(int[] boardState, boolean switchTurn){
        this.boardState = boardState;
        this.switchTurn = switchTurn;
    }
    
}
