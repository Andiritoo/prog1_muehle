package com.github.Andiritoo.prog1_muehle.game;

import com.github.Andiritoo.prog1_muehle.common.Move;
import com.github.Andiritoo.prog1_muehle.player.Player;

public interface GameEngine {
    /**
     * @return the current {@link GameState}, which includes metadata about the game, which is in progress.
     */
    GameState getState();

    /**
     * @return whether a proposed move is allowed
     */
    boolean isMoveValid(Move move);

    /**
     * @param move the move to apply to the current {@link GameState}
     * @return the updated {@link GameState} with the move applied
     */
    GameState applyMove(Move move);

    /**
     * @return true if the game is finished, false if the game is in progress
     */
    boolean isGameOver();

    /**
     * @return the winner of the finished game or null if the game is in progress
     */
    Player getWinner();
}
