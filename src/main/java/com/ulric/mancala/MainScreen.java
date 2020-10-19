/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ulric.mancala;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

/**
 *
 * @author Ulric
 */
public class MainScreen implements ActionListener {
    
    protected GUI parentGUI;
    
    private JPanel main;
    private GameController game;
    private JTextField host;
    
    private JTextField portServer;
    private JTextField portClient;

    private JButton runHost, runJoin;
    
    private JLabel labelIP;
    private JLabel labelPortServer;
    private JLabel labelPortClient;
    private JLabel labelMessageWrite;
    private JLabel labelMessageRead;

    protected JTextArea server;
    protected JTextField input;
    private JScrollPane scrollBar;
    
    public MainScreen(GUI parentGUI) {
        
        this.parentGUI = parentGUI;
    
        main = new JPanel();
        main.setLayout(null); 
        main.setSize(300, 400);
        main.setBackground(new Color(0,0,0));

        labelMessageRead = new JLabel("Mensagens recebidas:");
        labelMessageRead.setForeground(Color.white);
        labelMessageRead.setBounds(30, -5, 300, 40);
        main.add(labelMessageRead);

        Border thinBorder = LineBorder.createBlackLineBorder();
        server = new JTextArea();
        server.setEditable(false);
        server.setBorder(thinBorder);
        scrollBar = new JScrollPane(server);
        scrollBar.setBounds(20, 30, 450, 150);

        main.add(scrollBar);

        labelMessageWrite = new JLabel("Digite sua mensagem: ");
        labelMessageWrite.setForeground(Color.white);
        labelMessageWrite.setBounds(30, 180, 300, 40);
        main.add(labelMessageWrite);

        input = new JTextField();
        input.setBounds(20, 220, 450, 30);
        input.addActionListener(this);
        main.add(input);
    }
    
    public JPanel getMainPanel(){
        return this.main;
    }
    
    public void writeMessage() {

        try {
            Message newMessage = new Message("CHAT", input.getText());
            parentGUI.objectOutputStream.writeObject(newMessage);
            parentGUI.objectOutputStream.flush();          
        } catch (IOException i) {
            System.out.println("Error " + i);
        }
        input.setText("");
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
  
        if (!input.getText().equals("")) {
            // user message input
            writeMessage();
        }
    }
}
