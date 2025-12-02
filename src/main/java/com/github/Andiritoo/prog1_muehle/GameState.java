package com.github.Andiritoo.prog1_muehle;

public class GameState {

    private Boolean gameInProgress;
    private GameNode[] board;
    private Player white;
    private Player black;
    private Boolean whiteToMove;

    public GameState() {
        whiteToMove = true;

        initBoard();
    }

    public void moveIsValid(Move move) {
        // Validate Move

        // Send to GUI --> Either the updatedBoard or "Invalidmove"

        // Request next move from Player
    }

    public void initBoard() {
        board = new GameNode[24];

        board[0] = new GameNode(1, 1, NodeValue.EMPTY);
        board[1] = new GameNode(1, 2, NodeValue.EMPTY);
        board[2] = new GameNode(1, 3, NodeValue.EMPTY);
        board[3] = new GameNode(1, 4, NodeValue.EMPTY);
        board[4] = new GameNode(1, 5, NodeValue.EMPTY);
        board[5] = new GameNode(1, 6, NodeValue.EMPTY);
        board[6] = new GameNode(1, 7, NodeValue.EMPTY);
        board[7] = new GameNode(1, 8, NodeValue.EMPTY);


        board[8] = new GameNode(2, 1, NodeValue.EMPTY);
        board[9] = new GameNode(2, 2, NodeValue.EMPTY);
        board[10] = new GameNode(2, 3, NodeValue.EMPTY);
        board[11] = new GameNode(2, 4, NodeValue.EMPTY);
        board[12] = new GameNode(2, 5, NodeValue.EMPTY);
        board[13] = new GameNode(2, 6, NodeValue.EMPTY);
        board[14] = new GameNode(2, 7, NodeValue.EMPTY);
        board[15] = new GameNode(2, 8, NodeValue.EMPTY);

        board[16] = new GameNode(3, 1, NodeValue.EMPTY);
        board[17] = new GameNode(3, 2, NodeValue.EMPTY);
        board[18] = new GameNode(3, 3, NodeValue.EMPTY);
        board[19] = new GameNode(3, 4, NodeValue.EMPTY);
        board[20] = new GameNode(3, 5, NodeValue.EMPTY);
        board[21] = new GameNode(3, 6, NodeValue.EMPTY);
        board[22] = new GameNode(3, 7, NodeValue.EMPTY);
        board[23] = new GameNode(3, 8, NodeValue.EMPTY);
    }

    public Boolean getGameInProgress() {
        return gameInProgress;
    }

    public void setGameInProgress(Boolean gameInProgress) {
        this.gameInProgress = gameInProgress;
    }

    public GameNode[] getBoard() {
        return board;
    }

    public void setBoard(GameNode[] board) {
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
