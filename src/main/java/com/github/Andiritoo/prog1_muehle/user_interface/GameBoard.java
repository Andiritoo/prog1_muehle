package com.github.Andiritoo.prog1_muehle.user_interface;
import ch.trick17.gui.Gui;
import ch.trick17.gui.component.Drawable;
import com.github.Andiritoo.prog1_muehle.*;

import static com.github.Andiritoo.prog1_muehle.NodeValue.*;


public class GameBoard implements Drawable {

    private NodeValue[][] board;
    private double size;

    public GameBoard(NodeValue[][] board, double size){
        this.board = board;
        this.size = size;
    }

    @Override
    public void draw(Gui gui) {
        double nodeSize = size * 0.01;
        double piceSize = size * 0.02;
        double[] xCoordinates = {0,0.5,1,1,1,0.5,0,0};
        double[] yCoordinates = {0,0,0,0.5,1,1,1,0.5};
        double layerOffset = 0.0;
        double offset = (0.8/6);
        double border = size * 0.1;

        for(int i = 0; i<board.length; i++){
            double xCoord = border + (size * layerOffset);
            double yCoord = ((border/2)*3) + (size * layerOffset);
            //System.out.println("x: " + xCoord + " y: " + yCoord); //for testing
            double sideLength = size-(xCoord*2);
            NodeValue[] layer = board[i];

            gui.setStrokeWidth(size*0.0033333333);
            gui.drawRect(xCoord, yCoord, sideLength, sideLength);
            gui.drawLine(xCoord + (sideLength * 0.5), yCoord, xCoord + (sideLength * 0.5), yCoord + (size*offset));
            gui.drawLine(xCoord + sideLength, yCoord + (sideLength * 0.5), xCoord + sideLength - (size*offset), yCoord + (sideLength * 0.5));
            gui.drawLine(xCoord + (sideLength * 0.5), yCoord + sideLength, xCoord + (sideLength * 0.5), yCoord + sideLength-(size*offset));
            gui.drawLine(xCoord, yCoord + (sideLength * 0.5), xCoord + (size*offset), yCoord + (sideLength * 0.5));

            for (int j = 0; j < layer.length; j++){
                double centerX = xCoord + (sideLength * xCoordinates[j]);
                double centerY = yCoord + (sideLength * yCoordinates[j]);

                if (layer[j] == WHITE) {
                    gui.setColor(255,255,255);
                    gui.fillCircle(centerX,centerY,piceSize);
                    gui.setColor(0,0,0);
                    gui.drawCircle(centerX,centerY,piceSize);
                    gui.drawCircle(centerX,centerY,piceSize*0.6);

                } else if (layer[j] == BLACK){
                    gui.setColor(0,0,0);
                    gui.fillCircle(centerX,centerY,piceSize);
                    gui.setColor(255,255,255);
                    gui.drawCircle(centerX,centerY,piceSize*0.6);
                } else {
                    gui.setColor(0,0,0);
                    gui.fillCircle(centerX,centerY,nodeSize);
                }

            }

            layerOffset += offset;
        }




    }
}
