package com.github.Andiritoo.prog1_muehle;

import java.lang.reflect.Array;

import static com.github.Andiritoo.prog1_muehle.NodeValue.*;

public class GameState {

    private Boolean gameInProgress;
    private NodeValue[][] board = {{EMPTY, WHITE, EMPTY, EMPTY, BLACK, EMPTY, EMPTY, EMPTY}, {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY}, {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY}};
    private Player white;
    private Player black;
    private Boolean whiteToMove;

    public GameState() {

        whiteToMove = true;


    }

    public void moveIsValid(Move move) {
        // Validate Move

        // Send to GUI --> Either the updatedBoard or "Invalidmove"

        // Request next move from Player
    }

    /**
     *
     * @return the original board, don't modify with this Methode directly. Use...?
     */
    public NodeValue[][] getGameBoard(){
        return board;
    }


    public Boolean getGameInProgress() {
        return gameInProgress;
    }
    public void setGameInProgress() {
        gameInProgress = true;
    }

    public NodeValue[][] getBoard() {
        return board;
    }

    public void setBoard(NodeValue[][] board) {
        this.board = board;
    }

    public Player getWhite() {
        return white;
    }

    public void setWhite(Player white) {
        this.white = white;
    }

    public Player getBlack() {
        return black;
    }

    public void setBlack(Player black) {
        this.black = black;
    }

    public Boolean getWhiteToMove() {
        return whiteToMove;
    }

    public void setWhiteToMove(Boolean whiteToMove) {
        this.whiteToMove = whiteToMove;
    }
}
