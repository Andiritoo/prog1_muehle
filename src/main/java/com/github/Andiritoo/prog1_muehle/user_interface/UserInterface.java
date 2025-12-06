package com.github.Andiritoo.prog1_muehle.user_interface;

import ch.trick17.gui.Gui;
import com.github.Andiritoo.prog1_muehle.game.GameController;
import com.github.Andiritoo.prog1_muehle.player.BasePlayer;
import com.github.Andiritoo.prog1_muehle.player.Player;
import com.github.Andiritoo.prog1_muehle.repository.PlayerRepository;

import java.util.ArrayList;
import java.util.List;

public class UserInterface {

    public static Gui gui;
    private static GameBoard currentGameBoard;

    public static void startGui(String[] args) {
        int width = 1000;
        int height = 1000;

        gui = Gui.create("Mühli", width, height);

        // Start with leaderboard
        openLeaderboard(null);

        gui.open();
        gui.runUntilClosed();
    }

    public static void openLeaderboard(List<BasePlayer> players) {
        if (players == null || players.isEmpty()) {
            players = new ArrayList<>();
        }

        Leaderboard leaderboard = new Leaderboard(players);
        gui.addComponent(leaderboard);
    }

    public static void startGame(Player whitePlayer, Player blackPlayer, List<BasePlayer> players) {
        // Ensure we have a valid player list
        if (players == null) {
            players = new ArrayList<>();
        }

        // Add players to the list if they don't exist yet
        addPlayerToListIfNew(players, (BasePlayer) whitePlayer);
        addPlayerToListIfNew(players, (BasePlayer) blackPlayer);

        GameController controller = new GameController();
        controller.startNewGame(whitePlayer, blackPlayer);

        GameBoard board = new GameBoard(
                controller,
                gui,
                (BasePlayer) whitePlayer,
                (BasePlayer) blackPlayer,
                players
        );

        gui.addComponent(board);
    }

    private static void addPlayerToListIfNew(List<BasePlayer> players, BasePlayer player) {
        // Check if player already exists in list by name
        boolean exists = false;
        for (BasePlayer p : players) {
            if (p.getPlayerName().equals(player.getPlayerName())) {
                exists = true;
                // Update the reference with existing stats
                player.setGamesWon(p.getGamesWon());
                break;
            }
        }

        // Add new player to list
        if (!exists) {
            players.add(player);
        }
    }
}