package com.github.Andiritoo.prog1_muehle.user_interface;

import ch.trick17.gui.Gui;
import ch.trick17.gui.component.Drawable;
import com.github.Andiritoo.prog1_muehle.player.BasePlayer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Leaderboard implements Drawable {

    private List<BasePlayer> entries;
    private double width;
    private double height;

    public Leaderboard(List<BasePlayer> entries, double width, double height) {
        this.entries = entries;
        this.width = width;
        this.height = height;
    }

    @Override
    public void draw(Gui gui) {
        // === Layout Rules ===
        double padding = width * 0.05;
        double titleHeight = height * 0.04;
        double rowHeight = titleHeight * 0.7;
        double x = padding;
        double y = padding;

        // === Draw Background Panel ===
        gui.setColor(230, 230, 230);
        gui.fillRect(x, y, width - padding * 2, height - padding * 2);

        // === Draw Title ===
        gui.setColor(0, 0, 0);
        gui.setFontSize((int) (titleHeight * 0.6));
        gui.drawString("Leaderboard", x + (width * 0.35),
                y + titleHeight * 0.75);

        y += titleHeight;

        // === Draw Column Headers ===
        gui.setFontSize((int) (rowHeight * 0.4));
        gui.setColor(50, 50, 50);

        gui.drawString("Rank", x + width * 0.07, y + rowHeight * 0.7);
        gui.drawString("Name", x + width * 0.25, y + rowHeight * 0.7);
        gui.drawString("Score", x + width * 0.75, y + rowHeight * 0.7);

        y += rowHeight * 1.1;

        // === Draw each entry ===
        int rank = 1;
        entries.sort(Comparator.comparingInt(BasePlayer::getGamesWon).reversed());
        for (int i = 0; i < entries.size(); i++) {
            BasePlayer e = entries.get(i);

            // alternating backgrounds
            if (i % 2 == 0) gui.setColor(255, 255, 255);
            else gui.setColor(245, 245, 245);

            gui.fillRect(x, y, width - padding * 2, rowHeight);

            // text color
            gui.setColor(0, 0, 0);

            gui.drawString(String.valueOf(rank), x + width * 0.07, y + rowHeight * 0.65);
            gui.drawString(e.getPlayerName(), x + width * 0.25, y + rowHeight * 0.65);
            gui.drawString(String.valueOf(e.getGamesWon()), x + width * 0.75, y + rowHeight * 0.65);

            y += rowHeight;
            rank++;
        }
    }
}
