package com.ulric.mancala.Game;

import com.ulric.mancala.Communication.Packet;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author Ulric
 */
public class GameController extends JPanel implements MouseListener {

    final Board board;

    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;

    private final boolean goesFirst;
    private boolean yourTurn;
    private boolean youWon = false;
    private boolean draw = false;
    private boolean gameEnded = false;
    private boolean surrenderVictory = false;
    
    private final Color stonesColor = new Color(0,0,0, (float) 0.5);
    private final Color infoColor = new Color(0,0,0);
    
    public String playerName;
    
    private final Font stonesFont = new Font("Arial", Font.BOLD, 20);
    private final Font infoFont = new Font("Arial", Font.BOLD, 20);

    private int[] currentBoardState = new int[] { 4, 4, 4, 4, 4, 4, 0, 4, 4, 4, 4, 4, 4, 0 };

    public GameController(ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream,
            boolean goesFirst, String playerName) {
        
        board = new Board();

        this.objectOutputStream = objectOutputStream;
        this.objectInputStream = objectInputStream;

        this.playerName = playerName;
        
        this.goesFirst = goesFirst;

        yourTurn = this.goesFirst == true;

        setBorder(BorderFactory.createLineBorder(Color.black));
        addMouseListener(this);      
    }
    
    @Override
    public Dimension getPreferredSize() {
        return board.getSize();
    }

    public void updateGameState(int[] boardState, boolean switchTurn){
        currentBoardState = boardState;
        checkForWin();
        repaint();
        if(switchTurn){
            yourTurn = true;
        }
    }

    public void resetBoard(){
        int[] initialBoard = { 4, 4, 4, 4, 4, 4, 0, 4, 4, 4, 4, 4, 4, 0 };
        currentBoardState = initialBoard;
    }
    
    public void restartGame(){
        try {
            Packet newPacket = new Packet("RESTART", playerName);
            objectOutputStream.writeObject(newPacket);
            objectOutputStream.flush();
            youWon = false;
            showEndgameInfo(true);
            resetBoard();
            yourTurn = this.goesFirst == true;
            gameEnded = false;
            repaint();
        } catch (IOException ex) {
            Logger.getLogger(GameController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void handleRestart(){
        youWon = true;
        showEndgameInfo(true);
        resetBoard();
        yourTurn = this.goesFirst == true;
        gameEnded = false;
        repaint();
    }
    
    public void finishGame(){
        gameEnded = true;
        removeMouseListener(this);
        showEndgameInfo(false);
        System.exit(0);
    }

    public void surrender(){   
        try {
            Packet newPacket =  new Packet("SURRENDER", playerName);
            objectOutputStream.writeObject(newPacket);
            objectOutputStream.flush();
            youWon = false;
            surrenderVictory = true;
            finishGame();
        } catch (IOException ex) {
            Logger.getLogger(GameController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void handleSurrender(){
        youWon = true;
        surrenderVictory = true;
        repaint();
        finishGame();
    }
    
      private void showEndgameInfo(boolean restart){
        String text;
        
        if(restart){
            if(youWon){
                text = "O oponente reiniciou a partida. Você venceu!";
            } else{
                text = "Você reiniciou a partida. Você perdeu!";
            }
        } 
        else {
            if (draw) {
            text = "Empate!";
            } else {
                if(youWon){
                    text = surrenderVictory ? "Você venceu! (Por desistência)" : "Você venceu!" ;
                } else{
                    text = surrenderVictory ? "Você perdeu! (Por desistência)" : "Você perdeu!" ;
                }
            }
        }
        

        JOptionPane.showMessageDialog(this, text, "Fim de jogo", JOptionPane.INFORMATION_MESSAGE);
    }
    
    protected boolean moveStones(final int cup) {
        int counter = cup;

        // Checa se a casa está vazia.
        if (currentBoardState[cup] < 1 ) {
            return true;
        }

        // Retira todas as pedras da casa.
        int stones = currentBoardState[cup];
        currentBoardState[cup] = 0;

        while(stones > 0) {
            counter++;

            // Pula a kallah adversária e reseta o contador.
            if (counter == 13) {
                counter = -1;
            } else {
                currentBoardState[counter]++;
                stones--;
            }
            repaint();
        }

        // Aponta para a casa oposta.
        int inverseCounter = -counter + 12;

        // Checa se houve uma captura.
        if (counter < 6 && currentBoardState[counter] == 1 && currentBoardState[inverseCounter] > 0) {

            // Captura as pedras do oponente e manda para a sua kallah.
            currentBoardState[6] += currentBoardState[inverseCounter] + 1;

            // Zera ambas as casas.
            currentBoardState[counter] = 0;
            currentBoardState[inverseCounter] = 0;
        }

        // Retorna se a última pedra caiu na sua kallah.
        // Em caso positivo, você tem mais um turno.
        return counter == 6;
    }

    public int[] switchBoardView() {

        // Inverte o tabuleiro
        int[] inverseBoardState = new int[14];
        System.arraycopy(currentBoardState, 7, inverseBoardState, 0, 7);
        System.arraycopy(currentBoardState, 0, inverseBoardState, 7, 7);

        return inverseBoardState;
    }

    protected void drawStones(Graphics g) {
        int cx, cy; // ajuste de centro

        for (int cup = 0; cup < currentBoardState.length; cup++) {
            if (cup == 6 || cup == 13) {
                cx = 0;
                cy = -4;
            } else {
                cx = -2;
                cy = 5;
            }

            g.setFont(stonesFont);
            g.drawString(Integer.toString(currentBoardState[cup]), board.getCupCenterX(cup) + cx, 
                                           board.getCupCenterY(cup) + cy);
        }
    }
    
    private void paintPlayerInfo(Graphics g) {

        String text;
        int x, y;
       
        if (!gameEnded) {
            text = yourTurn ? "Seu turno" : "Turno do oponente" ;
            
            FontMetrics fm = g.getFontMetrics();
            x = (getWidth() - fm.stringWidth(text)) / 2;  
            y = 250;

            g.drawString(text, x, y);
        } 
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
               
        board.drawBoard(g, yourTurn);

        g.setColor(stonesColor);
        drawStones(g);

        
        g.setColor(infoColor);
        paintPlayerInfo(g);

    }

    public void checkForWin() {
        boolean topRowEmpty = true, bottomRowEmpty = true;

        // Checa se a fileira de baixo está vazia.
        for (int i = 0; i < 6; ++i) {
            if (currentBoardState[i] > 0) {
                    bottomRowEmpty = false;
                    break;
            }
        }

        // Checa se a fileira de cima está vazia.
        for (int i = 7; i < 13; ++i) {
            if (currentBoardState[i] > 0) {
                    topRowEmpty = false;
                    break;
            }
        }

        // Coloca as pedras restantes do tabuleiro na kallah do seu jogador.
        if (topRowEmpty || bottomRowEmpty) {
            if (topRowEmpty && ! bottomRowEmpty) {
                for (int i = 0; i < 6; ++i) {
                    currentBoardState[6] += currentBoardState[i];
                    currentBoardState[i] = 0;
                }
            } else if (! topRowEmpty && bottomRowEmpty) {
                for (int i = 7; i < 13; ++i) {
                    currentBoardState[13] += currentBoardState[i];
                    currentBoardState[i] = 0;
                }
            }

            // Checa qual jogador tem mais pedras.
            if (currentBoardState[6] > currentBoardState[13] ) {
                youWon = true;
            } else if (currentBoardState[6] < currentBoardState[13]) {
                youWon = false;
            } else {
                // Empate.
                draw = true;
            }

            finishGame();
        }
    }
    
    public boolean doPlayerTurn(int cup) {

        // Move as pedras da casa selecionada.
        boolean result = moveStones(cup);

        // Checa se alguem jogador não tem mais pedras.
        checkForWin();

        // Retorna se o seu turno acabou.
        return !result && !gameEnded;
     }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (yourTurn && !gameEnded) {
            try {
                int x, y;
                int mx = e.getX();
                int my = e.getY();

                // Percorre as casas na fileira de baixo.
                for (int cup = 0; cup < 6; ++cup) {
                    x = board.getCupX(cup);
                    y = board.getCupY(cup);

                    // Checa se o clique foi na casa da iteração atual.
                    if (mx > x && mx < x + board.cupWidth && my > y && my < y + board.cupHeight )  {
                        boolean shouldSwitch = doPlayerTurn(cup);
                        repaint();      

                        if(shouldSwitch){
                            yourTurn = false;
                        }

                        Packet newPacket = new Packet("GAME", playerName, switchBoardView(), shouldSwitch);
                        objectOutputStream.writeObject(newPacket);
                        objectOutputStream.flush();
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(GameController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
}
