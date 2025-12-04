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

import static com.github.Andiritoo.prog1_muehle.common.NodeValue.BLACK;
import static com.github.Andiritoo.prog1_muehle.common.NodeValue.WHITE;

public class GameBoard implements Drawable, Clickable {

    private final GameController controller;
    private final Gui gui;   // <<< GUI-Referenz speichern

    private Integer selected = null;

    private static final double[] X = {0, 0.5, 1, 1, 1, 0.5, 0, 0};
    private static final double[] Y = {0, 0, 0, 0.5, 1, 1, 1, 0.5};

    public GameBoard(GameController controller, Gui gui) {
        this.controller = controller;
        this.gui = gui;  // <<< merken für refresh()
    }

    @Override
    public void draw(Gui gui) {
        //TODO: reopen Leaderboard and increment gamesWon for Winner --> pass back to leaderboard

        double size = Math.min(gui.getWidth(), gui.getHeight());
        double border = size * 0.1;

        double usable = size - 2 * border;

        double nodeSize = usable * 0.02;
        double pieceSize = usable * 0.04;

        for (int layer = 0; layer < 3; layer++) {

            double layerFraction = (2 - layer) / 2.0;
            double side = usable * (0.5 + layerFraction * 0.5);

            double x0 = border + (usable - side) / 2;
            double y0 = border + (usable - side) / 2;

            gui.setStrokeWidth(size * 0.003);
            gui.drawRect(x0, y0, side, side);

            gui.drawLine(x0 + side * 0.5, y0, x0 + side * 0.5, y0 + side * 0.25);
            gui.drawLine(x0 + side, y0 + side * 0.5, x0 + side * 0.75, y0 + side * 0.5);
            gui.drawLine(x0 + side * 0.5, y0 + side, x0 + side * 0.5, y0 + side * 0.75);
            gui.drawLine(x0, y0 + side * 0.5, x0 + side * 0.25, y0 + side * 0.5);

            for (int j = 0; j < 8; j++) {
                double cx = x0 + side * X[j];
                double cy = y0 + side * Y[j];

                NodeValue value = controller.getState().getBoard()[layer][j];

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
            }
        }
    }

    @Override
    public Shape getInteractiveArea(Gui gui) {
        return new Rectangle(0, 0, gui.getWidth(), gui.getHeight());
    }

    @Override
    public void onLeftClick(double x, double y) {
        int pos = positionFromCoordinates(x, y);
        System.out.println("CLICK at " + x + ", " + y + " → pos: " + pos);

        if (pos == -1) return;

        if (controller.isAwaitingRemove()) {
            System.out.println(">>> REMOVE-PHASE: remove stone at " + pos);
            controller.handleUserMove(new Move(pos, -1));
            gui.refresh();
            return;
        }

        if (controller.getGamePhase() == GamePhase.PLACE) {
            System.out.println(">>> PLACE-PHASE: set stone at " + pos);
            controller.handleUserMove(new Move(-1, pos));
            gui.refresh();
            return;
        }

        if (selected == null) {
            System.out.println(">>> SELECT: " + pos);
            selected = pos;
        } else {
            System.out.println(">>> MOVE: " + selected + " → " + pos);
            controller.handleUserMove(new Move(selected, pos));
            selected = null;
        }

        gui.refresh();
    }



    @Override
    public void onRightClick(double x, double y) {
    }

    private int positionFromCoordinates(double x, double y) {

        double size = Math.min(gui.getWidth(), gui.getHeight());
        double usable = size - 2 * (size * 0.1);

        for (int layer = 0; layer < 3; layer++) {

            double layerFraction = (2 - layer) / 2.0;
            double side = usable * (0.5 + layerFraction * 0.5);
            double border = size * 0.1;

            double x0 = border + (usable - side) / 2;
            double y0 = border + (usable - side) / 2;

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
