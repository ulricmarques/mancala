package com.ulric.mancala;

import java.awt.CardLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

/**
 *
 * @author Ulric
 */
public class SetupScreen implements ActionListener{
   
    protected GUI parentGUI;
    
    private final JPanel panelHost;
    private final JPanel panelJoin;
    
    private final JTextField inputHost;
    private final JTextField inputNameHost;
    private final JTextField inputNameJoin;
    private final JTextField inputServerPort;
    private final JTextField inputClientPort;

    private final JButton runHost;
    private final JButton runJoin;
    
    private final JLabel labelIP;
    private final JLabel labelServerPort;
    private final JLabel labelClientPort;
    private final JLabel labelNameHost;
    private final JLabel labelNameJoin;
    

    private final JTabbedPane tabbedPane;
    
    public SetupScreen(GUI parentGUI)  {
        this.parentGUI = parentGUI;
        tabbedPane = new JTabbedPane();
        
        // Hospedar
        panelHost = new JPanel();
        panelHost.setLayout(null); 
        panelHost.setSize(300, 400);

        inputNameHost = new JTextField();
        inputNameHost.setText("Jogador 1");
        inputNameHost.setBounds(60, 60, 400, 40);
        inputNameHost.addActionListener(this);
        labelNameHost = new JLabel("Digite seu nome:");
        labelNameHost.setBounds(60, 30, 400, 40);
        
        inputServerPort = new JTextField();
        inputServerPort.setBounds(60, 130, 400, 40);
        inputServerPort.addActionListener(this);
        inputServerPort.setText("5000");
        labelServerPort = new JLabel("Digite a porta:");
        labelServerPort.setBounds(60, 100, 400, 40);
        
        runHost = new JButton("Iniciar servidor");
        runHost.setBounds(200, 180, 120, 50);
        runHost.addActionListener(this);
        
        panelHost.add(runHost);
        panelHost.add(inputServerPort);
        panelHost.add(labelServerPort);
        panelHost.add(inputNameHost);
        panelHost.add(labelNameHost);
     
        // Entrar
        panelJoin = new JPanel();
        panelJoin.setLayout(null); 
        panelJoin.setSize(300, 400);
        
        inputNameJoin = new JTextField();
        inputNameJoin.setText("Jogador 2");
        inputNameJoin.setBounds(60, 60, 400, 40);
        inputNameJoin.addActionListener(this);
        labelNameJoin = new JLabel("Digite seu nome:");
        labelNameJoin.setBounds(60, 30, 400, 40);

        inputHost = new JTextField();
        inputHost.setBounds(60, 130, 400, 40);
        inputHost.setText("localhost");
        inputHost.addActionListener(this);
        labelIP = new JLabel("Digite o IP");
        labelIP.setBounds(60, 100, 300, 40);

        inputClientPort = new JTextField();
        inputClientPort.setBounds(60, 200, 400, 40);
        inputClientPort.addActionListener(this);
        inputClientPort.setText("5000");
        labelClientPort = new JLabel("Digite a porta");
        labelClientPort.setBounds(60, 170, 300, 40);
        

        runJoin = new JButton("Iniciar cliente");
        runJoin.setBounds(200, 250, 120, 50);
        runJoin.addActionListener(this);
        
        panelJoin.add(runJoin);
        panelJoin.add(labelIP);
        panelJoin.add(inputHost);
        panelJoin.add(labelClientPort);
        panelJoin.add(inputClientPort);
        panelJoin.add(labelNameJoin);
        panelJoin.add(inputNameJoin);
        
        tabbedPane.addTab("Hospedar", null, panelHost,
                  "Hospedar um jogo");
        tabbedPane.addTab("Entrar", null, panelJoin,
                  "Entrar em um jogo");
    }
    
    public JTabbedPane getTabbedPane(){
        return this.tabbedPane;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {

        CardLayout changePages = (CardLayout) (parentGUI.switchPanels.getLayout());
        
        if (e.getSource() == runHost && (inputServerPort.getText().length() < 1 || inputNameHost.getText().length() < 1)) {
            JOptionPane.showMessageDialog(null, "Por favor, preencha todos os campos.");
        }

        if (e.getSource() == runHost && inputServerPort.getText().length() > 0 && inputNameHost.getText().length() > 0) {
            int portNumber = Integer.parseInt(inputServerPort.getText());
            String playerName = inputNameHost.getText();
            
            
            parentGUI.gameServer = new Server(portNumber);
            parentGUI.gameServer.initializeServer();
            if (!parentGUI.gameServer.connectionAccepted) {
                parentGUI.gameServer.listenForRequest();
            }
            
            parentGUI.objectInputStream = parentGUI.gameServer.objectInputStream;
            parentGUI.objectOutputStream = parentGUI.gameServer.objectOutputStream;
            
            parentGUI.start();
            
            changePages.show(parentGUI.switchPanels, "main");
            parentGUI.window.setSize(780, 600);
            parentGUI.window.setLayout(new GridLayout(2,0));
            parentGUI.game = new GameController(parentGUI.objectInputStream, parentGUI.objectOutputStream, true, playerName);
            parentGUI.window.add(parentGUI.game);
            
        }

        if (e.getSource() == runJoin  && (inputClientPort.getText().length() < 1 || inputNameJoin.getText().length() < 1)) {

            JOptionPane.showMessageDialog(null, "Por favor, preencha todos os campos.");

        }
        if (e.getSource() == runJoin  && inputClientPort.getText().length() > 0 && inputNameJoin.getText().length() > 0) {
            
            int portNumber = Integer.parseInt(inputClientPort.getText());
            String hostNumber = inputHost.getText();
            String playerName = inputNameJoin.getText();
      
            parentGUI.gameClient = new Client(hostNumber, portNumber);
            boolean connectionAccepted = parentGUI.gameClient.connect();
            
            
            parentGUI.objectInputStream = parentGUI.gameClient.objectInputStream;
            parentGUI.objectOutputStream = parentGUI.gameClient.objectOutputStream;
            
            parentGUI.start();
            changePages.show(parentGUI.switchPanels, "main");
            parentGUI.window.setSize(780, 600);
            parentGUI.window.setLayout(new GridLayout(2,0));
            parentGUI.game = new GameController(parentGUI.objectInputStream, parentGUI.objectOutputStream, false, playerName);
            parentGUI.window.add(parentGUI.game);
        }
    }   
}
