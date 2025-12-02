package com.github.Andiritoo.prog1_muehle.common;

/**
 * SET: fromRow/fromCol = -1, toRow/toCol = Target
 * MOVE/JUMP: fromRow/fromCol -> toRow/toCol
 * REMOVE: removeRow/removeCol != -1 (additional)
 */
public class Move {

    public int fromRow = -1;
    public int fromCol = -1;

    public int toRow = -1;
    public int toCol = -1;

    public int removeRow = -1;
    public int removeCol = -1;
}
