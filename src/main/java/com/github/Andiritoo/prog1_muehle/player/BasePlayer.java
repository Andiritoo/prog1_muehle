package com.github.Andiritoo.prog1_muehle.player;

public abstract class BasePlayer {
    private String playerName;
    private int gamesWon;

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getGamesWon() {
        return gamesWon;
    }

    public void setGamesWon(int gamesWon) {
        this.gamesWon = gamesWon;
    }

    public void incrementGameWon() {
        this.gamesWon++;
    }
}
