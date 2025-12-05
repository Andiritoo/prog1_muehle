package com.github.Andiritoo.prog1_muehle.game;

import com.github.Andiritoo.prog1_muehle.common.NodeValue;
import com.github.Andiritoo.prog1_muehle.player.Player;

import static com.github.Andiritoo.prog1_muehle.common.NodeValue.EMPTY;

public class GameState {

    private boolean gameInProgress;

    private NodeValue[][] board;

    private Player white;
    private Player black;
    private int stonesToPlaceWhite;
    private int stonesToPlaceBlack;
    private boolean whiteToMove;

    private boolean awaitingRemove;

    private Player winner;

    public GameState() {
        board = new NodeValue[3][8];
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 8; j++)
                board[i][j] = EMPTY;
    }


    public boolean isGameInProgress() {
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

    public boolean isWhiteToMove() {
        return whiteToMove;
    }

    public void setWhiteToMove(boolean whiteToMove) {
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

    public boolean isAwaitingRemove() {
        return awaitingRemove;
    }

    public void setAwaitingRemove(boolean awaitingRemove) {
        this.awaitingRemove = awaitingRemove;
    }

    public Player getWinner() {
        return winner;
    }

    public void setWinner(Player winner) {
        this.winner = winner;
    }
}
