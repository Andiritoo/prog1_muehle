package com.github.Andiritoo.prog1_muehle.llmPlayer;

import com.github.Andiritoo.prog1_muehle.common.Move;
import com.github.Andiritoo.prog1_muehle.common.NodeValue;
import com.github.Andiritoo.prog1_muehle.game.GamePhase;
import com.github.Andiritoo.prog1_muehle.game.GameState;
import com.github.Andiritoo.prog1_muehle.player.BasePlayer;
import com.github.Andiritoo.prog1_muehle.player.Player;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;

import java.time.Duration;

import static com.github.Andiritoo.prog1_muehle.common.NodeValue.*;

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
        this.model = OllamaChatModel.builder()
                .baseUrl(LLM_URL)
                .modelName(LLM_MODEL)
                .temperature(0.7)
                .timeout(Duration.ofMinutes(2))
                .build();
    }

    @Override
    public Move move(GameState gameState) {
        String prompt = buildPrompt(gameState);
        System.out.println("=== AI PROMPT ===\n" + prompt);

        String response = model.generate(prompt);
        System.out.println("=== AI RESPONSE ===\n" + response);

        return parseMove(response, gameState);
    }

    private String convertBoardToString(GameState gameState) {
        StringBuilder sb = new StringBuilder();
        NodeValue[][] board = gameState.getBoard();

        sb.append("CURRENT BOARD STATE:\n");
        sb.append("Legend: EMPTY=., WHITE=W, BLACK=B\n\n");

        // Draw each layer as a square (positions go clockwise like a watch)
        for (int layer = 0; layer < 3; layer++) {
            String layerName = switch (layer) {
                case 0 -> "OUTER";
                case 1 -> "MIDDLE";
                case 2 -> "INNER";
                default -> "?";
            };

            int base = layer * 8;
            String p0 = getPiece(board, layer, 0);
            String p1 = getPiece(board, layer, 1);
            String p2 = getPiece(board, layer, 2);
            String p3 = getPiece(board, layer, 3);
            String p4 = getPiece(board, layer, 4);
            String p5 = getPiece(board, layer, 5);
            String p6 = getPiece(board, layer, 6);
            String p7 = getPiece(board, layer, 7);

            sb.append(layerName).append(" RING (positions ").append(base).append("-").append(base + 7).append("):\n");
            sb.append("  ").append(String.format("%2d", base)).append("=").append(p0)
              .append("---").append(String.format("%2d", base + 1)).append("=").append(p1)
              .append("---").append(String.format("%2d", base + 2)).append("=").append(p2).append("\n");
            sb.append("  |           |\n");
            sb.append("  ").append(String.format("%2d", base + 7)).append("=").append(p7)
              .append("       ").append(String.format("%2d", base + 3)).append("=").append(p3).append("\n");
            sb.append("  |           |\n");
            sb.append("  ").append(String.format("%2d", base + 6)).append("=").append(p6)
              .append("---").append(String.format("%2d", base + 5)).append("=").append(p5)
              .append("---").append(String.format("%2d", base + 4)).append("=").append(p4).append("\n\n");
        }

        sb.append("Positions go CLOCKWISE: 0(top-left) → 1(top) → 2(top-right) → 3(right) → 4(bottom-right) → 5(bottom) → 6(bottom-left) → 7(left)\n\n");

        // List empty positions explicitly
        sb.append("EMPTY POSITIONS: ");
        StringBuilder emptyList = new StringBuilder();
        for (int pos = 0; pos < 24; pos++) {
            int layer = pos / 8;
            int position = pos % 8;
            if (board[layer][position] == EMPTY) {
                if (emptyList.length() > 0) emptyList.append(", ");
                emptyList.append(pos);
            }
        }
        if (emptyList.length() == 0) {
            sb.append("None\n");
        } else {
            sb.append(emptyList).append("\n");
        }

        // List player positions
        sb.append("YOUR (").append(playerColor).append(") PIECES AT: ");
        StringBuilder yourPieces = new StringBuilder();
        for (int pos = 0; pos < 24; pos++) {
            int layer = pos / 8;
            int position = pos % 8;
            if (board[layer][position] == playerColor) {
                if (yourPieces.length() > 0) yourPieces.append(", ");
                yourPieces.append(pos);
            }
        }
        sb.append(yourPieces.length() > 0 ? yourPieces : "None").append("\n");

        // List opponent positions
        NodeValue opponent = (playerColor == WHITE) ? BLACK : WHITE;
        sb.append("OPPONENT (").append(opponent).append(") PIECES AT: ");
        StringBuilder oppPieces = new StringBuilder();
        for (int pos = 0; pos < 24; pos++) {
            int layer = pos / 8;
            int position = pos % 8;
            if (board[layer][position] == opponent) {
                if (oppPieces.length() > 0) oppPieces.append(", ");
                oppPieces.append(pos);
            }
        }
        sb.append(oppPieces.length() > 0 ? oppPieces : "None").append("\n");

        return sb.toString();
    }

    private String getPiece(NodeValue[][] board, int layer, int position) {
        return switch (board[layer][position]) {
            case EMPTY -> ".";
            case WHITE -> "W";
            case BLACK -> "B";
        };
    }

    private String buildPrompt(GameState gameState) {
        StringBuilder prompt = new StringBuilder();

        // Game explanation
        prompt.append("═══════════════════════════════════════════════════════════\n");
        prompt.append("           NINE MEN'S MORRIS (MÜHLE)\n");
        prompt.append("═══════════════════════════════════════════════════════════\n\n");

        prompt.append("GAME OBJECTIVE:\n");
        prompt.append("Reduce your opponent to 2 pieces OR block them so they cannot move.\n\n");

        prompt.append("WHAT IS A MILL?\n");
        prompt.append("A MILL is THREE of your pieces in a row (horizontally or vertically).\n");
        prompt.append("When you form a MILL, you get to REMOVE one opponent piece.\n");
        prompt.append("Mills can be:\n");
        prompt.append("  - Horizontal: Along one side of a square (e.g., 0-1-2 or 3-4-5)\n");
        prompt.append("  - Vertical: Along radial lines (e.g., 1-9-17 or 3-11-19)\n\n");

        prompt.append("HOW THE GAME WORKS:\n");
        prompt.append("1. PLACEMENT PHASE: Players alternate placing their 9 pieces on empty positions\n");
        prompt.append("   - Try to form mills while blocking opponent's mills\n");
        prompt.append("2. MOVEMENT PHASE: Once all pieces are placed, players move pieces to adjacent positions\n");
        prompt.append("   - Continue trying to form mills\n");
        prompt.append("3. FLYING/JUMP PHASE: When you have only 3 pieces left, you can jump to any empty position\n");
        prompt.append("   - This gives you more mobility when down to 3 pieces\n");
        prompt.append("4. WINNING: Opponent has only 2 pieces left OR opponent cannot move\n\n");

        prompt.append("STRATEGY TIPS:\n");
        prompt.append("- Try to create potential mills (2 pieces in a row with empty third spot)\n");
        prompt.append("- Occupy key positions (odd positions 1,3,5,7 etc. give more connectivity)\n");
        prompt.append("- Break opponent's potential mills when possible\n");
        prompt.append("- When removing opponent pieces, target pieces that complete their mills\n\n");

        prompt.append("YOUR ROLE:\n");
        prompt.append("You are playing as: ").append(playerColor).append("\n");
        prompt.append("Your opponent is: ").append(playerColor == WHITE ? "BLACK" : "WHITE").append("\n\n");

        // Current board state
        prompt.append(convertBoardToString(gameState));
        prompt.append("\n");

        // Game status
        GamePhase phase = determinePhase(gameState);
        int stonesToPlace = (playerColor == WHITE)
            ? gameState.getStonesToPlaceWhite()
            : gameState.getStonesToPlaceBlack();

        prompt.append("Game Status:\n");
        prompt.append("- Stones left to place: ").append(stonesToPlace).append("\n");
        prompt.append("- Current phase: ").append(phase).append("\n");

        if (gameState.isAwaitingRemove()) {
            prompt.append("- ACTION REQUIRED: You formed a MILL! Remove an opponent's piece.\n");
            prompt.append(getRemovablePositions(gameState)).append("\n");
        } else if (phase == GamePhase.MOVE) {
            prompt.append(getMovablePositions(gameState)).append("\n");
        } else {
            prompt.append("\n");
        }

        // Board topology
        prompt.append("BOARD TOPOLOGY (positions go CLOCKWISE like a clock):\n");
        prompt.append("Each square has 8 positions numbered clockwise:\n");
        prompt.append("  0───1───2    Position meanings:\n");
        prompt.append("  │       │    0=top-left corner, 1=top-middle, 2=top-right corner\n");
        prompt.append("  7       3    3=right-middle, 4=bottom-right corner, 5=bottom-middle\n");
        prompt.append("  │       │    6=bottom-left corner, 7=left-middle\n");
        prompt.append("  6───5───4\n\n");

        prompt.append("Three concentric squares:\n");
        prompt.append("  OUTER (0-7), MIDDLE (8-15), INNER (16-23)\n\n");

        prompt.append("ADJACENCY RULES:\n");
        prompt.append("1. Each position connects to neighbors on same ring (clockwise/counter-clockwise):\n");
        prompt.append("   - Position N connects to N-1 and N+1 (wraps around: 0↔7)\n");
        prompt.append("2. RADIAL connections at ODD positions (1,3,5,7 and 9,11,13,15 and 17,19,21,23):\n");
        prompt.append("   - Position 1 (top) ↔ 9 (middle top) ↔ 17 (inner top)\n");
        prompt.append("   - Position 3 (right) ↔ 11 (middle right) ↔ 19 (inner right)\n");
        prompt.append("   - Position 5 (bottom) ↔ 13 (middle bottom) ↔ 21 (inner bottom)\n");
        prompt.append("   - Position 7 (left) ↔ 15 (middle left) ↔ 23 (inner left)\n\n");

        prompt.append("Examples of adjacencies:\n");
        prompt.append("  - Pos 0: adjacent to 1, 7 (corner, same ring only)\n");
        prompt.append("  - Pos 1: adjacent to 0, 2, 9 (middle position, has radial to 9)\n");
        prompt.append("  - Pos 9: adjacent to 1, 8, 10, 17 (middle position, radial to both 1 and 17)\n\n");

        prompt.append("CURRENT SITUATION:\n");
        prompt.append("Phase: ").append(phase).append("\n");
        if (phase == GamePhase.PLACE) {
            prompt.append("  → In PLACEMENT: Place your piece on ANY empty position to form mills\n");
        } else if (phase == GamePhase.MOVE) {
            prompt.append("  → In MOVEMENT: Move to ADJACENT positions to form mills\n");
        } else if (phase == GamePhase.JUMP) {
            prompt.append("  → In FLYING/JUMP: You have 3 pieces - can jump to ANY empty position!\n");
        }
        if (gameState.isAwaitingRemove()) {
            prompt.append("  → YOU FORMED A MILL! Remove one opponent piece now!\n");
        }
        prompt.append("\n");

        // Response format with emphasis
        prompt.append("═══════════════════════════════════════\n");
        prompt.append("RESPONSE FORMAT - READ CAREFULLY:\n");
        prompt.append("═══════════════════════════════════════\n");

        if (gameState.isAwaitingRemove()) {
            prompt.append("Format: REMOVE <position>\n");
            prompt.append("Rules:\n");
            prompt.append("  - <position> MUST be an opponent's piece position\n");
            prompt.append("  - Choose from opponent positions listed above\n");
            prompt.append("Example: REMOVE 15\n\n");
        } else if (phase == GamePhase.PLACE) {
            prompt.append("Format: PLACE <position>\n");
            prompt.append("Rules:\n");
            prompt.append("  - <position> MUST be from the EMPTY POSITIONS list above\n");
            prompt.append("  - DO NOT place on positions with W or B\n");
            prompt.append("  - Only use positions marked with . (dot)\n");
            prompt.append("Example: PLACE 7\n\n");
        } else if (phase == GamePhase.JUMP) {
            prompt.append("Format: MOVE <from> <to>\n");
            prompt.append("Rules:\n");
            prompt.append("  - <from> MUST be one of YOUR piece positions\n");
            prompt.append("  - <to> MUST be from the EMPTY POSITIONS list\n");
            prompt.append("  - JUMP phase: can move to ANY empty position\n");
            prompt.append("Example: MOVE 8 16\n\n");
        } else {
            prompt.append("Format: MOVE <from> <to>\n");
            prompt.append("Rules:\n");
            prompt.append("  - <from> MUST be one of YOUR piece positions\n");
            prompt.append("  - <to> MUST be from the EMPTY POSITIONS list\n");
            prompt.append("  - <to> MUST be adjacent to <from> (see adjacency rules)\n");
            prompt.append("  - See 'Your pieces that can move' section for valid moves\n");
            prompt.append("Example: MOVE 8 9\n\n");
        }

        prompt.append("CRITICAL: Reply with ONLY the command. No explanations, no reasoning.\n");
        prompt.append("═══════════════════════════════════════\n\n");
        prompt.append("Your move:\n");

        return prompt.toString();
    }

    private GamePhase determinePhase(GameState gameState) {
        int stonesToPlace = (playerColor == WHITE)
            ? gameState.getStonesToPlaceWhite()
            : gameState.getStonesToPlaceBlack();

        if (stonesToPlace > 0) {
            return GamePhase.PLACE;
        }

        // Count pieces on board
        int piecesOnBoard = 0;
        NodeValue[][] board = gameState.getBoard();
        for (int layer = 0; layer < 3; layer++) {
            for (int position = 0; position < 8; position++) {
                if (board[layer][position] == playerColor) {
                    piecesOnBoard++;
                }
            }
        }

        return (piecesOnBoard == 3) ? GamePhase.JUMP : GamePhase.MOVE;
    }

    private String getRemovablePositions(GameState gameState) {
        StringBuilder sb = new StringBuilder();
        NodeValue opponent = (playerColor == WHITE) ? BLACK : WHITE;
        NodeValue[][] board = gameState.getBoard();

        sb.append("- Opponent pieces you can remove: ");
        for (int pos = 0; pos < 24; pos++) {
            int layer = pos / 8;
            int position = pos % 8;
            if (board[layer][position] == opponent) {
                sb.append(pos).append(" ");
            }
        }
        return sb.toString();
    }

    private String getMovablePositions(GameState gameState) {
        StringBuilder sb = new StringBuilder();
        NodeValue[][] board = gameState.getBoard();
        boolean hasMovablePieces = false;

        sb.append("- Your pieces that can move:\n");
        for (int from = 0; from < 24; from++) {
            int layer = from / 8;
            int pos = from % 8;
            if (board[layer][pos] == playerColor) {
                // Check if this piece has any valid adjacent empty positions
                StringBuilder moves = new StringBuilder();
                for (int to = 0; to < 24; to++) {
                    int toLayer = to / 8;
                    int toPos = to % 8;
                    if (board[toLayer][toPos] == EMPTY && isAdjacent(from, to)) {
                        if (moves.length() > 0) moves.append(", ");
                        moves.append(to);
                    }
                }
                if (moves.length() > 0) {
                    sb.append("  Pos ").append(from).append(" → ").append(moves).append("\n");
                    hasMovablePieces = true;
                }
            }
        }

        if (!hasMovablePieces) {
            sb.append("  WARNING: No valid moves available! You may be blocked.\n");
        }

        return sb.toString();
    }

    private Move parseMove(String response, GameState gameState) {
        String cleanResponse = response.trim().toUpperCase();

        try {
            if (cleanResponse.startsWith("PLACE")) {
                // Format: PLACE <position>
                String[] parts = cleanResponse.split("\\s+");
                if (parts.length < 2) {
                    System.err.println("AI: Invalid PLACE format - missing position");
                    return getFallbackMove(gameState);
                }

                int position = Integer.parseInt(parts[1]);
                if (position < 0 || position > 23) {
                    System.err.println("AI: Invalid position: " + position);
                    return getFallbackMove(gameState);
                }

                // Validate position is empty
                int layer = position / 8;
                int pos = position % 8;
                if (gameState.getBoard()[layer][pos] != EMPTY) {
                    System.err.println("AI: Position " + position + " is not empty");
                    return getFallbackMove(gameState);
                }

                return new Move(-1, position);

            } else if (cleanResponse.startsWith("MOVE")) {
                // Format: MOVE <from> <to>
                String[] parts = cleanResponse.split("\\s+");
                if (parts.length < 3) {
                    System.err.println("AI: Invalid MOVE format - missing positions");
                    return getFallbackMove(gameState);
                }

                int from = Integer.parseInt(parts[1]);
                int to = Integer.parseInt(parts[2]);

                if (from < 0 || from > 23 || to < 0 || to > 23) {
                    System.err.println("AI: Invalid positions: from=" + from + " to=" + to);
                    return getFallbackMove(gameState);
                }

                // Validate from position has our piece
                int fromLayer = from / 8;
                int fromPos = from % 8;
                if (gameState.getBoard()[fromLayer][fromPos] != playerColor) {
                    System.err.println("AI: Position " + from + " does not contain our piece");
                    return getFallbackMove(gameState);
                }

                // Validate to position is empty
                int toLayer = to / 8;
                int toPos = to % 8;
                if (gameState.getBoard()[toLayer][toPos] != EMPTY) {
                    System.err.println("AI: Position " + to + " is not empty");
                    return getFallbackMove(gameState);
                }

                // Validate adjacency (unless in JUMP phase)
                GamePhase phase = determinePhase(gameState);
                if (phase == GamePhase.MOVE && !isAdjacent(from, to)) {
                    System.err.println("AI: Positions " + from + " and " + to + " are not adjacent");
                    return getFallbackMove(gameState);
                }

                return new Move(from, to);

            } else if (cleanResponse.startsWith("REMOVE")) {
                // Format: REMOVE <position>
                String[] parts = cleanResponse.split("\\s+");
                if (parts.length < 2) {
                    System.err.println("AI: Invalid REMOVE format - missing position");
                    return getFallbackMove(gameState);
                }

                int position = Integer.parseInt(parts[1]);
                if (position < 0 || position > 23) {
                    System.err.println("AI: Invalid position: " + position);
                    return getFallbackMove(gameState);
                }

                // Validate position has opponent's piece
                int layer = position / 8;
                int pos = position % 8;
                NodeValue opponent = (playerColor == WHITE) ? BLACK : WHITE;
                if (gameState.getBoard()[layer][pos] != opponent) {
                    System.err.println("AI: Position " + position + " does not contain opponent's piece");
                    return getFallbackMove(gameState);
                }

                return new Move(position, -1);
            }
        } catch (NumberFormatException e) {
            System.err.println("AI: Failed to parse move from response: " + cleanResponse);
            return getFallbackMove(gameState);
        }

        System.err.println("AI: Unrecognized move format: " + cleanResponse);
        return getFallbackMove(gameState);
    }

    /**
     * Fallback move when AI response cannot be parsed.
     * Returns the first valid move available.
     */
    private Move getFallbackMove(GameState gameState) {
        System.err.println("AI: Using fallback move");
        NodeValue[][] board = gameState.getBoard();
        NodeValue opponent = (playerColor == WHITE) ? BLACK : WHITE;

        // Check if awaiting remove
        if (gameState.isAwaitingRemove()) {
            for (int pos = 0; pos < 24; pos++) {
                int layer = pos / 8;
                int position = pos % 8;
                if (board[layer][position] == opponent) {
                    return new Move(pos, -1);
                }
            }
        }

        // Check phase
        GamePhase phase = determinePhase(gameState);

        if (phase == GamePhase.PLACE) {
            // Find first empty position
            for (int pos = 0; pos < 24; pos++) {
                int layer = pos / 8;
                int position = pos % 8;
                if (board[layer][position] == EMPTY) {
                    return new Move(-1, pos);
                }
            }
        } else {
            // Find first valid move
            for (int from = 0; from < 24; from++) {
                int fromLayer = from / 8;
                int fromPos = from % 8;
                if (board[fromLayer][fromPos] == playerColor) {
                    // Try adjacent positions
                    for (int to = 0; to < 24; to++) {
                        int toLayer = to / 8;
                        int toPos = to % 8;
                        if (board[toLayer][toPos] == EMPTY) {
                            if (phase == GamePhase.JUMP || isAdjacent(from, to)) {
                                return new Move(from, to);
                            }
                        }
                    }
                }
            }
        }

        // No valid move found (should not happen in a valid game)
        return new Move(-1, -1);
    }

    /**
     * Checks if two positions are adjacent on the board.
     */
    private boolean isAdjacent(int pos1, int pos2) {
        int layer1 = pos1 / 8;
        int position1 = pos1 % 8;
        int layer2 = pos2 / 8;
        int position2 = pos2 % 8;

        // Same layer - check if positions are adjacent (circular)
        if (layer1 == layer2) {
            int diff = Math.abs(position1 - position2);
            return diff == 1 || diff == 7; // Adjacent or wrapping around
        }

        // Different layers - only connected at odd positions (1, 3, 5, 7)
        // and only between adjacent layers
        if (position1 == position2 && position1 % 2 == 1 && Math.abs(layer1 - layer2) == 1) {
            return true;
        }

        return false;
    }
}
