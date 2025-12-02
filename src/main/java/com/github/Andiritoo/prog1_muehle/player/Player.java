package com.github.Andiritoo.prog1_muehle.player;

import com.github.Andiritoo.prog1_muehle.game.GameState;
import com.github.Andiritoo.prog1_muehle.common.Move;

public interface Player {
    Move move(GameState gameState);
}
