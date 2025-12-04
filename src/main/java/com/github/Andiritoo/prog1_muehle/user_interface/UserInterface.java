package com.github.Andiritoo.prog1_muehle.user_interface;

import ch.trick17.gui.Gui;
import com.github.Andiritoo.prog1_muehle.common.NodeValue;
import com.github.Andiritoo.prog1_muehle.game.GameController;
import com.github.Andiritoo.prog1_muehle.llm.AIPlayer;
import com.github.Andiritoo.prog1_muehle.player.HumanPlayer;
import com.github.Andiritoo.prog1_muehle.player.Player;

public class UserInterface {

    public static void main(String[] args) {
        Gui gui = Gui.create("Mühli", 1000, 1000);

        GameController controller = new GameController();

        Player whitePlayer = new HumanPlayer();
        Player blackPlayer = new AIPlayer(NodeValue.BLACK);

        controller.startNewGame(whitePlayer, blackPlayer);

        GameBoard board = new GameBoard(controller.getState().getBoard(), Math.min(gui.getHeight(), gui.getWidth()));
        gui.addComponent(board);

        gui.setResizable(true);
        gui.open();
        gui.runUntilClosed(20);
    }
}
