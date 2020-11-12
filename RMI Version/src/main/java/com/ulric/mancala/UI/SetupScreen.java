package com.ulric.mancala.UI;

import com.ulric.mancala.Communication.Client;
import com.ulric.mancala.Communication.MancalaInterface;
import com.ulric.mancala.Game.GameController;
import com.ulric.mancala.Communication.Server;
import java.awt.CardLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
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
public class SetupScreen implements ActionListener{
   
    protected GUI parentGUI;
    protected Registry registry;
    
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
    
    @Override
    public void actionPerformed(ActionEvent e) {

        CardLayout changePages = (CardLayout) (parentGUI.switchPanels.getLayout());
        
        if (e.getSource() == runHost && (inputServerPort.getText().length() < 1 || inputNameHost.getText().length() < 1)) {
            JOptionPane.showMessageDialog(null, "Por favor, preencha todos os campos.");
        }

        if (e.getSource() == runHost && inputServerPort.getText().length() > 0 && inputNameHost.getText().length() > 0) {
            int portNumber = Integer.parseInt(inputServerPort.getText());
            String playerName = inputNameHost.getText();
            boolean serverCreated = false;
            try {
                Server serverObj = new Server();
                registry = LocateRegistry.createRegistry(portNumber);
                registry.bind("//"+"localhost"+":"+portNumber+"/Servidor", serverObj);

                System.out.println("Criou e registrou o servidor");
                serverCreated = true;
                     
            if(!serverCreated){
                JOptionPane.showMessageDialog(null, "Não foi possível criar o servidor. "
                        + "A porta digitada pode estar ocupada.","Falha ao criar servidor", JOptionPane.ERROR_MESSAGE);
            }
            else {
                changePages.show(parentGUI.switchPanels, "main");
                parentGUI.window.setSize(775, 595);
                parentGUI.window.setLayout(new GridLayout(2,0));
                parentGUI.game = new GameController(registry, true, playerName);
                parentGUI.window.add(parentGUI.game);
            }
            } catch (RemoteException | AlreadyBoundException ex) {  
                System.out.println("Erro na criação do servidor: "+ ex);
            }
        }

        if (e.getSource() == runJoin  && (inputClientPort.getText().length() < 1 || inputNameJoin.getText().length() < 1)) {

            JOptionPane.showMessageDialog(null, "Por favor, preencha todos os campos.");

        }
        if (e.getSource() == runJoin  && inputClientPort.getText().length() > 0 && inputNameJoin.getText().length() > 0) {
            
            int portNumber = Integer.parseInt(inputClientPort.getText());
            String hostNumber = inputHost.getText();
            String playerName = inputNameJoin.getText();
      
            try {
                Server serverObj = new Server();
                registry = LocateRegistry.getRegistry(portNumber);
                registry.bind("//"+hostNumber+":"+portNumber+"/Cliente", serverObj);
                
                MancalaInterface opponent = (MancalaInterface) registry.lookup("//"+hostNumber+":"+portNumber+"/Servidor");
                System.out.println("Criou o cliente e encontrou o servidor");
            }catch (RemoteException | NotBoundException ex) {
                System.out.println("Erro na criação do cliente: " + ex);
            }catch (AlreadyBoundException ex) {
                Logger.getLogger(SetupScreen.class.getName()).log(Level.SEVERE, null, ex);
            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
            
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
            
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
            
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
            
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
            
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
            
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }
            
            
//            boolean connectionAccepted = parentGUI.gameClient.connect();
//            
//            if(!connectionAccepted){
//                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
//                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
//            }
//            else{
//                parentGUI.inputStream = parentGUI.gameClient.inputStream;
//                parentGUI.outputStream = parentGUI.gameClient.outputStream;
//
//                parentGUI.start();
//                changePages.show(parentGUI.switchPanels, "main");
//                parentGUI.window.setSize(775, 595);
//                parentGUI.window.setLayout(new GridLayout(2,0));
//                parentGUI.game = new GameController(parentGUI.inputStream, parentGUI.outputStream, false, playerName);
//                parentGUI.window.add(parentGUI.game);
//            }

        }
    }   
}