package com.ulric.mancala;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author Ulric
 */
public class SetupScreen implements ActionListener{
   
    protected GUI parentGUI;
    
    private JPanel coverHost, coverJoin;
    
    private JTextField host;
    private JTextField portServer;
    private JTextField portClient;

    private JButton runHost, runJoin;
    
    private Color clientColour;
    
    private JLabel labelIP;
    private JLabel labelPortServer;
    private JLabel labelPortClient;
    private JLabel labelMessageWrite;
    private JLabel labelMessageRead;

    private JTextArea server;
    private JTextField input;
    private JScrollPane scrollBar;
    
    private JTabbedPane tabbedPane;
    
    public SetupScreen(GUI parentGUI)  {
        this.parentGUI = parentGUI;
        tabbedPane = new JTabbedPane();
     
        coverJoin = new JPanel();
        coverJoin.setLayout(null); 
        coverJoin.setSize(300, 400);

        host = new JTextField();
        host.setBounds(60, 120, 400, 40);
        host.setText("localhost");
        host.addActionListener(this);
        labelIP = new JLabel("Digite o IP");
        labelIP.setBounds(60, 90, 300, 40);
        coverJoin.add(labelIP);
        coverJoin.add(host);

        portClient = new JTextField();
        portClient.setBounds(60, 180, 400, 40);
        portClient.addActionListener(this);
        portClient.setText("5000");
        labelPortClient = new JLabel("Digite a porta");
        labelPortClient.setBounds(60, 150, 300, 40);
        coverJoin.add(labelPortClient);
        coverJoin.add(portClient);

        runJoin = new JButton("Iniciar cliente");
        runJoin.setBounds(200, 250, 120, 50);
        runJoin.addActionListener(this);
        coverJoin.add(runJoin);
        
        coverHost = new JPanel();
        coverHost.setLayout(null); 

        portServer = new JTextField();
        portServer.setBounds(60, 60, 400, 40);
        portServer.addActionListener(this);
        portServer.setText("5000");
        labelPortServer = new JLabel("Digite a porta:");
        labelPortServer.setBounds(60, 30, 400, 40);
        
        runHost = new JButton("Iniciar servidor");
        runHost.setBounds(200, 100, 120, 50);
        runHost.addActionListener(this);
        
        coverHost.add(runHost);
        coverHost.add(portServer);
        coverHost.add(labelPortServer);
 
        
        tabbedPane.addTab("Hospedar", null, coverHost,
                  "Hospedar um jogo");
        tabbedPane.addTab("Entrar", null, coverJoin,
                  "Entrar em um jogo");
    }
    
    public JTabbedPane getTabbedPane(){
        return this.tabbedPane;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {

        CardLayout changePages = (CardLayout) (parentGUI.switchPanels.getLayout());
        
        if (e.getSource() == runHost && portServer.getText().length() < 1) {
            JOptionPane.showMessageDialog(null, "Por favor, digite um número para a porta");
        }

        if (e.getSource() == runHost && portServer.getText().length() > 0) {
            int portNumber = Integer.parseInt(portServer.getText());
            parentGUI.gameServer = new Server(portNumber);
            parentGUI.gameServer.initializeServer();
            if (!parentGUI.gameServer.connectionAccepted) {
                parentGUI.gameServer.listenForRequest();
            }
            
            parentGUI.objectInputStream = parentGUI.gameServer.objectInputStream;
            parentGUI.objectOutputStream = parentGUI.gameServer.objectOutputStream;
            
            parentGUI.start();
            
            changePages.show(parentGUI.switchPanels, "main");
            parentGUI.window.setSize(800, 600);
            parentGUI.window.setLayout(new GridLayout(2,0));
            parentGUI.game = new GameController(parentGUI.objectInputStream, parentGUI.objectOutputStream, true);
            parentGUI.window.add(parentGUI.game);
            
        }

        if (e.getSource() == runJoin  && portClient.getText().length() < 1) {

            JOptionPane.showMessageDialog(null, "Por favor, digite o endereço IP e a porta.");

        }
        if (e.getSource() == runJoin  && portClient.getText().length() > 0) {
            
            int portNumber = Integer.parseInt(portClient.getText());
            String hostNumber = host.getText();
      
            parentGUI.gameClient = new Client(hostNumber, portNumber);
            boolean connectionAccepted = parentGUI.gameClient.connect();
            
            
            parentGUI.objectInputStream = parentGUI.gameClient.objectInputStream;
            parentGUI.objectOutputStream = parentGUI.gameClient.objectOutputStream;
            
            parentGUI.start();
            changePages.show(parentGUI.switchPanels, "main");
            parentGUI.window.setSize(800, 600);
            parentGUI.window.setLayout(new GridLayout(2,0));
            parentGUI.game = new GameController(parentGUI.objectInputStream, parentGUI.objectOutputStream, false);
            parentGUI.window.add(parentGUI.game);
        }
    }   
}
