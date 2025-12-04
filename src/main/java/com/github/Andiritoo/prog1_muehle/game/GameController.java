package com.github.Andiritoo.prog1_muehle.game;


import com.github.Andiritoo.prog1_muehle.common.Move;
import com.github.Andiritoo.prog1_muehle.player.Player;

public class GameController {

    private GameEngine engine;

    public void startNewGame(Player white, Player black) {
        this.engine = new GameEngineImpl(white, black);
        runGameLoop(white, black);
    }

    //TODO: polish, just example for demo
    private void runGameLoop(Player white, Player black) {
        GameState state = engine.getState();

        while (!engine.isGameOver()) {
            Player currentPlayer = state.isWhiteToMove() ? white : black;
            System.out.println((state.isWhiteToMove() ? "White" : "Black") + " player's turn");

            Move move = currentPlayer.move(state);

            if (engine.isMoveValid(move)) {
                engine.applyMove(move);
                System.out.println("Move applied: from=" + move.getFrom() + " to=" + move.getTo());

                if (engine.isAwaitingRemove()) {
                    Move removeMove = currentPlayer.move(state);
                    if (engine.isMoveValid(removeMove)) {
                        engine.applyMove(removeMove);
                        System.out.println("Piece removed at: " + removeMove.getRemove());
                    }
                }
            } else {
                System.out.println("Invalid move attempted!");
            }

            state = engine.getState();
        }

        System.out.println("Game Over!");
        Player winner = engine.getWinner();
        if (winner != null) {
            System.out.println("Winner: " + (winner == white ? "White" : "Black"));
        } else {
            System.out.println("Game ended in a draw");
        }
    }

    public void handleUserMove(Move move) {
        if (engine == null) return;
        if (engine.isMoveValid(move)) {
            engine.applyMove(move);
        }
    }

    public GameState getState() {
        return engine != null ? engine.getState() : null;
    }

    public GamePhase getGamePhase() {
        return engine != null ? engine.getGamePhase() : null;
    }

    public boolean isAwaitingMove() {
        return engine != null && engine.isAwaitingMove();
    }

    public boolean isAwaitingRemove() {
        return engine != null && engine.isAwaitingRemove();
    }

    public boolean isGameOver() {
        return engine != null && engine.isGameOver();
    }

    public Player getWinner() {
        return engine != null ? engine.getWinner() : null;
    }
}
