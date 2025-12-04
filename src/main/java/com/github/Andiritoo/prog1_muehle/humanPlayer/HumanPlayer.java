package com.github.Andiritoo.prog1_muehle.humanPlayer;


import com.github.Andiritoo.prog1_muehle.common.Move;
import com.github.Andiritoo.prog1_muehle.game.GameState;
import com.github.Andiritoo.prog1_muehle.player.Player;

public class HumanPlayer extends BasePlayer implements Player {
    @Override
    public Move move(GameState gameState) {
        return new Move(-1, 2);
    }
}

