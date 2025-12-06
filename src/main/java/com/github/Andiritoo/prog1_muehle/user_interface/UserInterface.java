package com.github.Andiritoo.prog1_muehle.user_interface;

import ch.trick17.gui.Gui;
import com.github.Andiritoo.prog1_muehle.game.GameController;
import com.github.Andiritoo.prog1_muehle.humanPlayer.HumanPlayer;
import com.github.Andiritoo.prog1_muehle.player.Player;
import com.github.Andiritoo.prog1_muehle.repository.PlayerRepository;

import java.util.ArrayList;

public class UserInterface {

    public static Gui gui;
    private static GameBoard currentGameBoard;

    public static void startGui(String[] args) {
        int width = 1000;
        int height = 1000;

        gui = Gui.create("Mühli", width, height);

        // Leaderboard
        openLeaderboard();

        gui.open();
        gui.runUntilClosed();
    }

    public static void openLeaderboard() {
        Leaderboard leaderboard = new Leaderboard(new ArrayList<>(PlayerRepository.getPlayers()));
        gui.addComponent(leaderboard);
    }

    public static void startGame(Player whitePlayer, Player blackPlayer) {
        GameController controller = new GameController();
        controller.startNewGame(whitePlayer, blackPlayer);

        GameCompletionCallback callback = winner -> {
            // Increment winner's games won if they are a human player
            if (winner != null) {
                if (winner instanceof HumanPlayer) {
                    HumanPlayer humanWinner = (HumanPlayer) winner;
                    humanWinner.incrementGameWon();
                    System.out.println("Game won by: " + humanWinner.getPlayerName() + " (wins: " + humanWinner.getGamesWon() + ")");
                } else {
                    System.out.println("Game won by non-human player");
                }
            } else {
                System.out.println("Game ended with no winner");
            }

            // Save player data
            PlayerRepository.savePlayers();

            // Remove game board and return to leaderboard
            if (currentGameBoard != null) {
                gui.removeComponent(currentGameBoard);
                currentGameBoard = null;
            }

            openLeaderboard();
        };

        currentGameBoard = new GameBoard(controller, gui, callback);

        if (whitePlayer instanceof HumanPlayer) {
            ((HumanPlayer) whitePlayer).setInputProvider(currentGameBoard);
        }
        if (blackPlayer instanceof HumanPlayer) {
            ((HumanPlayer) blackPlayer).setInputProvider(currentGameBoard);
        }

        gui.addComponent(currentGameBoard);
    }
}
