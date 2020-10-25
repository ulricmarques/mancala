package com.ulric.mancala;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

/**
 *
 * @author Ulric
 */
class Board {

    final int marginLeft = 13;
    final int padding = 17;
    final int marginTop = 20;
    final int cupWidth = 74;
    final int cupHeight = 74;
    final int kallahWidth = 80;
    final int kallahHeight = 205;
    final Color playerOneColor, playerTwoColor;
    
    private BufferedImage boardImagePlayer;
    private BufferedImage boardImageOpponent;

    public Board(Color playerOneColor, Color playerTwoColor) {
        this.playerOneColor = playerOneColor;
        this.playerTwoColor = playerTwoColor;
        
        loadImages();
    }

    public Dimension getSize() {
        int height = 2 * (marginLeft + cupHeight) + padding;
        int width = 6 * (cupWidth + padding ) + 2 * (kallahWidth + marginLeft);
        System.out.println(width + " " + height);
        return new Dimension(width, height);
    }

    protected void drawCupsRow(Graphics g, int x, int y) {
        for (int i = 0; i < 6; ++i ) {
                g.drawOval(x, y, cupWidth, cupHeight);
                x += cupWidth + padding;
        }
    }

    protected void drawKallahs(Graphics g) {
        int resize = 20;

        g.setColor(playerTwoColor);
        g.drawRect(marginLeft, marginTop + resize,
                        kallahWidth, kallahHeight - resize*2);
                       
        
        int x = 2*padding + kallahWidth + 6 * ( padding + cupWidth );

        g.setColor(playerOneColor);
        g.drawRect(x, marginTop + resize,
                        kallahWidth, kallahHeight - resize*2);
    }

    public void drawBoard(Graphics g, boolean yourTurn) {
        
        if(yourTurn){
            g.drawImage(boardImagePlayer, 0, 0, null);
        }else{
            g.drawImage(boardImageOpponent, 0, 0, null);
        }
        
        //drawKallahs(g);

        int rowX = kallahWidth + padding * 2;

        g.setColor(playerTwoColor);
        //drawCupsRow(g, rowX, marginTop);

        g.setColor(playerOneColor);
        //drawCupsRow(g, rowX, 2*marginTop + cupHeight + padding );
        
    }

    public int getCupX(int cup) {
        int x;

        if ( cup == 6 || cup == 13 ) {
            x = kallahWidth / 2;
            x = (cup == 6) ? x + (2*padding + kallahWidth + 6 * ( padding + cupWidth))  : x + marginLeft;
        } else {

            
            if (cup > 6) cup = -cup + 12;

           
            x = padding + kallahWidth;
            x += padding * (cup+1);
            x += cup * cupWidth;
        }

        return x;
    }

    public int getCupY(int cup) {

        if ( cup <= 6 || cup == 13 ) {
            return marginTop * 2 + cupHeight + padding;
        }

        return marginTop;
    }

    public int getCupCenterX(int cup) {
        int x = getCupX(cup);
        
        if (cup != 6 && cup != 13) {
            x += cupWidth/2;
        }

        return x;
    }

    public int getCupCenterY(int cup) {
        int y = getCupY(cup);
        
        if (cup != 6 && cup != 13) {
            y += cupHeight/2;
        }

        return y;
    }
    
    public void loadImages(){
        ImageIcon image1 = new ImageIcon("src/resources/Jogador.png");
        boardImagePlayer = new BufferedImage(
        image1.getIconWidth(),
        image1.getIconHeight(),
        BufferedImage.TYPE_INT_RGB);
        Graphics g1 = boardImagePlayer.createGraphics();
        image1.paintIcon(null, g1, 0,0);
        g1.dispose();
        
        ImageIcon image2 = new ImageIcon("src/resources/Oponente.png");
        boardImageOpponent = new BufferedImage(
        image2.getIconWidth(),
        image2.getIconHeight(),
        BufferedImage.TYPE_INT_RGB);
        Graphics g2 = boardImageOpponent.createGraphics();
        image2.paintIcon(null, g2, 0,0);
        g2.dispose();
    }
}
