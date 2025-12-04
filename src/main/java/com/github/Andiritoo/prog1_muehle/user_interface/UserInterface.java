package com.github.Andiritoo.prog1_muehle.user_interface;

import ch.trick17.gui.Gui;
import com.github.Andiritoo.prog1_muehle.botPlayer.BotPlayer;
import com.github.Andiritoo.prog1_muehle.game.GameController;
import com.github.Andiritoo.prog1_muehle.humanPlayer.HumanPlayer;
import com.github.Andiritoo.prog1_muehle.player.BasePlayer;
import com.github.Andiritoo.prog1_muehle.player.Player;

import java.util.ArrayList;
import java.util.List;

public class UserInterface {

    public static Gui gui;

    public static void startGui(String[] args) {
        int width = 1000;
        int height = 1000;

        gui = Gui.create("Mühli", width, height);


        // Leaderboard
        openLeaderboard(null);

        gui.open();
        gui.runUntilClosed();
    }

    public static void openLeaderboard(List<BasePlayer> players) {
        if(players == null || players.isEmpty()) {
            players = new ArrayList<>();
        }

        Leaderboard leaderboard = new Leaderboard(players);
        gui.addComponent(leaderboard);
    }

    public static void startGame(Player whitePlayer, Player blackPlayer) {

        GameController controller = new GameController();
        controller.startNewGame(whitePlayer, blackPlayer);
        GameBoard board = new GameBoard(
                controller,
                gui
        );
        gui.addComponent(board);
    }
}
