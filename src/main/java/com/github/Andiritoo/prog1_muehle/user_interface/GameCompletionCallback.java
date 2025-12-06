package com.github.Andiritoo.prog1_muehle.user_interface;

import com.github.Andiritoo.prog1_muehle.player.Player;

@FunctionalInterface
public interface GameCompletionCallback {
    void onGameComplete(Player winner);
}
