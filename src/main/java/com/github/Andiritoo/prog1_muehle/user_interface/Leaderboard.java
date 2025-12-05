package com.github.Andiritoo.prog1_muehle.user_interface;

import ch.trick17.gui.Gui;
import ch.trick17.gui.component.Drawable;
import com.github.Andiritoo.prog1_muehle.botPlayer.BotPlayer;
import com.github.Andiritoo.prog1_muehle.common.NodeValue;
import com.github.Andiritoo.prog1_muehle.humanPlayer.HumanPlayer;
import com.github.Andiritoo.prog1_muehle.llmPlayer.AIPlayer;
import com.github.Andiritoo.prog1_muehle.player.BasePlayer;

import java.util.Comparator;
import java.util.List;

public class Leaderboard implements Drawable {

    private static final int MAX_NAME_LENGTH = 20;

    private final List<BasePlayer> players;

    // Name input fields
    private Rect nameField1;
    private Rect nameField2;
    private boolean nameField1Focused = false;
    private boolean nameField2Focused = false;
    private String playerName1 = "";
    private String playerName2 = "";

    // Buttons
    private Rect btnHuman;
    private Rect btnBot;
    private Rect btnAI;
    private Rect btnStart;

    // Game mode tracking
    private enum GameMode {
        NONE, HUMAN_VS_HUMAN, HUMAN_VS_BOT, HUMAN_VS_AI
    }
    private GameMode selectedMode = GameMode.NONE;

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

    public Leaderboard(List<BasePlayer> players) {
        players.sort(Comparator.comparingInt(BasePlayer::getGamesWon).reversed());
        this.players = players;
    }

    @Override
    public void draw(Gui gui) {
        double screenW = gui.getWidth();
        double screenH = gui.getHeight();

        drawLeaderboardPanel(gui, screenW, screenH);

        if (selectedMode == GameMode.NONE) {
            // Show only game mode buttons
            drawGameModeButtons(gui, screenW, screenH);
        } else {
            // Show name input fields and start button
            drawNameInputFields(gui, screenW, screenH);
            drawStartButton(gui, screenW, screenH);
        }

        handleClicks(gui);
        handleTyping(gui);
    }

    // ======================================================================
    // DRAW LEADERBOARD PANEL
    // ======================================================================
    private void drawLeaderboardPanel(Gui gui, double screenW, double screenH) {
        double boardW = screenW * 0.7;
        double boardH = screenH * 0.45;
        double x = screenW * 0.15;
        double y = screenH * 0.05;

        // Background
        gui.setColor(230, 230, 230);
        gui.fillRect(x, y, boardW, boardH);

        // Title
        gui.setColor(0, 0, 0);
        gui.setFontSize((int) (boardH * 0.12));
        gui.drawString("Leaderboard", x + boardW / 3, y + boardH * 0.18);

        // Table rows
        drawLeaderboardRows(gui, x, y, boardW, boardH);
    }

    private void drawLeaderboardRows(Gui gui, double x, double y, double boardW, double boardH) {
        double rowY = y + boardH * 0.28;
        double rowHeight = (boardH * 0.55) / Math.max(players.size(), 1);

        gui.setFontSize((int) (rowHeight * 0.45));

        int index = 0;
        for (BasePlayer p : players) {
            // Alternating row background
            gui.setColor(index % 2 == 0 ? 255 : 245,
                    index % 2 == 0 ? 255 : 245,
                    index % 2 == 0 ? 255 : 245);
            gui.fillRect(x + boardW * 0.05, rowY, boardW * 0.9, rowHeight);

            // Text
            gui.setColor(0, 0, 0);
            gui.drawString(p.getPlayerName(), x + boardW * 0.1, rowY + rowHeight * 0.7);
            gui.drawString(String.valueOf(p.getGamesWon()), x + boardW * 0.75, rowY + rowHeight * 0.7);

            rowY += rowHeight;
            index++;
        }
    }

    // ======================================================================
    // DRAW NAME INPUT FIELDS
    // ======================================================================
    private void drawNameInputFields(Gui gui, double screenW, double screenH) {
        double fieldW = screenW * 0.4;
        double fieldH = screenH * 0.07;
        double fieldX = screenW * 0.3;
        double fieldY = screenH * 0.52;

        if (selectedMode == GameMode.HUMAN_VS_HUMAN) {
            // Two fields side by side for human vs human
            double gap = screenW * 0.02;
            double smallFieldW = (fieldW - gap) / 2;

            nameField1 = new Rect(fieldX, fieldY, smallFieldW, fieldH);
            nameField2 = new Rect(fieldX + smallFieldW + gap, fieldY, smallFieldW, fieldH);

            drawNameField(gui, nameField1, playerName1, nameField1Focused, "Player 1");
            drawNameField(gui, nameField2, playerName2, nameField2Focused, "Player 2");
        } else if (selectedMode == GameMode.HUMAN_VS_BOT || selectedMode == GameMode.HUMAN_VS_AI) {
            // Single field for bot/AI modes
            nameField1 = new Rect(fieldX, fieldY, fieldW, fieldH);
            nameField2 = null;

            String placeholder = "Your Name";
            drawNameField(gui, nameField1, playerName1, nameField1Focused, placeholder);
        }
    }

    private void drawNameField(Gui gui, Rect r, String text, boolean focused, String placeholder) {
        boolean hover = r.contains(gui.getMouseX(), gui.getMouseY());

        // Background color
        if (focused) {
            gui.setColor(255, 255, 255);
        } else if (hover) {
            gui.setColor(240, 240, 240);
        } else {
            gui.setColor(220, 220, 220);
        }
        gui.fillRect(r.x, r.y, r.w, r.h);

        // Border
        gui.setColor(0, 0, 0);
        gui.setStrokeWidth(3);
        gui.drawRect(r.x, r.y, r.w, r.h);

        // Text or placeholder
        gui.setFontSize((int)(r.h * 0.4));
        if (text.isEmpty() && !focused) {
            gui.setColor(150, 150, 150);
            gui.drawString(placeholder, r.x + r.w * 0.03, r.y + r.h * 0.7);
        } else {
            gui.setColor(0, 0, 0);
            gui.drawString(text, r.x + r.w * 0.03, r.y + r.h * 0.7);
        }
    }

    // ======================================================================
    // DRAW GAME MODE BUTTONS (only shown when no mode is selected)
    // ======================================================================
    private void drawGameModeButtons(Gui gui, double screenW, double screenH) {
        double buttonAreaY = screenH * 0.55;
        double buttonWidth = screenW * 0.25;
        double buttonHeight = screenH * 0.15;
        double gap = screenW * 0.05;

        double b1x = screenW * 0.1;
        double b2x = b1x + buttonWidth + gap;
        double b3x = b2x + buttonWidth + gap;
        double by = buttonAreaY;

        btnHuman = new Rect(b1x, by, buttonWidth, buttonHeight);
        btnBot = new Rect(b2x, by, buttonWidth, buttonHeight);
        btnAI = new Rect(b3x, by, buttonWidth, buttonHeight);

        drawButton(gui, btnHuman, "Vs Human");
        drawButton(gui, btnBot, "Vs Bot");
        drawButton(gui, btnAI, "Vs AI");
    }

    // ======================================================================
    // DRAW START BUTTON (only shown when a mode is selected)
    // ======================================================================
    private void drawStartButton(Gui gui, double screenW, double screenH) {
        double buttonWidth = screenW * 0.25;
        double buttonHeight = screenH * 0.15;
        double buttonX = screenW * 0.5 - buttonWidth / 2;
        double buttonY = screenH * 0.65;

        btnStart = new Rect(buttonX, buttonY, buttonWidth, buttonHeight);

        boolean hover = btnStart.contains(gui.getMouseX(), gui.getMouseY());

        // Draw button
        gui.setColor(hover ? 180 : 210, 220, hover ? 180 : 210);
        gui.fillRect(btnStart.x, btnStart.y, btnStart.w, btnStart.h);

        gui.setColor(0, 0, 0);
        gui.setStrokeWidth(3);
        gui.drawRect(btnStart.x, btnStart.y, btnStart.w, btnStart.h);

        gui.setFontSize((int) (btnStart.h * 0.3));
        gui.drawString("Start Game", btnStart.x + btnStart.w / 10, btnStart.y + btnStart.h * 0.65);
    }

    private void drawButton(Gui gui, Rect r, String text) {
        boolean hover = r.contains(gui.getMouseX(), gui.getMouseY());

        if (hover) {
            gui.setColor(180, 180, 180);
        } else {
            gui.setColor(210, 210, 210);
        }
        gui.fillRect(r.x, r.y, r.w, r.h);

        gui.setColor(0, 0, 0);
        gui.setStrokeWidth(3);
        gui.drawRect(r.x, r.y, r.w, r.h);

        gui.setFontSize((int) (r.h * 0.3));
        gui.drawString(text, r.x + r.w / 10, r.y + r.h * 0.65);
    }

    // ======================================================================
    // CLICK HANDLING
    // ======================================================================
    private void handleClicks(Gui gui) {
        if (!gui.isLeftMouseButtonPressed()) return;

        double mx = gui.getMouseX();
        double my = gui.getMouseY();

        if (selectedMode == GameMode.NONE) {
            // Handle game mode selection
            handleGameModeSelection(gui, mx, my);
        } else {
            // Handle name field clicks
            handleNameFieldClicks(mx, my);

            // Handle start button click
            if (btnStart != null && btnStart.contains(mx, my)) {
                startGame(gui);
            }
        }
    }

    private void handleGameModeSelection(Gui gui, double mx, double my) {
        if (btnHuman.contains(mx, my)) {
            selectedMode = GameMode.HUMAN_VS_HUMAN;
            gui.refresh();
        } else if (btnBot.contains(mx, my)) {
            selectedMode = GameMode.HUMAN_VS_BOT;
            gui.refresh();
        } else if (btnAI.contains(mx, my)) {
            selectedMode = GameMode.HUMAN_VS_AI;
            gui.refresh();
        }
    }

    private void handleNameFieldClicks(double mx, double my) {
        // Reset focus
        nameField1Focused = false;
        nameField2Focused = false;

        // Check if clicked on name field 1
        if (nameField1 != null && nameField1.contains(mx, my)) {
            nameField1Focused = true;
        }

        // Check if clicked on name field 2 (only exists in human vs human mode)
        if (nameField2 != null && nameField2.contains(mx, my)) {
            nameField2Focused = true;
        }
    }

    private void startGame(Gui gui) {
        if (selectedMode == GameMode.HUMAN_VS_HUMAN) {
            startHumanVsHuman(gui);
        } else if (selectedMode == GameMode.HUMAN_VS_BOT) {
            startHumanVsBot(gui);
        } else if (selectedMode == GameMode.HUMAN_VS_AI) {
            startHumanVsAI(gui);
        }
    }

    private void startHumanVsHuman(Gui gui) {
        // Validate names
        if (playerName1.trim().isEmpty()) playerName1 = "Player 1";
        if (playerName2.trim().isEmpty()) playerName2 = "Player 2";

        HumanPlayer white = new HumanPlayer();
        white.setPlayerName(playerName1);

        HumanPlayer black = new HumanPlayer();
        black.setPlayerName(playerName2);

        System.out.println("Start game: " + playerName1 + " vs " + playerName2);
        gui.removeComponent(this);
        UserInterface.startGame(white, black, players);
    }

    private void startHumanVsBot(Gui gui) {
        // Only use first name for bot games
        if (playerName1.trim().isEmpty()) playerName1 = "Player";

        HumanPlayer white = new HumanPlayer();
        white.setPlayerName(playerName1);

        BotPlayer black = new BotPlayer();
        black.setPlayerName("Bot");

        System.out.println("Start game: " + playerName1 + " vs Bot");
        gui.removeComponent(this);
        UserInterface.startGame(white, black, players);
    }

    private void startHumanVsAI(Gui gui) {
        // Only use first name for AI games
        if (playerName1.trim().isEmpty()) playerName1 = "Player";

        HumanPlayer white = new HumanPlayer();
        white.setPlayerName(playerName1);

        AIPlayer black = new AIPlayer(NodeValue.WHITE);
        black.setPlayerName("AI");

        System.out.println("Start game: " + playerName1 + " vs AI");
        gui.removeComponent(this);
        UserInterface.startGame(white, black, players);
    }

    // ======================================================================
    // TYPING HANDLING
    // ======================================================================
    private void handleTyping(Gui gui) {
        if (selectedMode == GameMode.NONE) return;

        if (!nameField1Focused && !nameField2Focused) return;

        List<String> keys = gui.getTypedKeys();
        if (keys == null || keys.isEmpty()) return;

        for (String key : keys) {
            if (key == null) continue;
            processKey(key);
        }
    }

    private void processKey(String key) {
        String upper = key.toUpperCase();

        // Handle backspace
        if (isBackspaceKey(upper)) {
            handleBackspace();
            return;
        }

        // Handle enter
        if (upper.equals("ENTER") || upper.equals("RETURN")) {
            handleEnter();
            return;
        }

        // Handle space
        if (upper.equals("SPACE")) {
            addCharacter(' ');
            return;
        }

        // Handle regular characters
        if (key.length() == 1) {
            addCharacter(key.charAt(0));
        }
    }

    private boolean isBackspaceKey(String key) {
        return key.equals("BACKSPACE") || key.equals("BACK_SPACE") ||
                key.equals("\b") || key.equals("DELETE");
    }

    private void handleBackspace() {
        if (nameField1Focused && !playerName1.isEmpty()) {
            playerName1 = playerName1.substring(0, playerName1.length() - 1);
        } else if (nameField2Focused && !playerName2.isEmpty()) {
            playerName2 = playerName2.substring(0, playerName2.length() - 1);
        }
    }

    private void handleEnter() {
        if (nameField1Focused && selectedMode == GameMode.HUMAN_VS_HUMAN && nameField2 != null) {
            // Move focus to second field in human vs human mode
            nameField1Focused = false;
            nameField2Focused = true;
        } else {
            // Unfocus all fields
            nameField1Focused = false;
            nameField2Focused = false;
        }
    }

    private void addCharacter(char c) {
        if (nameField1Focused && playerName1.length() < MAX_NAME_LENGTH) {
            playerName1 += c;
        } else if (nameField2Focused && playerName2.length() < MAX_NAME_LENGTH) {
            playerName2 += c;
        }
    }
}