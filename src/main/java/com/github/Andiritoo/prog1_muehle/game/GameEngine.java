package com.github.Andiritoo.prog1_muehle.game;

import com.github.Andiritoo.prog1_muehle.common.Move;
import com.github.Andiritoo.prog1_muehle.player.Player;

public interface GameEngine {
    GameState getState();

    boolean isMoveValid(Move move);

    GameState applyMove(Move move);

    boolean isGameOver();

    Player getWinner();

    GamePhase getGamePhase();

    boolean isAwaitingRemove();

    boolean isAwaitingMove();
}
