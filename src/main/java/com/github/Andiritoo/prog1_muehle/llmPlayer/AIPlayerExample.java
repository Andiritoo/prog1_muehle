package com.github.Andiritoo.prog1_muehle.llmPlayer;

import com.github.Andiritoo.prog1_muehle.common.NodeValue;
import com.github.Andiritoo.prog1_muehle.game.GameState;
import com.github.Andiritoo.prog1_muehle.player.Player;

/**
 * SETUP: 1. Install Ollama: https://ollama.ai/ 2. Start Ollama service: ollama serve 3. Pull a
 * model: ollama pull llama3.2
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
        if (move.getFrom() == -1 && move.getTo() != -1) {
            int layer = move.getTo() / 8;
            int position = move.getTo() % 8;
            System.out.println("PLACE at layer " + (layer + 1) + " position " + (position + 1));
        } else if (move.getFrom() != -1 && move.getTo() != -1) {
            int fromLayer = move.getFrom() / 8;
            int fromPosition = move.getFrom() % 8;
            int toLayer = move.getTo() / 8;
            int toPosition = move.getTo() % 8;
            System.out.println(
                    "MOVE from layer " + (fromLayer + 1) + " position " + (fromPosition + 1)
                            + " to layer " + (toLayer + 1) + " position " + (toPosition + 1));
        } else if (move.getFrom() != -1 && move.getTo() == -1) {
            int layer = move.getFrom() / 8;
            int position = move.getFrom() % 8;
            System.out.println("REMOVE at layer " + (layer + 1) + " position " + (position + 1));
        }
    }
}
