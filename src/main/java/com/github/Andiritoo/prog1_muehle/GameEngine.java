package com.github.Andiritoo.prog1_muehle;

public interface GameEngine {
    GameState getState();

    boolean isMoveValid(Move move);

    GameState applyMove(Move move);

    boolean isGameOver();

    Player getWinner();
}
