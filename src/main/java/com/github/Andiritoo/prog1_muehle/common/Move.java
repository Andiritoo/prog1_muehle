package com.github.Andiritoo.prog1_muehle.common;

/**
 * PLACE  → (from=-1, to=pos)
 * MOVE   → (from=src, to=dest)
 * REMOVE → (from=pos, to=-1)
 */
public class Move {

    private int from = -1;
    private int to = -1;

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

    /**
     * Converts a position to an index
     * 
     * @param layer
     * @param position number on that layer
     * @return global unique index of the board
     */
    public static int convertToIndex(int layer, int position) {
        return layer * 8 + position;
    }

    /**
     * Converts an index to a position
     * 
     * @param index global unique index on the board
     * @return int[], first index is layer, second is position
     */
    public static int[] convertToPosition(int index) {
        return new int[] { index / 8, index % 8 };
    }
}
