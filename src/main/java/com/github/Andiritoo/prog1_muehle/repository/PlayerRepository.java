package com.github.Andiritoo.prog1_muehle.repository;

import com.github.Andiritoo.prog1_muehle.humanPlayer.HumanPlayer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PlayerRepository {

    private static final String DATA_FILE = "players.dat";
    private static List<HumanPlayer> players = null;

    public static List<HumanPlayer> getPlayers() {
        if (players == null) {
            players = loadPlayers();
        }
        return players;
    }

    public static void savePlayers() {
        if (players == null) {
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE))) {
            for (HumanPlayer player : players) {
                writer.write(player.getPlayerName() + "|" + player.getGamesWon());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving players: " + e.getMessage());
        }
    }

    public static HumanPlayer findOrCreatePlayer(String name) {
        List<HumanPlayer> playerList = getPlayers();

        for (HumanPlayer player : playerList) {
            if (player.getPlayerName().equals(name)) {
                return player;
            }
        }

        HumanPlayer newPlayer = new HumanPlayer();
        newPlayer.setPlayerName(name);
        newPlayer.setGamesWon(0);
        playerList.add(newPlayer);
        return newPlayer;
    }

    private static List<HumanPlayer> loadPlayers() {
        List<HumanPlayer> loadedPlayers = new ArrayList<>();
        File file = new File(DATA_FILE);

        if (!file.exists()) {
            return loadedPlayers;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 2) {
                    String name = parts[0];
                    int wins = Integer.parseInt(parts[1]);

                    HumanPlayer player = new HumanPlayer();
                    player.setPlayerName(name);
                    player.setGamesWon(wins);
                    loadedPlayers.add(player);
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error loading players: " + e.getMessage());
        }

        return loadedPlayers;
    }
}
