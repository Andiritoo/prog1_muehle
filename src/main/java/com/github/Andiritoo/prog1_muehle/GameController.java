package com.github.Andiritoo.prog1_muehle;


public class GameController {

    private GameEngine engine;

    void startNewGame(Player white, Player black) {
        this.engine = new GameEngineImpl(white, black);
    }

    void handleUserMove(Move move) {
        if (engine == null) return;
        if (engine.isMoveValid(move)) {
            engine.applyMove(move);
        }
    }

    GameState getState() {
        return engine != null ? engine.getState() : null;
    }
}
