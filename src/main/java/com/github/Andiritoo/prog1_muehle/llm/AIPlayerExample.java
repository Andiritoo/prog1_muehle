package com.github.Andiritoo.prog1_muehle.llm;

import com.github.Andiritoo.prog1_muehle.GameState;
import com.github.Andiritoo.prog1_muehle.NodeValue;
import com.github.Andiritoo.prog1_muehle.Player;

/**
 * SETUP:
 * 1. Install Ollama: https://ollama.ai/
 * 2. Start Ollama service: ollama serve
 * 3. Pull a model: ollama pull llama3.2
 */
public class AIPlayerExample {

    public static void main(String[] args) {
        GameState gameState = new GameState();

        Player whitePlayer = new AIPlayer(NodeValue.WHITE);
        gameState.setWhite(whitePlayer);

        Player blackPlayer = new AIPlayer(NodeValue.BLACK);
        gameState.setBlack(blackPlayer);

        System.out.println("Asking White AI Player for a move...");
        var move = whitePlayer.move(gameState);

        System.out.println("\nAI suggests move:");
        if (move.getFrom() == null && move.getTo() != null) {
            System.out.println("PLACE at layer " + move.getTo().getLayer() +
                             " point " + move.getTo().getPoint());
        } else if (move.getFrom() != null && move.getTo() != null) {
            System.out.println("MOVE from layer " + move.getFrom().getLayer() +
                             " point " + move.getFrom().getPoint() +
                             " to layer " + move.getTo().getLayer() +
                             " point " + move.getTo().getPoint());
        } else if (move.getFrom() != null && move.getTo() == null) {
            System.out.println("REMOVE at layer " + move.getFrom().getLayer() +
                             " point " + move.getFrom().getPoint());
        }
    }
}
