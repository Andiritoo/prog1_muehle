package com.github.Andiritoo.prog1_muehle.user_interface;

import ch.trick17.gui.Gui;
import ch.trick17.gui.component.Drawable;
import com.github.Andiritoo.prog1_muehle.botPlayer.BotPlayer;
import com.github.Andiritoo.prog1_muehle.common.Move;
import com.github.Andiritoo.prog1_muehle.common.NodeValue;
import com.github.Andiritoo.prog1_muehle.game.GameState;
import com.github.Andiritoo.prog1_muehle.humanPlayer.HumanPlayer;
import com.github.Andiritoo.prog1_muehle.llmPlayer.AIPlayer;
import com.github.Andiritoo.prog1_muehle.player.BasePlayer;
import com.github.Andiritoo.prog1_muehle.player.Player;

import java.util.Comparator;
import java.util.List;

public class Leaderboard implements Drawable {

    private final List<BasePlayer> players;

    // Name input
    private Rect nameField;
    private boolean nameFieldFocused = false;
    private String playerName = "";

    // Buttons
    private Rect btnHuman;
    private Rect btnBot;
    private Rect btnAI;

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

        // ======================================================
        // NAME INPUT FIELD
        // ======================================================

        double fieldW = screenW * 0.4;
        double fieldH = screenH * 0.07;
        double fieldX = screenW * 0.3;
        double fieldY = screenH * 0.52;

        nameField = new Rect(fieldX, fieldY, fieldW, fieldH);

        drawNameField(gui, nameField, playerName);


        // ======================================================
        // BIG BUTTONS
        // ======================================================

        double buttonAreaY = screenH * 0.62;

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

        handleClicks(gui);
        handleTyping(gui);
    }


    // ======================================================================
    // DRAW NAME FIELD
    // ======================================================================
    private void drawNameField(Gui gui, Rect r, String text) {

        boolean hover = r.contains(gui.getMouseX(), gui.getMouseY());

        if (nameFieldFocused) gui.setColor(255, 255, 255);
        else if (hover)       gui.setColor(240, 240, 240);
        else                 gui.setColor(220, 220, 220);

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
    // CLICK HANDLING (FIELD + BUTTONS)
    // ======================================================================
    private void handleClicks(Gui gui) {
        if (!gui.isLeftMouseButtonPressed()) return;

        double mx = gui.getMouseX();
        double my = gui.getMouseY();

        // Click inside name field → focus
        if (nameField.contains(mx, my)) {
            nameFieldFocused = true;
            return;
        } else {
            nameFieldFocused = false;
        }

        var white = new HumanPlayer();
        white.setPlayerName(playerName);

        // Buttons
        if (btnHuman.contains(mx, my)) {
            System.out.println("Start game (human vs human) for: " + playerName);
            var black = new HumanPlayer();
            black.setPlayerName("Player 2");

            UserInterface.startGame(white, black);
        }
        else if (btnBot.contains(mx, my)) {
            System.out.println("Start game (human vs bot) for: " + playerName);
            var black = new BotPlayer();
            black.setPlayerName("Bot");

            UserInterface.startGame(white, black);
        }
        else if (btnAI.contains(mx, my)) {
            System.out.println("Start game (human vs AI) for: " + playerName);
            var black = new AIPlayer(NodeValue.WHITE);
            black.setPlayerName("AI");

            UserInterface.startGame(white, black);
        }
    }


    // ======================================================================
    // TYPING INTO THE TEXTFIELD
    // ======================================================================
    private void handleTyping(Gui gui) {
        if (!nameFieldFocused) return;

        List<String> keys = gui.getTypedKeys();
        if (keys == null || keys.isEmpty()) return;

        for (String key : keys) {
            if (key == null) continue;

            String upper = key.toUpperCase();

            // Backspace variants (covers BACKSPACE, BACK_SPACE, \b, DELETE)
            if (upper.equals("BACKSPACE") || upper.equals("BACK_SPACE") || upper.equals("\b") || upper.equals("DELETE")) {
                if (!playerName.isEmpty()) {
                    playerName = playerName.substring(0, playerName.length() - 1);
                }
                continue;
            }

            // Enter / Return: unfocus the field (optional)
            if (upper.equals("ENTER") || upper.equals("RETURN")) {
                nameFieldFocused = false;
                continue;
            }

            // Space key
            if (upper.equals("SPACE")) {
                if (playerName.length() < 20) playerName += ' ';
                continue;
            }

            // Printable single-character keys — preserve original case
            if (key.length() == 1 && playerName.length() < 20) {
                playerName += key;
            }
        }
    }

}
