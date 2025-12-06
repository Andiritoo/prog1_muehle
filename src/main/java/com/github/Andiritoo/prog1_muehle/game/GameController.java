package com.github.Andiritoo.prog1_muehle.game;


import com.github.Andiritoo.prog1_muehle.common.Move;
import com.github.Andiritoo.prog1_muehle.player.Player;

public class GameController {

    private GameEngine engine;
    private String lastMoveError = null;

    /**
     * Initializes the {@link GameEngine} and with that starts a new game.
     */
    public void startNewGame(Player white, Player black) {
        this.engine = new GameEngineImpl(white, black);
    }

    public GameState getState() {
        return engine != null ? engine.getState() : null;
    }

    public boolean isGameOver() {
        return engine != null && engine.isGameOver();
    }

    public Player getWinner() {
        return engine != null ? engine.getWinner() : null;
    }

    /**
     * @return the player who's turn it is currently
     */
    public Player getCurrentPlayer() {
        if (engine == null || engine.getState() == null) {
            return null;
        }
        return engine.getState().isWhiteToMove()
            ? engine.getState().getWhite()
            : engine.getState().getBlack();
    }

    public boolean isWhiteToMove() {
        return engine != null && engine.getState() != null && engine.getState().isWhiteToMove();
    }

    public boolean isAwaitingRemove() {
        return engine != null && engine.getState() != null && engine.getState().isAwaitingRemove();
    }

    public GamePhase getGamePhase() {
        return engine != null ? engine.getPhaseForCurrentPlayer() : null;
    }

    /**
     * Requests a move from the current player if the game is not over yet.
     * The move returned by the player is then validated and only executed if it's a valid move.
     */
    public void executeCurrentPlayerMove() {
        if (engine == null || isGameOver()) {
            return;
        }

        Player currentPlayer = getCurrentPlayer();
        if (currentPlayer == null) {
            return;
        }

        Move move = currentPlayer.move(getState());
        if (move == null) {
            return;
        }

        if (engine.isMoveValid(move)) {
            engine.applyMove(move);
            lastMoveError = null; // Clear error on successful move
        } else {
            // Set error message for invalid move
            lastMoveError = "Invalid move!";
        }
    }

    public String getAndClearLastMoveError() {
        String error = lastMoveError;
        lastMoveError = null;
        return error;
    }
}
