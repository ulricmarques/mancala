package com.ulric.mancala.UI;

import com.ulric.mancala.Communication.MancalaInterface;
import com.ulric.mancala.Game.GameController;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class MainScreen implements ActionListener, Serializable {
    
    protected GUI parentGUI;
    
    protected MancalaInterface opponent;
    
    protected final JPanel panelMain;
    private GameController game;
    
    private final JLabel labelMessageWrite;
    private final JLabel labelMessageRead;
    
    private final JButton runSurrender;
    private JButton runUndoTurn;
    private final JButton runRestartGame;

    public JTextArea display;
    protected JTextField input;
    private final JScrollPane scrollBar;
    
    public MainScreen(GUI parentGUI) {
        
        this.parentGUI = parentGUI;
    
        panelMain = new JPanel();
        panelMain.setLayout(null); 
        panelMain.setSize(300, 400);
        panelMain.setBackground(new Color(0,0,0));

        labelMessageRead = new JLabel("Mensagens recebidas:");
        labelMessageRead.setForeground(Color.white);
        labelMessageRead.setBounds(21, -5, 300, 40);
        panelMain.add(labelMessageRead);

        Border thinBorder = LineBorder.createBlackLineBorder();
        display = new JTextArea();
        display.setEditable(false);
        display.setBorder(thinBorder);
        scrollBar = new JScrollPane(display);
        scrollBar.setBounds(20, 30, 450, 150);

        panelMain.add(scrollBar);

        labelMessageWrite = new JLabel("Digite sua mensagem: (ENTER para enviar)");
        labelMessageWrite.setForeground(Color.white);
        labelMessageWrite.setBounds(21, 180, 300, 40);
        panelMain.add(labelMessageWrite);

        input = new JTextField();
        input.setBounds(20, 220, 450, 30);
        input.addActionListener(this);
        panelMain.add(input);
        
        runSurrender = new JButton("Desistir");
        runSurrender.setBounds(550, 30, 130, 30);
        runSurrender.addActionListener(this);
        
        runRestartGame = new JButton("Reiniciar partida");
        runRestartGame.setBounds(550, 90, 130, 30);
        runRestartGame.addActionListener(this);
         
        panelMain.add(runSurrender);
        panelMain.add(runRestartGame);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
  
        if (!input.getText().equals("")) {
            String temp;
   
            temp = display.getText() + "Você: " + input.getText() + "\n";
            display.setText(temp);
            try {
                parentGUI.game.opponent.updateChat(parentGUI.game.playerName, input.getText());
            } catch (RemoteException ex) {
                Logger.getLogger(MainScreen.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            input.setText("");

        }
        
        if (e.getSource() == runSurrender) {
            int response = JOptionPane.showConfirmDialog(
                    parentGUI.game.painter, 
                    "Deseja mesmo desistir da partida?", 
                    "Confirmação de desistência", 
                    JOptionPane.YES_NO_OPTION);    
            
            if(response == 0){
                parentGUI.game.surrender();
            }
            
        }
        
        if (e.getSource() == runRestartGame) {
            int response = JOptionPane.showConfirmDialog(
                    parentGUI.game.painter, 
                    "Deseja mesmo reiniciar a partida? Isso contará como desistência.", 
                    "Confirmação de reinício", 
                    JOptionPane.YES_NO_OPTION);    
            
            if(response == 0){
                parentGUI.game.restartGame();
            }
            
        }
    }
}
