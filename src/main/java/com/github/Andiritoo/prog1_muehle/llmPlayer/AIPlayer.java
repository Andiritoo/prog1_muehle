package com.github.Andiritoo.prog1_muehle.llmPlayer;

import com.github.Andiritoo.prog1_muehle.common.Move;
import com.github.Andiritoo.prog1_muehle.common.NodeValue;
import com.github.Andiritoo.prog1_muehle.game.GameState;
import com.github.Andiritoo.prog1_muehle.player.BasePlayer;
import com.github.Andiritoo.prog1_muehle.player.Player;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class AIPlayer extends BasePlayer implements Player {
    private static final String LLM_URL = "http://localhost:11434";
    private static final String LLM_MODEL = "llama3.2";

    private final ChatLanguageModel model;
    private final NodeValue playerColor;

    /**
     * Requires Ollama to be installed and running locally.
     */
    public AIPlayer(NodeValue playerColor) {
        this.playerColor = playerColor;
        this.model = OllamaChatModel.builder().baseUrl(LLM_URL).modelName(LLM_MODEL)
                .temperature(0.7).timeout(Duration.ofMinutes(2)).build();
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
        NodeValue[][] board = gameState.getBoard();

        sb.append("Board State (layer-position: value):\n");
        for (int layer = 0; layer < 3; layer++) {
            sb.append("Layer ").append(layer + 1).append(": ");
            for (int position = 0; position < 8; position++) {
                sb.append(position + 1).append("=");
                switch (board[layer][position]) {
                    case EMPTY -> sb.append("_");
                    case WHITE -> sb.append("W");
                    case BLACK -> sb.append("B");
                }
                sb.append(" ");
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

        if (gameState.isAwaitingRemove()) {
            prompt.append("YOU FORMED A MILL! You must REMOVE one of your opponent's stones.\n\n");
        }

        prompt.append("Available moves:\n");
        List<String> availableMoves = getAvailableMoves(gameState);
        for (String move : availableMoves) {
            prompt.append("- ").append(move).append("\n");
        }

        prompt.append("\n=== IMPORTANT INSTRUCTIONS ===\n");
        prompt.append("1. Choose EXACTLY ONE move from the list above\n");
        prompt.append("2. Copy it EXACTLY as shown\n");
        prompt.append("3. Do not add any explanation or extra text\n");
        prompt.append("4. Valid formats:\n");
        prompt.append("   - PLACE <layer> <point>  (example: PLACE 1 3)\n");
        prompt.append("   - MOVE <from_layer> <from_point> <to_layer> <to_point>  (example: MOVE 1 2 2 2)\n");
        prompt.append("   - REMOVE <layer> <point>  (example: REMOVE 2 5)\n");
        prompt.append("\nYour move (choose from available moves above):\n");

        return prompt.toString();
    }

    private List<String> getAvailableMoves(GameState gameState) {
        List<String> moves = new ArrayList<>();
        NodeValue[][] board = gameState.getBoard();

        if (gameState.isAwaitingRemove()) {
            NodeValue opponent = (playerColor == NodeValue.WHITE) ? NodeValue.BLACK : NodeValue.WHITE;
            for (int layer = 0; layer < 3; layer++) {
                for (int position = 0; position < 8; position++) {
                    if (board[layer][position] == opponent) {
                        moves.add("REMOVE " + (layer + 1) + " " + (position + 1));
                    }
                }
            }
            return moves;
        }

        int myPieces = 0;
        for (int layer = 0; layer < 3; layer++) {
            for (int position = 0; position < 8; position++) {
                if (board[layer][position] == playerColor) {
                    myPieces++;
                }
            }
        }

        if (myPieces < 9) {
            // Placement phase
            for (int layer = 0; layer < 3; layer++) {
                for (int position = 0; position < 8; position++) {
                    if (board[layer][position] == NodeValue.EMPTY) {
                        moves.add("PLACE " + (layer + 1) + " " + (position + 1));
                    }
                }
            }
        } else {
            // Movement phase
            for (int layer = 0; layer < 3; layer++) {
                for (int position = 0; position < 8; position++) {
                    if (board[layer][position] == playerColor) {
                        List<int[]> adjacentEmpty = getAdjacentEmpty(board, layer, position);
                        for (int[] target : adjacentEmpty) {
                            moves.add("MOVE " + (layer + 1) + " " + (position + 1) + " "
                                    + (target[0] + 1) + " " + (target[1] + 1));
                        }
                    }
                }
            }
        }

        return moves;
    }

    private List<int[]> getAdjacentEmpty(NodeValue[][] board, int layer, int position) {
        List<int[]> adjacent = new ArrayList<>();

        // Check adjacent positions on same layer (circular)
        int prevPosition = position == 0 ? 7 : position - 1;
        int nextPosition = position == 7 ? 0 : position + 1;

        if (board[layer][prevPosition] == NodeValue.EMPTY) {
            adjacent.add(new int[] {layer, prevPosition});
        }
        if (board[layer][nextPosition] == NodeValue.EMPTY) {
            adjacent.add(new int[] {layer, nextPosition});
        }

        // Check connections to other layers (at even positions: 1, 3, 5, 7 in 1-based, which are 0,
        // 2, 4, 6 in 0-based)
        // According to GameState comment: even positions (2,4,6,8 in 1-based) allow layer changes
        // In 0-based indexing, that's positions 1, 3, 5, 7
        if (position % 2 == 1) {
            for (int otherLayer = 0; otherLayer < 3; otherLayer++) {
                if (otherLayer != layer && board[otherLayer][position] == NodeValue.EMPTY && Math.abs(otherLayer - layer) == 1) {
                    adjacent.add(new int[] {otherLayer, position});
                }
            }
        }

        return adjacent;
    }

    private Move parseMove(String response, GameState gameState) {
        String cleanResponse = response.trim().toUpperCase();

        try {
            if (cleanResponse.startsWith("PLACE")) {
                String[] parts = cleanResponse.split("\\s+");
                if (parts.length < 3) {
                    return getFirstAvailableMove(gameState);
                }

                int layer = Integer.parseInt(parts[1]) - 1; // Convert to 0-based
                int position = Integer.parseInt(parts[2]) - 1; // Convert to 0-based

                if (layer < 0 || layer > 2 || position < 0 || position > 7) {
                    return getFirstAvailableMove(gameState);
                }

                int targetIndex = boardIndex(layer, position);
                return new Move(-1, targetIndex);

            } else if (cleanResponse.startsWith("MOVE")) {
                String[] parts = cleanResponse.split("\\s+");
                if (parts.length < 5) {
                    return getFirstAvailableMove(gameState);
                }

                int fromLayer = Integer.parseInt(parts[1]) - 1; // Convert to 0-based
                int fromPosition = Integer.parseInt(parts[2]) - 1; // Convert to 0-based
                int toLayer = Integer.parseInt(parts[3]) - 1; // Convert to 0-based
                int toPosition = Integer.parseInt(parts[4]) - 1; // Convert to 0-based

                if (fromLayer < 0 || fromLayer > 2 || fromPosition < 0 || fromPosition > 7 ||
                    toLayer < 0 || toLayer > 2 || toPosition < 0 || toPosition > 7) {
                    return getFirstAvailableMove(gameState);
                }

                int fromIndex = boardIndex(fromLayer, fromPosition);
                int toIndex = boardIndex(toLayer, toPosition);
                return new Move(fromIndex, toIndex);

            } else if (cleanResponse.startsWith("REMOVE")) {
                String[] parts = cleanResponse.split("\\s+");
                if (parts.length < 3) {
                    return getFirstAvailableMove(gameState);
                }

                int layer = Integer.parseInt(parts[1]) - 1; // Convert to 0-based
                int position = Integer.parseInt(parts[2]) - 1; // Convert to 0-based

                if (layer < 0 || layer > 2 || position < 0 || position > 7) {
                    return getFirstAvailableMove(gameState);
                }

                int targetIndex = boardIndex(layer, position);
                return new Move(targetIndex, -1);
            }
        } catch (Exception e) {
            return getFirstAvailableMove(gameState);
        }

        return getFirstAvailableMove(gameState);
    }

    private Move getFirstAvailableMove(GameState gameState) {
        NodeValue[][] board = gameState.getBoard();

        // Determine game phase
        int stonesToPlace = (playerColor == NodeValue.WHITE)
            ? gameState.getStonesToPlaceWhite()
            : gameState.getStonesToPlaceBlack();

        // Check if awaiting remove
        if (gameState.isAwaitingRemove()) {
            // Find first opponent stone
            NodeValue opponent = (playerColor == NodeValue.WHITE) ? NodeValue.BLACK : NodeValue.WHITE;
            for (int layer = 0; layer < 3; layer++) {
                for (int position = 0; position < 8; position++) {
                    if (board[layer][position] == opponent) {
                        int targetIndex = boardIndex(layer, position);
                        return new Move(targetIndex, -1);
                    }
                }
            }
        }

        // If in placement phase
        if (stonesToPlace > 0) {
            for (int layer = 0; layer < 3; layer++) {
                for (int position = 0; position < 8; position++) {
                    if (board[layer][position] == NodeValue.EMPTY) {
                        int targetIndex = boardIndex(layer, position);
                        return new Move(-1, targetIndex);
                    }
                }
            }
        } else {
            for (int layer = 0; layer < 3; layer++) {
                for (int position = 0; position < 8; position++) {
                    if (board[layer][position] == playerColor) {
                        List<int[]> adjacentEmpty = getAdjacentEmpty(board, layer, position);
                        if (!adjacentEmpty.isEmpty()) {
                            int fromIndex = boardIndex(layer, position);
                            int[] target = adjacentEmpty.get(0);
                            int toIndex = boardIndex(target[0], target[1]);
                            return new Move(fromIndex, toIndex);
                        }
                    }
                }
            }
        }

        return new Move(-1, -1);
    }

    /**
     * Converts layer and position to a single board index. Board index = layer * 8 + position
     */
    private int boardIndex(int layer, int position) {
        return layer * 8 + position;
    }
}
