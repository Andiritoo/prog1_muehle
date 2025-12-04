package com.github.Andiritoo.prog1_muehle.player;


import com.github.Andiritoo.prog1_muehle.common.Move;
import com.github.Andiritoo.prog1_muehle.game.GameState;

public class HumanPlayer implements Player {

    @Override
    public Move move(GameState gameState) {
        return new Move(-1, 2);
    }
}

