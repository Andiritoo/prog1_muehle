package com.github.Andiritoo.prog1_muehle;

public class HumanPlayer {
    private int stontesToPlace;
    private GamePhase playerGamePhase;

    public HumanPlayer(int stontesToPlace, GamePhase playerGamePhase) {
        this.stontesToPlace = stontesToPlace;
        this.playerGamePhase = playerGamePhase;
    }

    public int getStontesToPlace() {
        return stontesToPlace;
    }

    public void setStontesToPlace(int stontesToPlace) {
        this.stontesToPlace = stontesToPlace;
    }

    public GamePhase getPlayerGamePhase() {
        return playerGamePhase;
    }

    public void setPlayerGamePhase(GamePhase playerGamePhase) {
        this.playerGamePhase = playerGamePhase;
    }
}

