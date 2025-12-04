package com.github.Andiritoo.prog1_muehle.common;

/**
 * SET: fromRow/fromCol = -1, toRow/toCol = Target
 * MOVE/JUMP: fromRow/fromCol -> toRow/toCol
 * REMOVE: removeRow/removeCol != -1 (additional)
 */
public class Move {

    private int from = -1;
    private int to = -1;
    private int remove = -1;

    public Move() {}

    public Move(int from, int to) {
        this.from = from;
        this.to = to;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public int getRemove() {
        return remove;
    }

    public void setRemove(int remove) {
        this.remove = remove;
    }
}
