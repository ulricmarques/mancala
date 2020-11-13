package com.ulric.mancala.UI;

import com.ulric.mancala.Game.GameController;

import java.awt.CardLayout;
import java.awt.Color;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author Ulric
 */
public class GUI {
        
    protected ServerSocket serverSocket;
    protected Socket socket;

    protected JPanel switchPanels; 
    protected JFrame window;

    protected GameController game;
    protected Color clientColour;
    
    public SetupScreen setupScreen;
    public MainScreen mainScreen;
   
    public GUI() {
        window = new JFrame("Mancala");
        
        switchPanels = new JPanel(new CardLayout());
        
        setupScreen = new SetupScreen(this);
        switchPanels.add(setupScreen.tabbedPane, "setup");

        mainScreen = new MainScreen(this);
  
        switchPanels.add(mainScreen.panelMain, "main");
        window.add(switchPanels);

        window.setResizable(false); 
        window.setSize(550, 400);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true); 
    }
  
}
