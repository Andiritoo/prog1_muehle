package com.github.Andiritoo.prog1_muehle.user_interface;

import ch.trick17.gui.Gui;
import com.github.Andiritoo.prog1_muehle.botPlayer.BotPlayer;
import com.github.Andiritoo.prog1_muehle.common.NodeValue;
import com.github.Andiritoo.prog1_muehle.game.GameController;
import com.github.Andiritoo.prog1_muehle.humanPlayer.HumanPlayer;
import com.github.Andiritoo.prog1_muehle.llmPlayer.AIPlayer;
import com.github.Andiritoo.prog1_muehle.player.Player;

public class UserInterface {

    public static void start(String[] args) {
        Gui gui = Gui.create("Mühli", 1000, 1000);

        GameController controller = new GameController();

        Player whitePlayer = new HumanPlayer();
        Player blackPlayer = new BotPlayer();
        // Player blackPlayer = new AIPlayer(NodeValue.BLACK);

        controller.startNewGame(whitePlayer, blackPlayer);

        GameBoard board = new GameBoard(controller.getState().getBoard(),
                Math.min(gui.getHeight(), gui.getWidth()));
        gui.addComponent(board);

        gui.setResizable(true);
        gui.open();
        gui.runUntilClosed(20);
    }
}
