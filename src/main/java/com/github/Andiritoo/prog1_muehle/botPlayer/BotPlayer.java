package com.github.Andiritoo.prog1_muehle.botPlayer;

import com.github.Andiritoo.prog1_muehle.common.Move;
import com.github.Andiritoo.prog1_muehle.game.GameState;
import com.github.Andiritoo.prog1_muehle.player.BasePlayer;
import com.github.Andiritoo.prog1_muehle.player.Player;

public class BotPlayer extends BasePlayer implements Player {

  // 3 sections: place, solve and survive

  // Basic requirements: select a next valid field randomly

  // Ultimate requirement: calculate the next step based on all valid Options and find valid options
  // where the opponent doesn't immediately have the chance to make a point

  @Override
  public Move move(GameState gameState) {
    return new Move(-1, 2);
  }

}
