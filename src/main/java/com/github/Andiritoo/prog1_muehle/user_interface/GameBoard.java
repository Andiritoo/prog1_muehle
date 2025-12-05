package com.github.Andiritoo.prog1_muehle.user_interface;

import ch.trick17.gui.Gui;
import ch.trick17.gui.component.Clickable;
import ch.trick17.gui.component.Drawable;
import ch.trick17.gui.component.Rectangle;
import ch.trick17.gui.component.Shape;
import com.github.Andiritoo.prog1_muehle.common.Move;
import com.github.Andiritoo.prog1_muehle.common.NodeValue;
import com.github.Andiritoo.prog1_muehle.game.GameController;
import com.github.Andiritoo.prog1_muehle.game.GamePhase;
import com.github.Andiritoo.prog1_muehle.player.BasePlayer;

import java.util.Arrays;
import java.util.List;

import static com.github.Andiritoo.prog1_muehle.common.NodeValue.BLACK;
import static com.github.Andiritoo.prog1_muehle.common.NodeValue.WHITE;

public class GameBoard implements Drawable, Clickable {

    private static final double BORDER_RATIO = 0.2;
    private static final double NODE_SIZE_RATIO = 0.02;
    private static final double PIECE_SIZE_RATIO = 0.04;
    private static final double STROKE_WIDTH_RATIO = 0.003;
    private static final double STOP_BUTTON_WIDTH_RATIO = 0.2;
    private static final double STOP_BUTTON_HEIGHT_RATIO = 0.06;

    // Node positions on each square (0=top-left, going clockwise)
    private static final double[] NODE_X = {0, 0.5, 1, 1, 1, 0.5, 0, 0};
    private static final double[] NODE_Y = {0, 0, 0, 0.5, 1, 1, 1, 0.5};

    private final GameController controller;
    private final Gui gui;
    private final BasePlayer whitePlayer;
    private final BasePlayer blackPlayer;
    private final List<BasePlayer> allPlayers;

    private Integer selected = null;
    private boolean gameEnded = false;
    private long gameStartTime;
    private boolean gameStarted = false;

    // Invalid move tracking
    private String invalidMoveMessage = "";
    private long invalidMoveTime = 0;
    private static final long INVALID_MOVE_DISPLAY_TIME = 1500; // 1.5 seconds

    // Stop button rectangle
    private Rect stopButton;

    // Simple rectangle helper type
    private static class Rect {
        double x, y, w, h;

        Rect(double x, double y, double w, double h) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }

        boolean contains(double px, double py) {
            return px >= x && px <= x + w && py >= y && py <= y + h;
        }
    }

    public GameBoard(GameController controller, Gui gui,
                     BasePlayer whitePlayer, BasePlayer blackPlayer,
                     List<BasePlayer> allPlayers) {
        this.controller = controller;
        this.gui = gui;
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        this.allPlayers = allPlayers;
        this.gameStartTime = System.currentTimeMillis();
    }

    @Override
    public void draw(Gui gui) {
        // Check if 2 seconds have passed since game start
        long currentTime = System.currentTimeMillis();
        if (!gameStarted && currentTime - gameStartTime >= 2000) {
            gameStarted = true;
        }

        // Check for game end condition
        if (!gameEnded && gameStarted) {
            checkGameEnd();
        }

        double screenW = gui.getWidth();
        double screenH = gui.getHeight();

        // Draw player information
        drawPlayerInfo(gui, screenW, screenH);

        // Draw stop button
        drawStopButton(gui, screenW, screenH);

        // Draw invalid move message if needed
        if (currentTime - invalidMoveTime < INVALID_MOVE_DISPLAY_TIME && !invalidMoveMessage.isEmpty()) {
            drawInvalidMoveMessage(gui, screenW, screenH);
        }

        // Draw the game board
        double size = Math.min(screenW, screenH);
        double border = size * BORDER_RATIO;
        double usableSpace = size - 2 * border;

        double nodeSize = usableSpace * NODE_SIZE_RATIO;
        double pieceSize = usableSpace * PIECE_SIZE_RATIO;

        gui.setStrokeWidth(size * STROKE_WIDTH_RATIO);

        // Draw all layers with proper positioning
        for (int layer = 0; layer < 3; layer++) {
            drawLayer(gui, layer, border, usableSpace, nodeSize, pieceSize);
        }

        // Draw countdown if game hasn't started yet
        if (!gameStarted) {
            drawCountdown(gui, screenW, screenH, currentTime);
        }
    }

    private void drawInvalidMoveMessage(Gui gui, double screenW, double screenH) {
        double centerX = screenW / 2;
        double messageY = screenH * 0.85;
        double fontSize = screenH * 0.025;

        gui.setFontSize((int) fontSize);
        gui.setColor(255, 0, 0); // Red for error

        // Estimate center position
        double estimatedCharWidth = fontSize * 0.5;
        double estimatedTextWidth = invalidMoveMessage.length() * estimatedCharWidth;
        gui.drawString(invalidMoveMessage, centerX - estimatedTextWidth / 2, messageY);
    }

    private void drawCountdown(Gui gui, double screenW, double screenH, long currentTime) {
        long elapsed = currentTime - gameStartTime;
        long remaining = 2000 - elapsed;
        double seconds = remaining / 1000.0;

        // Draw semi-transparent overlay
        gui.setColor(0, 0, 0);
        gui.fillRect(0, 0, screenW, screenH);

        // Draw countdown text
        String countdownText = String.format("Game starts in %.1f seconds", seconds);
        gui.setFontSize((int) (screenH * 0.05));
        gui.setColor(255, 255, 255);

        // Estimate center position
        double estimatedCharWidth = screenH * 0.05 * 0.6;
        double estimatedTextWidth = countdownText.length() * estimatedCharWidth;
        gui.drawString(countdownText, screenW/2 - estimatedTextWidth/2, screenH/2);
    }

    private void drawPlayerInfo(Gui gui, double screenW, double screenH) {
        double padding = screenW * 0.05;
        double fontSize = screenH * 0.03;

        // Set font for player names
        gui.setFontSize((int) fontSize);

        boolean isRemovePhase = controller.isAwaitingRemove();
        boolean isWhiteTurn = controller.isWhiteToMove();
        boolean isBlackTurn = controller.isBlackToMove();

        // Draw white player info (top left)
        if (isWhiteTurn && gameStarted && !isRemovePhase) {
            gui.setColor(0, 200, 0); // Green for active player
        } else {
            gui.setColor(0, 0, 0); // Black for inactive player
        }
        String whiteText = whitePlayer.getPlayerName() + " (White)";
        gui.drawString(whiteText, padding, padding + fontSize);

        // Draw black player info (top right)
        if (isBlackTurn && gameStarted && !isRemovePhase) {
            gui.setColor(0, 200, 0); // Green for active player
        } else {
            gui.setColor(0, 0, 0); // Black for inactive player
        }
        String blackText = blackPlayer.getPlayerName() + " (Black)";
        // Estimate position for right alignment
        double estimatedCharWidth = fontSize * 0.6;
        double estimatedTextWidth = blackText.length() * estimatedCharWidth;
        gui.drawString(blackText, screenW - padding - estimatedTextWidth, padding + fontSize);

        // Draw turn indicator if game is not ended and has started
        if (!gameEnded && gameStarted) {
            drawTurnIndicator(gui, screenW, screenH, isRemovePhase, isWhiteTurn, isBlackTurn);
        }
    }

    private void drawTurnIndicator(Gui gui, double screenW, double screenH,
                                   boolean isRemovePhase, boolean isWhiteTurn, boolean isBlackTurn) {
        double centerX = screenW / 2;
        double topY = screenH * 0.05;
        double fontSize = screenH * 0.025;

        gui.setFontSize((int) fontSize);

        String turnText;

        if (isRemovePhase) {
            // In remove phase, the player who just formed a Mühle gets to remove
            // This is the player whose turn it was BEFORE the mill was formed
            // So it's the OPPOSITE of the current turn
            String playerName = isWhiteTurn ? blackPlayer.getPlayerName() : whitePlayer.getPlayerName();
            String playerColor = isWhiteTurn ? "Black" : "White";
            turnText = playerName + " (" + playerColor + ") must remove opponent's piece";
            gui.setColor(200, 0, 0); // Red for remove phase
        } else {
            GamePhase phase = controller.getGamePhase();
            if (phase != null) {
                String playerName = isWhiteTurn ? whitePlayer.getPlayerName() : blackPlayer.getPlayerName();
                String playerColor = isWhiteTurn ? "White" : "Black";

                switch (phase) {
                    case PLACE:
                        turnText = playerName + " (" + playerColor + ") - Place a piece";
                        break;
                    case MOVE:
                        turnText = playerName + " (" + playerColor + ") - Move a piece";
                        break;
                    case JUMP:
                        turnText = playerName + " (" + playerColor + ") - Jump a piece";
                        break;
                    default:
                        turnText = playerName + " (" + playerColor + ") - Your turn";
                }
                gui.setColor(0, 200, 0); // Green for normal turn
            } else {
                turnText = "Game in progress";
                gui.setColor(0, 0, 0);
            }
        }

        // Estimate center position
        double estimatedCharWidth = fontSize * 0.5;
        double estimatedTextWidth = turnText.length() * estimatedCharWidth;
        gui.drawString(turnText, centerX - estimatedTextWidth / 2, topY);

        // Draw a colored indicator next to the player name
        if (!isRemovePhase) {
            drawPlayerIndicator(gui, screenW, screenH, isWhiteTurn);
        }
    }

    private void drawPlayerIndicator(Gui gui, double screenW, double screenH, boolean isWhiteActive) {
        double indicatorSize = screenH * 0.02;
        double padding = screenW * 0.05;

        if (isWhiteActive) {
            // Draw indicator next to white player name
            double indicatorX = padding - indicatorSize * 1.5;
            double indicatorY = screenH * 0.05;

            gui.setColor(0, 200, 0); // Green
            gui.fillCircle(indicatorX, indicatorY, indicatorSize);
            gui.setColor(0, 0, 0);
            gui.drawCircle(indicatorX, indicatorY, indicatorSize);
        } else {
            // Draw indicator next to black player name
            double indicatorX = screenW - padding + indicatorSize * 0.5;
            double indicatorY = screenH * 0.05;

            gui.setColor(0, 200, 0); // Green
            gui.fillCircle(indicatorX, indicatorY, indicatorSize);
            gui.setColor(0, 0, 0);
            gui.drawCircle(indicatorX, indicatorY, indicatorSize);
        }
    }

    private void drawStopButton(Gui gui, double screenW, double screenH) {
        double buttonWidth = screenW * STOP_BUTTON_WIDTH_RATIO;
        double buttonHeight = screenH * STOP_BUTTON_HEIGHT_RATIO;
        double buttonX = screenW - buttonWidth - 20;
        double buttonY = screenH - buttonHeight - 20;

        stopButton = new Rect(buttonX, buttonY, buttonWidth, buttonHeight);

        boolean hover = stopButton.contains(gui.getMouseX(), gui.getMouseY());

        // Draw button background
        if (hover) {
            gui.setColor(220, 100, 100); // Light red when hovered
        } else {
            gui.setColor(200, 80, 80); // Red color
        }
        gui.fillRect(stopButton.x, stopButton.y, stopButton.w, stopButton.h);

        // Draw button border
        gui.setColor(0, 0, 0);
        gui.setStrokeWidth(2);
        gui.drawRect(stopButton.x, stopButton.y, stopButton.w, stopButton.h);

        // Draw button text
        gui.setFontSize((int) (buttonHeight * 0.4));
        String text = "Stop Game";
        // Center text in button (estimate position)
        double estimatedCharWidth = buttonHeight * 0.4 * 0.6;
        double estimatedTextWidth = text.length() * estimatedCharWidth;
        double textX = stopButton.x + (stopButton.w - estimatedTextWidth) / 2;
        double textY = stopButton.y + stopButton.h * 0.7;

        gui.setColor(255, 255, 255); // White text
        gui.drawString(text, textX, textY);
    }

    private void checkGameEnd() {
        NodeValue[][] board = controller.getState().getBoard();

        int whitePieces = countPieces(board, WHITE);
        int blackPieces = countPieces(board, BLACK);

        // Game ends when a player has less than 3 pieces (after placement phase)
        if (controller.getGamePhase() != GamePhase.PLACE) {
            if (whitePieces < 3) {
                handleGameEnd(blackPlayer);
            } else if (blackPieces < 3) {
                handleGameEnd(whitePlayer);
            }
        }
    }

    private int countPieces(NodeValue[][] board, NodeValue color) {
        int count = 0;
        for (int layer = 0; layer < 3; layer++) {
            for (int pos = 0; pos < 8; pos++) {
                if (board[layer][pos] == color) {
                    count++;
                }
            }
        }
        return count;
    }

    private void handleGameEnd(BasePlayer winner) {
        gameEnded = true;

        // Increment winner's games won
        winner.setGamesWon(winner.getGamesWon() + 1);

        // Update the player in the main list if they exist there
        for (BasePlayer p : allPlayers) {
            if (p.getPlayerName().equals(winner.getPlayerName())) {
                p.setGamesWon(winner.getGamesWon());
                break;
            }
        }

        System.out.println("Game Over! Winner: " + winner.getPlayerName());

        // Draw winner announcement
        drawWinnerAnnouncement();

        // Return to leaderboard after a short delay
        new Thread(() -> {
            try {
                Thread.sleep(2000); // 2 second delay to show final board
                returnToLeaderboard();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void drawWinnerAnnouncement() {
        double screenW = gui.getWidth();
        double screenH = gui.getHeight();

        // Draw semi-transparent overlay
        gui.setColor(0, 0, 0);
        gui.fillRect(0, 0, screenW, screenH);

        // Draw winner text
        String winnerName;
        if (countPieces(controller.getState().getBoard(), WHITE) < 3) {
            winnerName = blackPlayer.getPlayerName();
        } else {
            winnerName = whitePlayer.getPlayerName();
        }

        String winnerText = "Winner: " + winnerName + "!";

        gui.setFontSize((int) (screenH * 0.05));
        gui.setColor(255, 255, 0); // Yellow text

        // Estimate center position for winner text
        double estimatedCharWidth = screenH * 0.05 * 0.6;
        double estimatedTextWidth = winnerText.length() * estimatedCharWidth;
        gui.drawString(winnerText, screenW/2 - estimatedTextWidth/2, screenH/2);

        String returnText = "Returning to leaderboard...";
        gui.setFontSize((int) (screenH * 0.02));

        // Estimate center position for return text
        estimatedCharWidth = screenH * 0.02 * 0.6;
        estimatedTextWidth = returnText.length() * estimatedCharWidth;
        gui.drawString(returnText, screenW/2 - estimatedTextWidth/2, screenH/2 + screenH * 0.06);
    }

    private void returnToLeaderboard() {
        gui.removeComponent(this);
        gui.addComponent(new Leaderboard(allPlayers));
    }

    private void drawLayer(Gui gui, int layer, double border, double usableSpace,
                           double nodeSize, double pieceSize) {
        double layerFraction = (2 - layer) / 2.0;
        double sideLength = usableSpace * (0.5 + layerFraction * 0.5);

        double x0 = border + (usableSpace - sideLength) / 2;
        double y0 = border + (usableSpace - sideLength) / 2;

        // Draw the square outline
        gui.setColor(0, 0, 0);
        gui.drawRect(x0, y0, sideLength, sideLength);

        // Draw connecting lines to the next inner layer (but not for the innermost layer)
        if (layer < 2) {
            drawConnectingLines(gui, x0, y0, sideLength, layer, usableSpace);
        }

        // Draw nodes and pieces
        drawNodesAndPieces(gui, layer, x0, y0, sideLength, nodeSize, pieceSize);
    }

    private void drawConnectingLines(Gui gui, double x0, double y0, double side,
                                     int layer, double usableSpace) {
        // Calculate the next inner layer's size to know where to stop
        double nextLayerFraction = (2 - (layer + 1)) / 2.0;
        double nextSide = usableSpace * (0.5 + nextLayerFraction * 0.5);
        double gap = (side - nextSide) / 2;

        // Top line (from top-center down)
        gui.drawLine(x0 + side * 0.5, y0,
                x0 + side * 0.5, y0 + gap);

        // Right line (from right-center left)
        gui.drawLine(x0 + side, y0 + side * 0.5,
                x0 + side - gap, y0 + side * 0.5);

        // Bottom line (from bottom-center up)
        gui.drawLine(x0 + side * 0.5, y0 + side,
                x0 + side * 0.5, y0 + side - gap);

        // Left line (from left-center right)
        gui.drawLine(x0, y0 + side * 0.5,
                x0 + gap, y0 + side * 0.5);
    }

    private void drawNodesAndPieces(Gui gui, int layer, double x0, double y0,
                                    double side, double nodeSize, double pieceSize) {
        for (int position = 0; position < 8; position++) {
            double cx = x0 + side * NODE_X[position];
            double cy = y0 + side * NODE_Y[position];

            NodeValue value = controller.getState().getBoard()[layer][position];

            if (value == WHITE) {
                drawWhitePiece(gui, cx, cy, pieceSize);
            } else if (value == BLACK) {
                drawBlackPiece(gui, cx, cy, pieceSize);
            } else {
                drawEmptyNode(gui, cx, cy, nodeSize);
            }
        }
    }

    private void drawWhitePiece(Gui gui, double cx, double cy, double pieceSize) {
        gui.setColor(255, 255, 255);
        gui.fillCircle(cx, cy, pieceSize);
        gui.setColor(0, 0, 0);
        gui.drawCircle(cx, cy, pieceSize);
    }

    private void drawBlackPiece(Gui gui, double cx, double cy, double pieceSize) {
        gui.setColor(0, 0, 0);
        gui.fillCircle(cx, cy, pieceSize);
        gui.setColor(255, 255, 255);
        gui.drawCircle(cx, cy, pieceSize * 0.6);
    }

    private void drawEmptyNode(Gui gui, double cx, double cy, double nodeSize) {
        gui.setColor(0, 0, 0);
        gui.fillCircle(cx, cy, nodeSize);
    }

    @Override
    public Shape getInteractiveArea(Gui gui) {
        return new Rectangle(0, 0, gui.getWidth(), gui.getHeight());
    }

    @Override
    public void onLeftClick(double x, double y) {
        // Check if stop button was clicked
        if (stopButton != null && stopButton.contains(x, y)) {
            returnToLeaderboard();
            return;
        }

        // Ignore clicks if game hasn't started yet (2 second countdown)
        if (!gameStarted) {
            return;
        }

        if (gameEnded) return; // Ignore clicks after game ends

        int position = positionFromCoordinates(x, y);

        if (position == -1) {
            return;
        }

        // Store the current board state before attempting move
        NodeValue[][] boardBefore = deepCopyBoard(controller.getState().getBoard());
        boolean wasAwaitingRemove = controller.isAwaitingRemove();

        if (controller.isAwaitingRemove()) {
            handleRemovePhase(position);
        } else if (controller.getGamePhase() == GamePhase.PLACE) {
            handlePlacePhase(position);
        } else {
            handleMovePhase(position);
        }

        // Check if the move was valid
        NodeValue[][] boardAfter = controller.getState().getBoard();
        boolean boardChanged = !boardsAreEqual(boardBefore, boardAfter);
        boolean removeStateChanged = (controller.isAwaitingRemove() != wasAwaitingRemove);

        // For move phase, also check if we're just selecting a piece (not a move attempt)
        boolean isJustSelecting = (controller.getGamePhase() != GamePhase.PLACE &&
                controller.getGamePhase() != GamePhase.JUMP &&
                selected != null && !boardChanged);

        if (!boardChanged && !removeStateChanged && !isJustSelecting) {
            // The move was invalid - show error message
            invalidMoveMessage = "Invalid move! Try again.";
            invalidMoveTime = System.currentTimeMillis();
        } else {
            // Valid move or piece selection - clear any previous error
            invalidMoveMessage = "";
        }

        gui.refresh();
    }

    // Helper method to create a deep copy of the board
    private NodeValue[][] deepCopyBoard(NodeValue[][] original) {
        if (original == null) return null;

        NodeValue[][] copy = new NodeValue[original.length][];
        for (int i = 0; i < original.length; i++) {
            copy[i] = Arrays.copyOf(original[i], original[i].length);
        }
        return copy;
    }

    // Helper method to compare two boards
    private boolean boardsAreEqual(NodeValue[][] board1, NodeValue[][] board2) {
        if (board1 == null || board2 == null) return false;
        if (board1.length != board2.length) return false;

        for (int layer = 0; layer < board1.length; layer++) {
            if (board1[layer].length != board2[layer].length) return false;
            for (int pos = 0; pos < board1[layer].length; pos++) {
                if (board1[layer][pos] != board2[layer][pos]) {
                    return false;
                }
            }
        }
        return true;
    }

    private void handleRemovePhase(int position) {
        // Try to make the move
        controller.handleUserMove(new Move(position, -1));
    }

    private void handlePlacePhase(int position) {
        // Try to make the move
        controller.handleUserMove(new Move(-1, position));
    }

    private void handleMovePhase(int position) {
        if (selected == null) {
            // Select a piece to move
            // Check if the clicked position contains a piece of the current player
            NodeValue[][] board = controller.getState().getBoard();
            int layer = position / 8;
            int pos = position % 8;
            NodeValue piece = board[layer][pos];

            boolean isWhiteTurn = controller.isWhiteToMove();
            boolean isValidSelection = false;

            if (isWhiteTurn && piece == WHITE) {
                isValidSelection = true;
            } else if (!isWhiteTurn && piece == BLACK) {
                isValidSelection = true;
            }

            if (isValidSelection) {
                selected = position;
                invalidMoveMessage = ""; // Clear any previous error
            } else {
                // Clicked on empty space or opponent's piece
                invalidMoveMessage = "Select your own piece to move";
                invalidMoveTime = System.currentTimeMillis();
            }
        } else {
            // Try to move the selected piece to the new position
            controller.handleUserMove(new Move(selected, position));
            selected = null;
        }
    }

    @Override
    public void onRightClick(double x, double y) {
        // Not implemented
    }

    private int positionFromCoordinates(double x, double y) {
        double size = Math.min(gui.getWidth(), gui.getHeight());
        double border = size * BORDER_RATIO;
        double usableSpace = size - 2 * border;
        double clickRadius = usableSpace * PIECE_SIZE_RATIO;

        for (int layer = 0; layer < 3; layer++) {
            double layerFraction = (2 - layer) / 2.0;
            double sideLength = usableSpace * (0.5 + layerFraction * 0.5);

            double x0 = border + (usableSpace - sideLength) / 2;
            double y0 = border + (usableSpace - sideLength) / 2;

            for (int position = 0; position < 8; position++) {
                double cx = x0 + sideLength * NODE_X[position];
                double cy = y0 + sideLength * NODE_Y[position];

                double dx = x - cx;
                double dy = y - cy;

                if (dx * dx + dy * dy <= clickRadius * clickRadius) {
                    return layer * 8 + position;
                }
            }
        }

        return -1;
    }
}