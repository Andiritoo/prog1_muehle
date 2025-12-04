package com.github.Andiritoo.prog1_muehle.game;


import com.github.Andiritoo.prog1_muehle.common.Move;
import com.github.Andiritoo.prog1_muehle.player.Player;

public class GameController {

    private GameEngine engine;

    public void startNewGame(Player white, Player black) {
        this.engine = new GameEngineImpl(white, black);
    }

    public void handleUserMove(Move move) {
        if (engine == null) return;
        if (engine.isMoveValid(move)) {
            engine.applyMove(move);
        } else {
            System.out.printf("invalid move");
        }
    }

    public GameState getState() {
        return engine != null ? engine.getState() : null;
    }

    public GamePhase getGamePhase() {
        return engine != null ? engine.getGamePhaseForCurrentPlayer() : null;
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
