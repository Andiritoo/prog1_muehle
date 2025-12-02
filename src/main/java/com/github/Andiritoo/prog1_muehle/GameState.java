package com.github.Andiritoo.prog1_muehle;


import static com.github.Andiritoo.prog1_muehle.NodeValue.*;

public class GameState {

    private Boolean gameInProgress;
    private NodeValue[][] board = {
            {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
            {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
            {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY}
    };
    private Player white;
    private Player black;
    private int stonesToPlaceWhite;
    private int stonesToPlaceBlack;
    private Boolean whiteToMove;

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

    public void setGameInProgress(boolean gameInProgress) {
        this.gameInProgress = gameInProgress;
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

    public int getStonesToPlaceWhite() {
        return stonesToPlaceWhite;
    }

    public void setStonesToPlaceWhite(int stonesToPlaceWhite) {
        this.stonesToPlaceWhite = stonesToPlaceWhite;
    }

    public int getStonesToPlaceBlack() {
        return stonesToPlaceBlack;
    }

    public void setStonesToPlaceBlack(int stonesToPlaceBlack) {
        this.stonesToPlaceBlack = stonesToPlaceBlack;
    }
}
