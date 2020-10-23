package com.ulric.mancala;

import java.awt.CardLayout;
import java.awt.Color;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.JFrame;
import javax.swing.JPanel;


/**
 *
 * @author Ulric
 */
public class GUI extends Thread {
        
    protected ServerSocket serverSocket;
    protected Socket socket;

    protected ObjectInputStream objectInputStream;
    protected ObjectOutputStream objectOutputStream;
    
    protected JPanel switchPanels; 
    protected JFrame window;

    protected GameController game;
    protected Color clientColour;
    
    
    private SetupScreen setupScreen;
    private MainScreen mainScreen;
    
    protected Server gameServer;
    protected Client gameClient;
    
    private boolean accepted = false;

    public void runInterface() {
       
        window = new JFrame("Mancala");
        
        switchPanels = new JPanel(new CardLayout());
        
        setupScreen = new SetupScreen(this);
        switchPanels.add(setupScreen.getTabbedPane(), "setup");

        mainScreen = new MainScreen(this);
  
        switchPanels.add(mainScreen.getMainPanel(), "main");
        window.add(switchPanels);

        window.setResizable(false); 
        window.setSize(550, 400);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true); 

    }

    public GUI() {
        runInterface();   
    }
    
    @Override
    public void run() {
        try {
            Message message;
            String temp = "";

            while (true) {
                message = (Message) objectInputStream.readObject();
                if(!"".equals(message.type)){
                    if(message.type.equals("CHAT")){
                        if(!"".equals(message.text)){
                            temp = mainScreen.display.getText() + game.playerName + ": "  + message.text + "\n";
                            mainScreen.display.setText(temp);
                        }
                    }
                    if(message.type.equals("GAME")){
                        game.updateGameState(message.boardState, message.switchTurn);
                    }
                    if(message.type.equals("SURRENDER")){
                        game.handleSurrender();
                    }
                    if(message.type.equals("RESTART")){
                        game.handleRestart();
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
        }
    }
   
}
