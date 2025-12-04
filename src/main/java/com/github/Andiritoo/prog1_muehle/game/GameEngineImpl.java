package com.github.Andiritoo.prog1_muehle.game;

import com.github.Andiritoo.prog1_muehle.common.Move;
import com.github.Andiritoo.prog1_muehle.player.Player;

public class GameEngineImpl implements GameEngine {

    private final GameState state;

    public GameEngineImpl(Player white, Player black) {
        state = new GameState();
        state.setWhite(white);
        state.setBlack(black);
        state.setWhiteToMove(true);
        state.setStonesToPlaceWhite(9);
        state.setStonesToPlaceBlack(9);
        state.setGameInProgress(true);
    }

    // TODO: Return defensive copy
    @Override
    public GameState getState() {
        return state;
    }

    @Override
    public boolean isMoveValid(Move move) {
        return true;
    }

    @Override
    public GameState applyMove(Move move) {
        state.setWhiteToMove(!state.isWhiteToMove());
        return state;
    }

    @Override
    public boolean isGameOver() {
        return false;
    }

    @Override
    public Player getWinner() {
        return null;
    }

    @Override
    public GamePhase getGamePhase() {
        return null;
    }

    @Override
    public boolean isAwaitingRemove() {
        return false;
    }

    @Override
    public boolean isAwaitingMove() {
        return false;
    }
}
