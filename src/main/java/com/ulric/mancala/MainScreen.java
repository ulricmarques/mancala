package com.ulric.mancala;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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
    
    private final JPanel main;
    private GameController game;
    
    private final JLabel labelMessageWrite;
    private final JLabel labelMessageRead;
    
    private JButton runSurrender;
    private JButton runUndoTurn;
    private JButton runRestartGame;

    protected JTextArea display;
    protected JTextField input;
    private final JScrollPane scrollBar;
    
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
        display = new JTextArea();
        display.setEditable(false);
        display.setBorder(thinBorder);
        scrollBar = new JScrollPane(display);
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
        
        runSurrender = new JButton("Desistir");
        runSurrender.setBounds(550, 30, 130, 30);
        runSurrender.addActionListener(this);
        
        runRestartGame = new JButton("Reiniciar partida");
        runRestartGame.setBounds(550, 90, 130, 30);
        runRestartGame.addActionListener(this);
         
        main.add(runSurrender);
        main.add(runRestartGame);
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
            writeMessage();
        }
        
        if (e.getSource() == runSurrender) {
            int response = JOptionPane.showConfirmDialog(
                    parentGUI.game, 
                    "Deseja mesmo desistir da partida?", 
                    "Confirmação de desistência", 
                    JOptionPane.YES_NO_OPTION);    
            
            if(response == 0){
                parentGUI.game.surrender(false);
            }
            
        }
        
        if (e.getSource() == runRestartGame) {
            int response = JOptionPane.showConfirmDialog(
                    parentGUI.game, 
                    "Deseja mesmo reiniciar a partida? Isso será contado como desistência.", 
                    "Confirmação de desistência", 
                    JOptionPane.YES_NO_OPTION);    
            
            if(response == 0){
                parentGUI.game.surrender(true);
            }
            
        }
    }
}
