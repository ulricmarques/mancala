/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ulric.mancala;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

/**
 *
 * @author Ulric
 */
public class GUI implements ActionListener {
        
    private ServerSocket serverSocket;
    
    protected DataInputStream inputStream;
    
    protected Socket socket;

    protected DataOutputStream outputStream;
    
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    
    private int port_number = 0;
     
    private JPanel switchPanels; 

    ///Swing UI
    private JFrame window;

    private JPanel coverHost, coverJoin;
    private JPanel main;
    private GameController game;
    private JTextField host;
    private JTextField port;
    private JTextField clientName;
    private JButton runHost, runJoin;
    private Color clientColour;
    private JLabel labels_cover[];
    private JLabel labels_port;

    private JTextArea server;
    private JTextField input;
    private JScrollPane scrollBar;
    
    private boolean accepted = false;


    public void runPanel() {
       
        window = new JFrame("Cliente");
        JTabbedPane tabbedPane = new JTabbedPane();
     
        labels_cover = new JLabel[6];
        switchPanels = new JPanel(new CardLayout());

        coverJoin = new JPanel();
        coverJoin.setLayout(null); // Setting to layout to null, so it becomes absolute position
        coverJoin.setSize(300, 400);

        clientName = new JTextField();
        clientName.setBounds(60, 60, 400, 40);
        clientName.addActionListener(this);
        labels_cover[0] = new JLabel("Please enter your name");
        labels_cover[0].setBounds(60, 30, 300, 40);
        coverJoin.add(clientName);
        coverJoin.add(labels_cover[0]);


        host = new JTextField();
        host.setBounds(60, 120, 400, 40);
        host.setText("localhost");
        host.addActionListener(this);
        labels_cover[1] = new JLabel("Please enter your host");
        labels_cover[1].setBounds(60, 90, 300, 40);
        coverJoin.add(labels_cover[1]);
        coverJoin.add(host);


        port = new JTextField();
        port.setBounds(60, 180, 400, 40);
        port.addActionListener(this);
        port.setText("5000");
        labels_cover[2] = new JLabel("Please enter your port number");
        labels_cover[2].setBounds(60, 150, 300, 40);
        coverJoin.add(labels_cover[2]);
        coverJoin.add(port);

        // Button for the user to leave the chat system
        runJoin = new JButton("Run Client");
        runJoin.setBounds(200, 250, 100, 50);
        runJoin.addActionListener(this);
        coverJoin.add(runJoin);
        
        // Panel for hosting
        coverHost = new JPanel();
        coverHost.setLayout(null); // Setting to layout to null, so it becomes absolute position

        port = new JTextField();
        port.setBounds(60, 60, 400, 40);
        port.addActionListener(this);
        port.setText("5000");
        labels_port = new JLabel("Please enter your port number");
        labels_port.setBounds(60, 30, 400, 40);

        // Button for the user to leave the chat system
        runHost = new JButton("Run Server");
        runHost.setBounds(200, 100, 100, 50);
        runHost.addActionListener(this);

        coverHost.add(runHost);
        coverHost.add(port);
        coverHost.add(labels_port);
        
        tabbedPane.addTab("Host", null, coverHost,
                  "Host a game");
        tabbedPane.addTab("Join", null, coverJoin,
                  "Join a game");
        switchPanels.add(tabbedPane, "cover");

        main = new JPanel();
        main.setLayout(null); // Setting to layout to null, so it becomes absolute position
        main.setSize(300, 400);

        mainInterface();
        window.setSize(600, 450);

        switchPanels.add(main, "main");
        window.add(switchPanels);

        window.setResizable(true); // size remain the same, not changeable
        window.setSize(600, 450); // size for the client
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true); // display frame

    }

    public void mainInterface() {
        
        window.setTitle(this.clientName.getText());
        main.setBackground(clientColour);

        labels_cover[3] = new JLabel("Display Messages:");
        labels_cover[3].setForeground(Color.white);
        labels_cover[3].setBounds(30, -5, 300, 40);
        main.add(labels_cover[3]);

        Border thinBorder = LineBorder.createBlackLineBorder();
        server = new JTextArea();
        server.setEditable(false);
        server.setBorder(thinBorder);
        scrollBar = new JScrollPane(server);
        scrollBar.setBounds(20, 30, 450, 150);

        main.add(scrollBar);

        labels_cover[4] = new JLabel("Enter message here");
        labels_cover[4].setForeground(Color.white);
        labels_cover[4].setBounds(30, 180, 300, 40);
        main.add(labels_cover[4]);

        input = new JTextField();
        input.setBounds(20, 220, 450, 30);
        input.addActionListener(this);
        main.add(input);
        
    }
    
    public Color randomColors() {
        Random randomGenerator = new Random();
        int red = randomGenerator.nextInt(256);
        int green = randomGenerator.nextInt(256);
        int blue = randomGenerator.nextInt(256);

        return new Color(red, green, blue);
    }
    
    
    public GUI() {
        clientColour = randomColors();
        runPanel();   
    }
    
    private boolean connect() {
        int portNumber = Integer.parseInt(port.getText());
        String hostNumber = host.getText();
        
        try {
            System.out.println("IP: " + hostNumber + " Port: " + portNumber);
            socket = new Socket(hostNumber, portNumber);
            //outputStream = new DataOutputStream(socket.getOutputStream());
            //inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
            inputStream = new DataInputStream(socket.getInputStream());
            objectOutputStream = new ObjectOutputStream(outputStream);
            objectInputStream = new ObjectInputStream(inputStream);
            accepted = true;
        } catch (IOException ex) {
            Logger.getLogger(GameController.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Unable to connect to the address: " + host.getText() + ":" + portNumber + " | Starting a server");
            return false;
        }
        System.out.println("Successfully connected to the server.");
        return true;
    }

    private void initializeServer() {
        try {
                int portNumber = Integer.parseInt(port.getText());
                serverSocket = new ServerSocket(portNumber);
                System.out.println("server initialized");
        } catch (Exception e) {
                e.printStackTrace();
        }
    }
    
    private void listenForServerRequest() {
		try {
                    socket = serverSocket.accept();
                    //outputStream = new DataOutputStream(socket.getOutputStream());
                    //inputStream = new DataInputStream(socket.getInputStream());
                    outputStream = new DataOutputStream(socket.getOutputStream());
                    inputStream = new DataInputStream(socket.getInputStream());
                    objectOutputStream = new ObjectOutputStream(outputStream);
                    objectInputStream = new ObjectInputStream(inputStream);
                    accepted = true;
                    System.out.println("CLIENT HAS REQUESTED TO JOIN, AND WE HAVE ACCEPTED");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    
    class serverReader extends Thread {

        @Override
        public void run() {
            try {
                Message message;
                String temp = "";

                while (true) {
                    message = (Message) objectInputStream.readObject();
                    if(!"".equals(message.type)){
                        System.out.println("Tipo da mensagem: " + message.type);
                        if(message.type.equals("CHAT")){
                            if(!"".equals(message.text)){
                                System.out.println("setou a mensagem: " + message.text);
                                temp = temp + message.text + "\n";
                                server.setText(temp);
                            }
                        }
                        
                        if(message.type.equals("GAME")){
                            System.out.println("Tipo game");
                            game.updateBoard(message.boardState, message.switchTurn);
                        }
                        
                    }
                    
                        
                }
            } catch (Exception e) {
            }
        }
    }
    
    public void communicate() {

        try {
            Message newMessage = new Message("CHAT", input.getText());
            objectOutputStream.writeObject(newMessage);
            objectOutputStream.flush();          
        } catch (IOException i) {
            System.out.println("Error " + i);
        }
        input.setText("");
    }
    
    public void closeConnections() {
        try {
            inputStream.close();
            outputStream.close();
            this.socket.close();
        } catch (IOException i) {
            System.out.println(i);
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {

        CardLayout changePages = (CardLayout) (switchPanels.getLayout());

        if (e.getSource() == runJoin  && port.getText().length() < 1 && clientName.getText().length() < 1) {

            JOptionPane.showMessageDialog(null, "All forms must be filled!");

        }
        if (e.getSource() == runJoin  && port.getText().length() > 0 && clientName.getText().length() > 0) {
            connect();
            new serverReader().start();
            changePages.show(switchPanels, "main");
            window.setSize(800, 600);
            window.setLayout(new GridLayout(2,0));
            game = new GameController(this.objectInputStream, this.objectOutputStream, false);
            window.add(game);
        }
        
        if (e.getSource() == runHost && port.getText().length() < 1) {

            JOptionPane.showMessageDialog(null, "A port number must be entered!");
        }

        if (e.getSource() == runHost && port.getText().length() > 0) {

            initializeServer();
            if (!accepted) {
                listenForServerRequest();
            }
            new serverReader().start();
            
            changePages.show(switchPanels, "main");
            window.setSize(800, 600);
            window.setLayout(new GridLayout(2,0));
            game = new GameController(this.objectInputStream, this.objectOutputStream, true);
            window.add(game);
            
        }
        
        if (!input.getText().equals("")) {
            // user message input
            communicate();
        }
    }
    
}
