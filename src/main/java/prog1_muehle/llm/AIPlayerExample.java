package prog1_muehle.llm;

import prog1_muehle.GameState;
import prog1_muehle.NodeValue;
import prog1_muehle.Player;

/**
 * SETUP:
 * 1. Install Ollama: https://ollama.ai/
 * 2. Start Ollama service: ollama serve
 * 3. Pull a model: ollama pull llama3.2
 */
public class AIPlayerExample {

    public static void main(String[] args) {
        System.out.println("Starting AI Player Example with FREE local LLM...");
        System.out.println("Make sure Ollama is running: ollama serve");
        System.out.println("And you have a model installed: ollama pull llama3.2\n");

        // Create game state
        GameState gameState = new GameState();

        // Create AI player for white (no API key needed!)
        Player whitePlayer = new AIPlayer(NodeValue.WHITE);
        gameState.setWhite(whitePlayer);

        // Create AI player for black (optional - could be human or another AI)
        Player blackPlayer = new AIPlayer(NodeValue.BLACK);
        gameState.setBlack(blackPlayer);

        // Get a move from the AI
        System.out.println("Asking AI for a move...");
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
        } else if (move.getFrom() == null && move.getTo() == null) {
            System.out.println("REMOVE at layer " + move.getTo().getLayer() +
                             " point " + move.getTo().getPoint());
        }
    }

    /**
     * Example: Use with a different Ollama model
     */
    public static Player createWithDifferentModel() {
        // You can specify a different model like mistral, phi, codellama, etc.
        return new AIPlayer(NodeValue.WHITE, "mistral");
    }
}
