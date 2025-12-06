package com.github.Andiritoo.prog1_muehle.user_interface;

import ch.trick17.gui.Gui;
import ch.trick17.gui.component.Clickable;
import ch.trick17.gui.component.Drawable;
import ch.trick17.gui.component.Rectangle;
import ch.trick17.gui.component.Shape;
import com.github.Andiritoo.prog1_muehle.common.NodeValue;
import com.github.Andiritoo.prog1_muehle.game.GameController;
import com.github.Andiritoo.prog1_muehle.game.GamePhase;

import static com.github.Andiritoo.prog1_muehle.common.NodeValue.BLACK;
import static com.github.Andiritoo.prog1_muehle.common.NodeValue.WHITE;

public class GameBoard implements Drawable, Clickable, UserInputProvider {

    private final GameController controller;
    private final Gui gui;
    private final GameCompletionCallback completionCallback;

    // UserInputProvider state
    private Integer clickedPosition = null;
    private Integer selectedPosition = null;
    private boolean gameEndedHandled = false;

    // Countdown state
    private long countdownStartTime = -1;
    private static final long COUNTDOWN_DURATION_MS = 3000; // 3 seconds

    // Error indicator state
    private String errorMessage = null;
    private long errorTimestamp = -1;
    private static final long ERROR_DISPLAY_DURATION_MS = 2000; // 2 seconds

    private static final double[] X = {0, 0.5, 1, 1, 1, 0.5, 0, 0};
    private static final double[] Y = {0, 0, 0, 0.5, 1, 1, 1, 0.5};

    public GameBoard(GameController controller, Gui gui, GameCompletionCallback completionCallback) {
        this.controller = controller;
        this.gui = gui;
        this.completionCallback = completionCallback;
    }

    @Override
    public Integer getClickedPosition() {
        return clickedPosition;
    }

    @Override
    public void clearClickedPosition() {
        clickedPosition = null;
    }

    @Override
    public Integer getSelectedPosition() {
        return selectedPosition;
    }

    @Override
    public void setSelectedPosition(Integer position) {
        selectedPosition = position;
    }

    @Override
    public void draw(Gui gui) {
        // Initialize countdown on first draw
        if (countdownStartTime == -1) {
            countdownStartTime = System.currentTimeMillis();
        }

        // Calculate remaining countdown time
        long elapsed = System.currentTimeMillis() - countdownStartTime;
        long remaining = COUNTDOWN_DURATION_MS - elapsed;
        boolean countdownActive = remaining > 0;

        if (!controller.isGameOver()) {
            // Only execute moves after countdown is finished
            if (!countdownActive) {
                controller.executeCurrentPlayerMove();

                // Check for move errors and display them
                String error = controller.getAndClearLastMoveError();
                if (error != null) {
                    showError(error);
                }
            }
        } else if (!gameEndedHandled) {
            gameEndedHandled = true;
            if (completionCallback != null) {
                completionCallback.onGameComplete(controller.getWinner());
            }
            return;
        }

        // Make the board 70% of the window size
        double size = Math.min(gui.getWidth(), gui.getHeight()) * 0.7;
        double border = size * 0.1;

        // Center the board in the window
        double offsetX = (gui.getWidth() - size) / 2;
        double offsetY = (gui.getHeight() - size) / 2;

        double usable = size - 2 * border;

        double nodeSize = usable * 0.02;
        double pieceSize = usable * 0.04;

        // Draw board structures
        gui.setColor(0, 0, 0);
        gui.setStrokeWidth(size * 0.003);

        // Draw the three squares
        for (int layer = 0; layer < 3; layer++) {
            double layerFraction = (2 - layer) / 2.0;
            double side = usable * (0.5 + layerFraction * 0.5);

            double x0 = offsetX + border + (usable - side) / 2;
            double y0 = offsetY + border + (usable - side) / 2;

            gui.drawRect(x0, y0, side, side);
        }

        // Draw connecting lines between layers
        // These lines connect the middle points of each side across all three layers
        for (int sideIndex = 0; sideIndex < 4; sideIndex++) {
            // Calculate start point (outermost layer, layer 0)
            double layer0Fraction = (2 - 0) / 2.0;
            double side0 = usable * (0.5 + layer0Fraction * 0.5);
            double x0_layer0 = offsetX + border + (usable - side0) / 2;
            double y0_layer0 = offsetY + border + (usable - side0) / 2;

            // Calculate end point (innermost layer, layer 2)
            double layer2Fraction = (2 - 2) / 2.0;
            double side2 = usable * (0.5 + layer2Fraction * 0.5);
            double x0_layer2 = offsetX + border + (usable - side2) / 2;
            double y0_layer2 = offsetY + border + (usable - side2) / 2;

            double startX, startY, endX, endY;

            switch (sideIndex) {
                case 0: // Top
                    startX = x0_layer0 + side0 * 0.5;
                    startY = y0_layer0;
                    endX = x0_layer2 + side2 * 0.5;
                    endY = y0_layer2;
                    break;
                case 1: // Right
                    startX = x0_layer0 + side0;
                    startY = y0_layer0 + side0 * 0.5;
                    endX = x0_layer2 + side2;
                    endY = y0_layer2 + side2 * 0.5;
                    break;
                case 2: // Bottom
                    startX = x0_layer0 + side0 * 0.5;
                    startY = y0_layer0 + side0;
                    endX = x0_layer2 + side2 * 0.5;
                    endY = y0_layer2 + side2;
                    break;
                case 3: // Left
                    startX = x0_layer0;
                    startY = y0_layer0 + side0 * 0.5;
                    endX = x0_layer2;
                    endY = y0_layer2 + side2 * 0.5;
                    break;
                default:
                    continue;
            }

            gui.drawLine(startX, startY, endX, endY);
        }

        // Draw pieces and nodes
        for (int layer = 0; layer < 3; layer++) {
            double layerFraction = (2 - layer) / 2.0;
            double side = usable * (0.5 + layerFraction * 0.5);

            double x0 = offsetX + border + (usable - side) / 2;
            double y0 = offsetY + border + (usable - side) / 2;

            for (int j = 0; j < 8; j++) {
                double cx = x0 + side * X[j];
                double cy = y0 + side * Y[j];

                NodeValue value = controller.getState().getBoard()[layer][j];
                int currentPosition = layer * 8 + j;

                if (value == WHITE) {
                    gui.setColor(255, 255, 255);
                    gui.fillCircle(cx, cy, pieceSize);
                    gui.setColor(0, 0, 0);
                    gui.drawCircle(cx, cy, pieceSize);
                } else if (value == BLACK) {
                    gui.setColor(0, 0, 0);
                    gui.fillCircle(cx, cy, pieceSize);
                    gui.setColor(255, 255, 255);
                    gui.drawCircle(cx, cy, pieceSize * 0.6);
                } else {
                    gui.setColor(0, 0, 0);
                    gui.fillCircle(cx, cy, nodeSize);
                }

                // Draw selection indicator for selected piece
                if (selectedPosition != null && selectedPosition == currentPosition && value != NodeValue.EMPTY) {
                    gui.setColor(0, 200, 0); // Green highlight
                    gui.setStrokeWidth(size * 0.005);
                    gui.drawCircle(cx, cy, pieceSize * 1.5);
                    gui.setStrokeWidth(size * 0.003); // Reset stroke width
                }
            }
        }

        // Draw player info and turn indicators
        if (!countdownActive) {
            drawPlayerInfo();
        }

        // Draw countdown overlay
        if (countdownActive) {
            drawCountdown(remaining);
        }

        // Draw error indicator if active
        drawErrorIndicator();
    }

    private void drawPlayerInfo() {
        double screenW = gui.getWidth();
        double screenH = gui.getHeight();
        double padding = screenW * 0.05;
        double fontSize = screenH * 0.03;

        gui.setFontSize((int) fontSize);

        boolean isRemovePhase = controller.isAwaitingRemove();
        boolean isWhiteTurn = controller.isWhiteToMove();

        // Draw white player info (top left)
        if (isWhiteTurn && !isRemovePhase) {
            gui.setColor(0, 200, 0); // Green for active player
        } else {
            gui.setColor(0, 0, 0); // Black for inactive player
        }
        String whiteText = "White";
        gui.drawString(whiteText, padding, padding + fontSize);

        // Draw black player info (top right)
        if (!isWhiteTurn && !isRemovePhase) {
            gui.setColor(0, 200, 0); // Green for active player
        } else {
            gui.setColor(0, 0, 0); // Black for inactive player
        }
        String blackText = "Black";
        double estimatedCharWidth = fontSize * 0.6;
        double estimatedTextWidth = blackText.length() * estimatedCharWidth;
        gui.drawString(blackText, screenW - padding - estimatedTextWidth, padding + fontSize);

        drawTurnIndicator(isRemovePhase, isWhiteTurn);

        drawPlayerIndicator(isWhiteTurn);
    }

    private void drawTurnIndicator(boolean isRemovePhase, boolean isWhiteTurn) {
        double centerX = gui.getWidth() / 2;
        double topY = gui.getHeight() * 0.05;
        double fontSize = gui.getHeight() * 0.025;

        gui.setFontSize((int) fontSize);

        String turnText;

        if (isRemovePhase) {
            String playerColor = isWhiteTurn ? "White" : "Black";
            turnText = playerColor + " must remove opponent's piece";
            gui.setColor(200, 0, 0); // Red for remove phase
        } else {
            GamePhase phase = controller.getGamePhase();
            if (phase != null) {
                String playerColor = isWhiteTurn ? "White" : "Black";

                turnText = switch (phase) {
                    case PLACE -> playerColor + " - Place a piece";
                    case MOVE -> playerColor + " - Move a piece";
                    case JUMP -> playerColor + " - Jump a piece";
                };
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
    }

    private void drawPlayerIndicator(boolean isWhiteActive) {
        double indicatorSize = gui.getHeight() * 0.02;
        double padding = gui.getWidth() * 0.05;

        if (isWhiteActive) {
            // Draw indicator next to white player name
            double indicatorX = padding - indicatorSize * 1.5;
            double indicatorY = gui.getHeight() * 0.05;

            gui.setColor(0, 200, 0); // Green
            gui.fillCircle(indicatorX, indicatorY, indicatorSize);
            gui.setColor(0, 0, 0);
            gui.drawCircle(indicatorX, indicatorY, indicatorSize);
        } else {
            // Draw indicator next to black player name
            double indicatorX = gui.getWidth() - padding + indicatorSize * 0.5;
            double indicatorY = gui.getHeight() * 0.05;

            gui.setColor(0, 200, 0); // Green
            gui.fillCircle(indicatorX, indicatorY, indicatorSize);
            gui.setColor(0, 0, 0);
            gui.drawCircle(indicatorX, indicatorY, indicatorSize);
        }
    }

    private void drawCountdown(long remaining) {
        int secondsRemaining = (int) Math.ceil(remaining / 1000.0);

        gui.setColor(0, 0, 0);
        gui.fillRect(0, 0, gui.getWidth(), gui.getHeight());

        String countdownText = String.format("Game starts in %d seconds", secondsRemaining);
        gui.setFontSize((int) (gui.getHeight() * 0.05));
        gui.setColor(255, 255, 255);
        double estimatedCharWidth = gui.getHeight() * 0.05 * 0.6;
        double estimatedTextWidth = countdownText.length() * estimatedCharWidth;
        gui.drawString(countdownText, gui.getWidth()/2 - estimatedTextWidth/2, gui.getHeight()/2);
    }

    private void drawErrorIndicator() {
        if (errorMessage == null || errorTimestamp == -1) {
            return;
        }

        long elapsed = System.currentTimeMillis() - errorTimestamp;
        if (elapsed > ERROR_DISPLAY_DURATION_MS) {
            // Error expired, clear it
            errorMessage = null;
            errorTimestamp = -1;
            return;
        }

        // Draw error box at top center
        double screenW = gui.getWidth();
        double screenH = gui.getHeight();
        double fontSize = screenH * 0.025;

        gui.setFontSize((int) fontSize);

        // Calculate text size
        double estimatedCharWidth = fontSize * 0.5;
        double estimatedTextWidth = errorMessage.length() * estimatedCharWidth;

        double boxWidth = estimatedTextWidth + screenW * 0.04;
        double boxHeight = fontSize * 2;
        double boxX = (screenW - boxWidth) / 2;
        double boxY = screenH * 0.08;

        // Draw red background
        gui.setColor(200, 0, 0);
        gui.fillRect(boxX, boxY, boxWidth, boxHeight);

        // Draw darker border
        gui.setColor(150, 0, 0);
        gui.setStrokeWidth(screenW * 0.003);
        gui.drawRect(boxX, boxY, boxWidth, boxHeight);

        // Draw error text
        gui.setColor(255, 255, 255);
        gui.drawString(errorMessage, boxX + screenW * 0.02, boxY + fontSize * 1.3);
    }

    public void showError(String message) {
        this.errorMessage = message;
        this.errorTimestamp = System.currentTimeMillis();
    }

    @Override
    public Shape getInteractiveArea(Gui gui) {
        return new Rectangle(0, 0, gui.getWidth(), gui.getHeight());
    }

    @Override
    public void onLeftClick(double x, double y) {
        int pos = positionFromCoordinates(x, y);
        System.out.println("CLICK at " + x + ", " + y + " → pos: " + pos);

        if (pos == -1) {
            return;
        }

        // Store clicked position - HumanPlayer will process it via move() method
        clickedPosition = pos;
        gui.refresh();
    }



    @Override
    public void onRightClick(double x, double y) {
    }

    private int positionFromCoordinates(double x, double y) {

        // Match the board size calculation from draw()
        double size = Math.min(gui.getWidth(), gui.getHeight()) * 0.7;
        double border = size * 0.1;

        // Center the board in the window
        double offsetX = (gui.getWidth() - size) / 2;
        double offsetY = (gui.getHeight() - size) / 2;

        double usable = size - 2 * border;

        for (int layer = 0; layer < 3; layer++) {

            double layerFraction = (2 - layer) / 2.0;
            double side = usable * (0.5 + layerFraction * 0.5);

            double x0 = offsetX + border + (usable - side) / 2;
            double y0 = offsetY + border + (usable - side) / 2;

            double radius = usable * 0.04;

            for (int j = 0; j < 8; j++) {
                double cx = x0 + side * X[j];
                double cy = y0 + side * Y[j];

                double dx = x - cx;
                double dy = y - cy;

                if (dx * dx + dy * dy <= radius * radius) {
                    return layer * 8 + j;
                }
            }
        }

        return -1;
    }
}