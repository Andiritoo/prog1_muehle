package prog1_muehle.llm;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import prog1_muehle.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class AIPlayer implements Player {

    private final ChatLanguageModel model;
    private final NodeValue playerColor;

    /**
     * Requires Ollama to be installed and running locally.
     */
    public AIPlayer(NodeValue playerColor) {
        this.playerColor = playerColor;
        this.model = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("llama3.2")
                .temperature(0.7)
                .timeout(Duration.ofMinutes(2))
                .build();
    }

    /**
     * Creates an AI player with a specific Ollama model.
     */
    public AIPlayer(NodeValue playerColor, String ollamaModelName) {
        this.playerColor = playerColor;
        this.model = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName(ollamaModelName)
                .temperature(0.7)
                .timeout(Duration.ofMinutes(2))
                .build();
    }

    /**
     * Creates an AI player with a custom LangChain4j model.
     */
    public AIPlayer(ChatLanguageModel customModel, NodeValue playerColor) {
        this.playerColor = playerColor;
        this.model = customModel;
    }

    @Override
    public Move move(GameState gameState) {
        String boardState = convertBoardToString(gameState);
        String prompt = buildPrompt(boardState, gameState);

        String response = model.generate(prompt);

        return parseMove(response, gameState);
    }

    private String convertBoardToString(GameState gameState) {
        StringBuilder sb = new StringBuilder();
        GameNode[] board = gameState.getBoard();

        sb.append("Board State (layer-point: value):\n");
        for (int layer = 1; layer <= 3; layer++) {
            sb.append("Layer ").append(layer).append(": ");
            for (int point = 1; point <= 8; point++) {
                GameNode node = findNode(board, layer, point);
                if (node != null) {
                    sb.append(point).append("=");
                    switch (node.getValue()) {
                        case EMPTY -> sb.append("_");
                        case WHITE -> sb.append("W");
                        case BLACK -> sb.append("B");
                    }
                    sb.append(" ");
                }
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    private String buildPrompt(String boardState, GameState gameState) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("You are playing Nine Men's Morris (Mühle). ");
        prompt.append("You are ").append(playerColor).append(".\n\n");
        prompt.append(boardState).append("\n");

        prompt.append("Game rules:\n");
        prompt.append("- During placement phase: Place a stone on any empty position\n");
        prompt.append("- During movement phase: Move a stone to an adjacent empty position\n");
        prompt.append("- When you form a mill (3 in a row): Remove an opponent's stone\n\n");

        prompt.append("Available moves:\n");
        List<String> availableMoves = getAvailableMoves(gameState);
        for (String move : availableMoves) {
            prompt.append("- ").append(move).append("\n");
        }

        prompt.append("\nRespond with ONLY the move in format: 'PLACE layer point' or 'MOVE from_layer from_point to_layer to_point' or 'REMOVE layer point'\n");
        prompt.append("Example: 'PLACE 1 3' or 'MOVE 1 2 2 2' or 'REMOVE 2 5'\n");

        return prompt.toString();
    }

    private List<String> getAvailableMoves(GameState gameState) {
        List<String> moves = new ArrayList<>();
        GameNode[] board = gameState.getBoard();

        // Check if we're in placement phase (counting our pieces)
        int myPieces = 0;
        for (GameNode node : board) {
            if (node.getValue() == playerColor) {
                myPieces++;
            }
        }

        if (myPieces < 9) {
            // Placement phase
            for (GameNode node : board) {
                if (node.getValue() == NodeValue.EMPTY) {
                    moves.add("PLACE " + node.getLayer() + " " + node.getPoint());
                }
            }
        } else {
            // Movement phase
            for (GameNode node : board) {
                if (node.getValue() == playerColor) {
                    List<GameNode> adjacentEmpty = getAdjacentEmpty(board, node);
                    for (GameNode target : adjacentEmpty) {
                        moves.add("MOVE " + node.getLayer() + " " + node.getPoint() +
                                " " + target.getLayer() + " " + target.getPoint());
                    }
                }
            }
        }

        return moves;
    }

    private List<GameNode> getAdjacentEmpty(GameNode[] board, GameNode from) {
        List<GameNode> adjacent = new ArrayList<>();
        int layer = from.getLayer();
        int point = from.getPoint();

        // Check adjacent points on same layer
        int prevPoint = point == 1 ? 8 : point - 1;
        int nextPoint = point == 8 ? 1 : point + 1;

        GameNode prev = findNode(board, layer, prevPoint);
        GameNode next = findNode(board, layer, nextPoint);

        if (prev != null && prev.getValue() == NodeValue.EMPTY) adjacent.add(prev);
        if (next != null && next.getValue() == NodeValue.EMPTY) adjacent.add(next);

        // Check connections to other layers (at points 2, 4, 6, 8)
        if (point % 2 == 0) {
            for (int otherLayer = 1; otherLayer <= 3; otherLayer++) {
                if (otherLayer != layer) {
                    GameNode connected = findNode(board, otherLayer, point);
                    if (connected != null && connected.getValue() == NodeValue.EMPTY) {
                        adjacent.add(connected);
                    }
                }
            }
        }

        return adjacent;
    }

    private Move parseMove(String response, GameState gameState) {
        Move move = new Move();
        String cleanResponse = response.trim().toUpperCase();

        try {
            if (cleanResponse.startsWith("PLACE")) {
                String[] parts = cleanResponse.split("\\s+");
                int layer = Integer.parseInt(parts[1]);
                int point = Integer.parseInt(parts[2]);

                GameNode target = findNode(gameState.getBoard(), layer, point);
                move.setTo(target);

            } else if (cleanResponse.startsWith("MOVE")) {
                String[] parts = cleanResponse.split("\\s+");
                int fromLayer = Integer.parseInt(parts[1]);
                int fromPoint = Integer.parseInt(parts[2]);
                int toLayer = Integer.parseInt(parts[3]);
                int toPoint = Integer.parseInt(parts[4]);

                GameNode from = findNode(gameState.getBoard(), fromLayer, fromPoint);
                GameNode to = findNode(gameState.getBoard(), toLayer, toPoint);

                move.setFrom(from);
                move.setTo(to);

            } else if (cleanResponse.startsWith("REMOVE")) {
                String[] parts = cleanResponse.split("\\s+");
                int layer = Integer.parseInt(parts[1]);
                int point = Integer.parseInt(parts[2]);

                GameNode target = findNode(gameState.getBoard(), layer, point);
                move.setTo(target);
            }
        } catch (Exception e) {
            System.err.println("Failed to parse move: " + response);
            // Fallback: return first available move
            move = getFirstAvailableMove(gameState);
        }

        return move;
    }

    private Move getFirstAvailableMove(GameState gameState) {
        Move move = new Move();
        GameNode[] board = gameState.getBoard();

        // Just find first empty spot for placement
        for (GameNode node : board) {
            if (node.getValue() == NodeValue.EMPTY) {
                move.setTo(node);
                break;
            }
        }

        return move;
    }

    private GameNode findNode(GameNode[] board, int layer, int point) {
        for (GameNode node : board) {
            if (node.getLayer() == layer && node.getPoint() == point) {
                return node;
            }
        }
        return null;
    }
}
