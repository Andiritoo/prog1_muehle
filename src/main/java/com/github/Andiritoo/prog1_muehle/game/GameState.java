package com.github.Andiritoo.prog1_muehle.game;


import com.github.Andiritoo.prog1_muehle.common.NodeValue;
import com.github.Andiritoo.prog1_muehle.player.Player;

import static com.github.Andiritoo.prog1_muehle.common.NodeValue.*;

public class GameState {

    private Boolean gameInProgress;

    private NodeValue[][] board = {{EMPTY, WHITE, EMPTY, EMPTY, BLACK, EMPTY, EMPTY, EMPTY}, {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY}, {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY}};
    private Player white;
    private Player black;
    private int stonesToPlaceWhite;
    private int stonesToPlaceBlack;
    private Boolean whiteToMove;

    /**
     * @return Returns the Mill game board as a 3x8 two-dimensional array of nodes.
     *         - board[layer][position]: layer 0-2 (outer to inner), position 0-7 (clockwise from top-left)
     *         - Position 0: top-left, Position 2: top-right, Position 4: bottom-right, Position 6: bottom-left
     *         - Odd positions (1,3,5,7) are the midpoints between corners
     *         - Even positions (2,4,6,8) are the nodes where players can change layer.
     *         - Returns the original board, don't modify with this Methode directly.
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
