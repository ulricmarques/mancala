package com.ulric.mancala;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

/**
 *
 * @author Ulric
 */
class Board {

    final int outerPadding = 15, innerPadding = 20;
    final int cupWidth = 75, cupHeight = 90;
    final int kallahWidth = 80, kallahHeight = 205;
    final Color playerOneColor, playerTwoColor;

    public Board(Color playerOneColor, Color playerTwoColor) {
        this.playerOneColor = playerOneColor;
        this.playerTwoColor = playerTwoColor;
    }

    public Dimension getSize() {
        int height = 2 * (outerPadding + cupHeight) + innerPadding;
        int width = 6 * (cupWidth + innerPadding ) + 2 * (kallahWidth + outerPadding);
        return new Dimension(width, height);
    }

    protected void drawCupsRow(Graphics g, int x, int y) {
        for (int i = 0; i < 6; ++i ) {
                g.drawOval(x, y, cupWidth, cupHeight);
                x += cupWidth + outerPadding;
        }
    }

    protected void drawKallahs(Graphics g) {
        int round = 30;
        int resize = 20;

        g.setColor(playerOneColor);
        g.drawRoundRect(outerPadding, outerPadding + resize,
                        kallahWidth, kallahHeight - resize*2,
                        round, round);

        
        int x = outerPadding + kallahWidth + 6 * ( innerPadding + cupWidth );

        g.setColor(playerTwoColor);
        g.drawRoundRect(x, outerPadding + resize,
                        kallahWidth, kallahHeight - resize*2,
                        round, round);
    }

    public void drawBoard(Graphics g) {
        drawKallahs(g);

        int rowX = kallahWidth + innerPadding * 2;

        g.setColor(playerOneColor);
        drawCupsRow(g, rowX, outerPadding);

        g.setColor(playerTwoColor);
        drawCupsRow(g, rowX, outerPadding + cupHeight + innerPadding );
    }

    public int getCupX(int cup) {
        int x;

        if ( cup == 6 || cup == 13 ) {
                x = outerPadding + kallahWidth / 2;


                x = (cup == 6) ? getSize().width - x : x;
        } else {

                // reverse the top row numbers
                if (cup > 6) cup = -cup + 12;

                // begin with outside padding + mancala
                x = outerPadding + kallahWidth;

                // add padding for each box
                x += outerPadding * (cup+1);

                // add boxes
                x += cup * cupWidth;
        }

        return x;
    }

    public int getCupY(int cup) {

        // check if a cup is a kallah or in the second row
        if ( cup <= 6 || cup == 13 ) {
                return outerPadding * 2 + cupHeight;
        }

        return outerPadding;
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
}
