package com.ulric.mancala.UI;

import com.ulric.mancala.Game.GameController;
import java.awt.CardLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class SetupScreen implements ActionListener, Serializable{
   
    protected GUI parentGUI;
    protected Registry registry;
    
    private final JPanel panelHost;
    private final JPanel panelJoin;
    
    private final JTextField inputServerHost;
    private final JTextField inputClientHost;
    private final JTextField inputNameHost;
    private final JTextField inputNameJoin;
    private final JTextField inputServerPort;
    private final JTextField inputClientPort;

    private final JButton runHost;
    private final JButton runJoin;
    
    private final JLabel labelServerIP;
    private final JLabel labelClientIP;
    private final JLabel labelServerPort;
    private final JLabel labelClientPort;
    private final JLabel labelNameHost;
    private final JLabel labelNameJoin;
    
    public String playerName;
    public int portNumber;
    public String hostNumber;
    
    protected final JTabbedPane tabbedPane;
    
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
        
        inputServerHost = new JTextField();
        inputServerHost.setBounds(60, 130, 400, 40);
        inputServerHost.setText("localhost");
        inputServerHost.addActionListener(this);
        labelServerIP = new JLabel("Digite o IP");
        labelServerIP.setBounds(60, 100, 300, 40);
        
        inputServerPort = new JTextField();
        inputServerPort.setBounds(60, 200, 400, 40);
        inputServerPort.addActionListener(this);
        inputServerPort.setText("5000");
        labelServerPort = new JLabel("Digite a porta:");
        labelServerPort.setBounds(60, 170, 400, 40);
        
        runHost = new JButton("Iniciar servidor");
        runHost.setBounds(200, 250, 120, 50);
        runHost.addActionListener(this);
        
        panelHost.add(runHost);
        panelHost.add(labelServerIP);
        panelHost.add(inputServerHost);
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

        inputClientHost = new JTextField();
        inputClientHost.setBounds(60, 130, 400, 40);
        inputClientHost.setText("localhost");
        inputClientHost.addActionListener(this);
        labelClientIP = new JLabel("Digite o IP");
        labelClientIP.setBounds(60, 100, 300, 40);

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
        panelJoin.add(labelClientIP);
        panelJoin.add(inputClientHost);
        panelJoin.add(labelClientPort);
        panelJoin.add(inputClientPort);
        panelJoin.add(labelNameJoin);
        panelJoin.add(inputNameJoin);
        
        tabbedPane.addTab("Hospedar", null, panelHost,
                  "Hospedar um jogo");
        tabbedPane.addTab("Entrar", null, panelJoin,
                  "Entrar em um jogo");
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {

        CardLayout changePages = (CardLayout) (parentGUI.switchPanels.getLayout());
        
        if (e.getSource() == runHost && (inputServerPort.getText().length() < 1 || inputNameHost.getText().length() < 1)) {
            JOptionPane.showMessageDialog(null, "Por favor, preencha todos os campos.");
        }

        if (e.getSource() == runHost && inputServerPort.getText().length() > 0 && inputNameHost.getText().length() > 0) {
            hostNumber = inputServerHost.getText();
            portNumber = Integer.parseInt(inputServerPort.getText());
            playerName = inputNameHost.getText();
            boolean serverCreated = true;
            
            if(!serverCreated){
                JOptionPane.showMessageDialog(null, "Não foi possível criar o servidor. "
                        + "A porta digitada pode estar ocupada.","Falha ao criar servidor", JOptionPane.ERROR_MESSAGE);
            }
            else {
                changePages.show(parentGUI.switchPanels, "main");
                parentGUI.window.setSize(775, 595);
                parentGUI.window.setLayout(new GridLayout(2,0));
                try {
                    parentGUI.game = new GameController(parentGUI, true, true);
                } catch (RemoteException ex) {
                    Logger.getLogger(SetupScreen.class.getName()).log(Level.SEVERE, null, ex);
                }
                parentGUI.window.add(parentGUI.game.painter);
            }
        }

        if (e.getSource() == runJoin  && (inputClientPort.getText().length() < 1 || inputNameJoin.getText().length() < 1)) {

            JOptionPane.showMessageDialog(null, "Por favor, preencha todos os campos.");

        }
        if (e.getSource() == runJoin  && inputClientPort.getText().length() > 0 && inputNameJoin.getText().length() > 0) {
            
            portNumber = Integer.parseInt(inputClientPort.getText());
            hostNumber = inputClientHost.getText();
            playerName = inputNameJoin.getText();
      
            boolean connectionAccepted = true;
            if(!connectionAccepted){
                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
            }
            else{
                changePages.show(parentGUI.switchPanels, "main");
                parentGUI.window.setSize(775, 595);
                parentGUI.window.setLayout(new GridLayout(2,0));
                try {
                    parentGUI.game = new GameController(parentGUI, false, false);
                } catch (RemoteException ex) {
                    Logger.getLogger(SetupScreen.class.getName()).log(Level.SEVERE, null, ex);
                }
                parentGUI.window.add(parentGUI.game.painter);
            }
        }
    }   
}