package com.github.Andiritoo.prog1_muehle.user_interface;

import ch.trick17.gui.Gui;
import ch.trick17.gui.component.Drawable;
import com.github.Andiritoo.prog1_muehle.botPlayer.BotPlayer;
import com.github.Andiritoo.prog1_muehle.common.NodeValue;
import com.github.Andiritoo.prog1_muehle.game.GameState;
import com.github.Andiritoo.prog1_muehle.humanPlayer.HumanPlayer;
import com.github.Andiritoo.prog1_muehle.llmPlayer.AIPlayer;
import com.github.Andiritoo.prog1_muehle.player.BasePlayer;
import com.github.Andiritoo.prog1_muehle.repository.PlayerRepository;

import java.util.Comparator;
import java.util.List;

public class Leaderboard implements Drawable {

    private final List<BasePlayer> players;

    // UI State
    private enum UIState {
        MODE_SELECTION,
        NAME_INPUT
    }

    private UIState currentState = UIState.MODE_SELECTION;
    private String selectedMode = ""; // "HUMAN", "BOT", or "AI"

    // Name input
    private Rect nameField1;
    private Rect nameField2;
    private boolean nameField1Focused = false;
    private boolean nameField2Focused = false;
    private String player1Name = "";
    private String player2Name = "";

    // Buttons
    private Rect btnHuman;
    private Rect btnBot;
    private Rect btnAI;
    private Rect btnStartGame;

    // Simple rectangle helper type
    private static class Rect {
        double x, y, w, h;
        Rect(double x, double y, double w, double h) {
            this.x = x; this.y = y; this.w = w; this.h = h;
        }
        boolean contains(double px, double py) {
            return px >= x && px <= x + w &&
                    py >= y && py <= y + h;
        }
    }

    public Leaderboard(List<BasePlayer> players) {
        players.sort(Comparator.comparingInt(BasePlayer::getGamesWon).reversed());
        this.players = players;
    }


    // ======================================================================
    // DRAW
    // ======================================================================
    @Override
    public void draw(Gui gui) {

        double screenW = gui.getWidth();
        double screenH = gui.getHeight();

        // Leaderboard panel (top half)
        double boardW = screenW * 0.7;
        double boardH = screenH * 0.45;
        double x = screenW * 0.15;
        double y = screenH * 0.05;

        // === Leaderboard Background ===
        gui.setColor(230, 230, 230);
        gui.fillRect(x, y, boardW, boardH);

        gui.setColor(0, 0, 0);
        gui.setFontSize((int) (boardH * 0.12));
        gui.drawString("Leaderboard",
                x + boardW / 3,
                y + boardH * 0.18);

        // === Table Rows ===
        double rowY = y + boardH * 0.28;
        double rowHeight = (boardH * 0.55) / Math.max(players.size(), 1);

        gui.setFontSize((int) (rowHeight * 0.45));

        int index = 0;
        for (BasePlayer p : players) {

            // alternating row background
            if (index % 2 == 0) gui.setColor(255, 255, 255);
            else gui.setColor(245, 245, 245);

            gui.fillRect(x + boardW * 0.05,
                    rowY,
                    boardW * 0.9,
                    rowHeight);

            gui.setColor(0, 0, 0);

            gui.drawString(p.getPlayerName(),
                    x + boardW * 0.1,
                    rowY + rowHeight * 0.7);

            gui.drawString(String.valueOf(p.getGamesWon()),
                    x + boardW * 0.75,
                    rowY + rowHeight * 0.7);

            rowY += rowHeight;
            index++;
        }

        // Draw based on current state
        if (currentState == UIState.MODE_SELECTION) {
            drawModeSelection(gui, screenW, screenH);
        } else {
            drawNameInput(gui, screenW, screenH);
        }

        handleClicks(gui);
        if (currentState == UIState.NAME_INPUT) {
            handleTyping(gui);
        }
    }


    // ======================================================================
    // DRAW MODE SELECTION (INITIAL SCREEN)
    // ======================================================================
    private void drawModeSelection(Gui gui, double screenW, double screenH) {
        double buttonAreaY = screenH * 0.58;

        double buttonWidth = screenW * 0.25;
        double buttonHeight = screenH * 0.15;
        double gap = screenW * 0.05;

        double b1x = screenW * 0.1;
        double b2x = b1x + buttonWidth + gap;
        double b3x = b2x + buttonWidth + gap;
        double by  = buttonAreaY;

        btnHuman = new Rect(b1x, by, buttonWidth, buttonHeight);
        btnBot   = new Rect(b2x, by, buttonWidth, buttonHeight);
        btnAI    = new Rect(b3x, by, buttonWidth, buttonHeight);

        drawButton(gui, btnHuman, "Vs Human");
        drawButton(gui, btnBot,   "Vs Bot");
        drawButton(gui, btnAI,    "Vs AI");
    }


    // ======================================================================
    // DRAW NAME INPUT (AFTER MODE SELECTION)
    // ======================================================================
    private void drawNameInput(Gui gui, double screenW, double screenH) {
        double fieldW = screenW * 0.35;
        double fieldH = screenH * 0.06;
        double field1X = screenW * 0.08;
        double field2X = screenW * 0.57;
        double fieldY = screenH * 0.58;

        nameField1 = new Rect(field1X, fieldY, fieldW, fieldH);

        // Draw Player 1 label and field
        gui.setColor(0, 0, 0);
        gui.setFontSize((int)(fieldH * 0.5));
        gui.drawString("Player 1 (White):", field1X, fieldY - fieldH * 0.2);
        drawNameField(gui, nameField1, player1Name, nameField1Focused);

        // Draw Player 2 field only for Human vs Human
        if (selectedMode.equals("HUMAN")) {
            nameField2 = new Rect(field2X, fieldY, fieldW, fieldH);
            gui.setColor(0, 0, 0);
            gui.drawString("Player 2 (Black):", field2X, fieldY - fieldH * 0.2);
            drawNameField(gui, nameField2, player2Name, nameField2Focused);
        }

        // Draw Start Game button
        double buttonWidth = screenW * 0.3;
        double buttonHeight = screenH * 0.1;
        double buttonX = (screenW - buttonWidth) / 2;
        double buttonY = screenH * 0.72;

        btnStartGame = new Rect(buttonX, buttonY, buttonWidth, buttonHeight);
        drawButton(gui, btnStartGame, "Start Game");
    }


    // ======================================================================
    // DRAW NAME FIELD
    // ======================================================================
    private void drawNameField(Gui gui, Rect r, String text, boolean isFocused) {

        boolean hover = r.contains(gui.getMouseX(), gui.getMouseY());

        if (isFocused)   gui.setColor(255, 255, 255);
        else if (hover)  gui.setColor(240, 240, 240);
        else             gui.setColor(220, 220, 220);

        gui.fillRect(r.x, r.y, r.w, r.h);

        gui.setColor(0, 0, 0);
        gui.setStrokeWidth(3);
        gui.drawRect(r.x, r.y, r.w, r.h);

        gui.setFontSize((int)(r.h * 0.4));
        gui.drawString(text,
                r.x + r.w * 0.03,
                r.y + r.h * 0.7);
    }


    // ======================================================================
    // DRAW BUTTON
    // ======================================================================
    private void drawButton(Gui gui, Rect r, String text) {
        double mx = gui.getMouseX();
        double my = gui.getMouseY();

        boolean hover = r.contains(mx, my);

        if (hover) gui.setColor(180, 180, 180);
        else gui.setColor(210, 210, 210);

        gui.fillRect(r.x, r.y, r.w, r.h);

        gui.setColor(0, 0, 0);
        gui.setStrokeWidth(3);
        gui.drawRect(r.x, r.y, r.w, r.h);

        gui.setFontSize((int) (r.h * 0.3));
        gui.drawString(text,
                r.x + r.w / 10,
                r.y + r.h * 0.65);
    }


    // ======================================================================
    // CLICK HANDLING
    // ======================================================================
    private void handleClicks(Gui gui) {
        if (!gui.isLeftMouseButtonPressed()) return;

        double mx = gui.getMouseX();
        double my = gui.getMouseY();

        if (currentState == UIState.MODE_SELECTION) {
            // Handle mode selection buttons
            if (btnHuman.contains(mx, my)) {
                selectedMode = "HUMAN";
                currentState = UIState.NAME_INPUT;
                System.out.println("Selected mode: Human vs Human");
            }
            else if (btnBot.contains(mx, my)) {
                selectedMode = "BOT";
                currentState = UIState.NAME_INPUT;
                System.out.println("Selected mode: Human vs Bot");
            }
            else if (btnAI.contains(mx, my)) {
                selectedMode = "AI";
                currentState = UIState.NAME_INPUT;
                System.out.println("Selected mode: Human vs AI");
            }
        } else {
            // Handle name input screen

            // Click inside name fields → focus
            if (nameField1.contains(mx, my)) {
                nameField1Focused = true;
                nameField2Focused = false;
                return;
            } else if (selectedMode.equals("HUMAN") && nameField2 != null && nameField2.contains(mx, my)) {
                nameField1Focused = false;
                nameField2Focused = true;
                return;
            } else if (btnStartGame.contains(mx, my)) {
                // Start the game
                startSelectedGame(gui);
                return;
            } else {
                nameField1Focused = false;
                nameField2Focused = false;
            }
        }
    }


    // ======================================================================
    // START THE SELECTED GAME
    // ======================================================================
    private void startSelectedGame(Gui gui) {
        HumanPlayer white = PlayerRepository.findOrCreatePlayer(player1Name.isEmpty() ? "Player 1" : player1Name);

        if (selectedMode.equals("HUMAN")) {
            HumanPlayer black = PlayerRepository.findOrCreatePlayer(player2Name.isEmpty() ? "Player 2" : player2Name);
            System.out.println("Start game (human vs human): " + white.getPlayerName() + " vs " + black.getPlayerName());
            gui.removeComponent(this);
            UserInterface.startGame(white, black);
        }
        else if (selectedMode.equals("BOT")) {
            System.out.println("Start game (human vs bot) for: " + white.getPlayerName());
            var black = new BotPlayer();
            black.setPlayerName("Bot");
            gui.removeComponent(this);
            UserInterface.startGame(white, black);
        }
        else if (selectedMode.equals("AI")) {
            System.out.println("Start game (human vs AI) for: " + white.getPlayerName());
            var black = new AIPlayer(NodeValue.BLACK);
            black.setPlayerName("AI");
            gui.removeComponent(this);
            UserInterface.startGame(white, black);
        }
    }


    // ======================================================================
    // TYPING INTO THE TEXTFIELD
    // ======================================================================
    private void handleTyping(Gui gui) {
        if (!nameField1Focused && !nameField2Focused) return;

        List<String> keys = gui.getTypedKeys();
        if (keys == null || keys.isEmpty()) return;

        // Determine which field to update
        boolean isField1 = nameField1Focused;

        for (String key : keys) {
            if (key == null) continue;

            String upper = key.toUpperCase();

            // Backspace variants (covers BACKSPACE, BACK_SPACE, \b, DELETE)
            if (upper.equals("BACKSPACE") || upper.equals("BACK_SPACE") || upper.equals("\b") || upper.equals("DELETE")) {
                if (isField1) {
                    if (!player1Name.isEmpty()) {
                        player1Name = player1Name.substring(0, player1Name.length() - 1);
                    }
                } else {
                    if (!player2Name.isEmpty()) {
                        player2Name = player2Name.substring(0, player2Name.length() - 1);
                    }
                }
                continue;
            }

            // Enter / Return: unfocus the field or switch to next field
            if (upper.equals("ENTER") || upper.equals("RETURN")) {
                if (isField1 && selectedMode.equals("HUMAN")) {
                    nameField1Focused = false;
                    nameField2Focused = true;
                } else {
                    nameField1Focused = false;
                    nameField2Focused = false;
                }
                continue;
            }

            // Space key
            if (upper.equals("SPACE")) {
                if (isField1) {
                    if (player1Name.length() < 20) player1Name += ' ';
                } else {
                    if (player2Name.length() < 20) player2Name += ' ';
                }
                continue;
            }

            // Printable single-character keys — preserve original case
            if (key.length() == 1) {
                if (isField1 && player1Name.length() < 20) {
                    player1Name += key;
                } else if (!isField1 && player2Name.length() < 20) {
                    player2Name += key;
                }
            }
        }
    }

}