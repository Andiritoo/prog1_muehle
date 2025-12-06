package com.github.Andiritoo.prog1_muehle.game;

import com.github.Andiritoo.prog1_muehle.common.Move;
import com.github.Andiritoo.prog1_muehle.common.NodeValue;
import com.github.Andiritoo.prog1_muehle.player.Player;

import java.util.*;

import static com.github.Andiritoo.prog1_muehle.common.NodeValue.*;

public class GameEngineImpl implements GameEngine {

    private final GameState state;

    private static final int[][] MILL_COMBINATIONS = {
            // outer ring
            {0, 1, 2}, {2, 3, 4}, {4, 5, 6}, {6, 7, 0},

            // middle ring
            {8, 9, 10}, {10, 11, 12}, {12, 13, 14}, {14, 15, 8},

            // inner ring
            {16, 17, 18}, {18, 19, 20}, {20, 21, 22}, {22, 23, 16},

            // mill crosses
            {1, 9, 17},
            {3, 11, 19},
            {5, 13, 21},
            {7, 15, 23},
    };

    private static final int[][] ADJ = {
            // outer ring
            {1,7},      // 0
            {0,2,9},    // 1
            {1,3},      // 2
            {2,4,11},   // 3
            {3,5},      // 4
            {4,6,13},   // 5
            {5,7},      // 6
            {6,0,15},   // 7

            // middle ring
            {9,15},     // 8
            {8,10,1,17},// 9
            {9,11},     // 10
            {10,12,3,19},//11
            {11,13},    // 12
            {12,14,5,21},//13
            {13,15},    // 14
            {14,8,7,23},// 15

            // inner ring
            {17,23},    // 16
            {16,18,9},  // 17
            {17,19},    // 18
            {18,20,11}, // 19
            {19,21},    // 20
            {20,22,13}, // 21
            {21,23},    // 22
            {22,16,15}  // 23
    };

    public GameEngineImpl(Player white, Player black) {
        state = new GameState();
        state.setWhite(white);
        state.setBlack(black);
        state.setWhiteToMove(true);

        state.setStonesToPlaceWhite(9);
        state.setStonesToPlaceBlack(9);

        state.setGameInProgress(true);

        NodeValue[][] board = new NodeValue[3][8];
        for (int i = 0; i < 3; i++)
            Arrays.fill(board[i], EMPTY);

        state.setBoard(board);

        state.setAwaitingRemove(false);
    }

    @Override
    public GameState getState() {
        return state;
    }

    @Override
    public boolean isMoveValid(Move move) {
        if (isGameOver()) return false;

        int from = move.getFrom();
        int to = move.getTo();

        if (from < -1 || from >= 24) return false;
        if (to < -1 || to >= 24) return false;
        if (from == -1 && to == -1) return false;

        if (getPhaseForCurrentPlayer() == GamePhase.PLACE && from == -1) {
            return isEmpty(to);
        }

        if (state.isAwaitingRemove()) {
            int pos = move.getFrom();

            if (pos < 0) return false;
            if (isEmpty(pos)) return false;

            if (belongsToCurrent(pos)) return false;

            return true;
        }

        if (!belongsToCurrent(from)) return false;

        if (!isEmpty(to)) return false;

        if (getPhaseForCurrentPlayer() == GamePhase.MOVE) {
            return areAdjacent(from, to);
        }

        if (getPhaseForCurrentPlayer() == GamePhase.JUMP) {
            return true; // Springen erlaubt überall
        }

        return false;
    }

    @Override
    public GameState applyMove(Move move) {
        debugState("Before Move: " + move.getFrom() + " -> " + move.getTo());

        int from = move.getFrom();
        int to = move.getTo();

        if (state.isAwaitingRemove()) {
            System.out.println("REMOVE: removing stone at " + from);
            removeStone(from);
            state.setAwaitingRemove(false);
            finalizeTurn();

            debugState("After REMOVE");
            return state;
        }

        if (getPhaseForCurrentPlayer() == GamePhase.PLACE) {
            System.out.println("PLACE: setting stone at " + to);
            placeStone(to);

            if (createsMill(to)) {
                System.out.println("Mill formed → Await Remove");
                state.setAwaitingRemove(true);
                debugState("After PLACE/MILL");
                return state;
            }

            finalizeTurn();
            checkPhaseTransition();
            debugState("After PLACE");
            return state;
        }

        System.out.println("MOVE: " + from + " -> " + to);
        moveStone(from, to);

        if (createsMill(to)) {
            System.out.println("Mill formed while moving → Await Remove");
            state.setAwaitingRemove(true);
            debugState("After MOVE/MILL");
            return state;
        }

        finalizeTurn();

        debugState("After MOVE");
        return state;
    }

    @Override
    public boolean isGameOver() {
        return (countPlayable(WHITE) < 3 && state.getStonesToPlaceWhite() == 0) || (countPlayable(BLACK) < 3 && state.getStonesToPlaceBlack() == 0);
    }

    @Override
    public Player getWinner() {
        if (!state.isGameInProgress()) {
            return state.isWhiteToMove() ? state.getBlack() : state.getWhite();
        }
        return null;
    }

    private boolean isEmpty(int pos) {
        if (pos < 0 || pos >= 24) {
            return false;
        }
        int layer = pos / 8;
        int index = pos % 8;
        return state.getBoard()[layer][index] == EMPTY;
    }

    private boolean belongsToCurrent(int pos) {
        if (pos < 0 || pos >= 24) {
            return false;
        }
        int layer = pos / 8;
        int index = pos % 8;

        NodeValue current = state.isWhiteToMove() ? WHITE : BLACK;
        return state.getBoard()[layer][index] == current;
    }

    private void placeStone(int to) {
        int layer = to / 8;
        int idx = to % 8;
        state.getBoard()[layer][idx] = state.isWhiteToMove() ? WHITE : BLACK;

        if (state.isWhiteToMove()) {
            state.setStonesToPlaceWhite(state.getStonesToPlaceWhite() - 1);
        } else {
            state.setStonesToPlaceBlack(state.getStonesToPlaceBlack() - 1);
        }
    }

    private void moveStone(int from, int to) {
        int fl = from / 8, fi = from % 8;
        int tl = to / 8, ti = to % 8;

        NodeValue[][] b = state.getBoard();
        NodeValue val = b[fl][fi];

        b[fl][fi] = EMPTY;
        b[tl][ti] = val;
    }

    private void removeStone(int pos) {
        if (pos < 0 || pos >= 24) {
            return;
        }
        int layer = pos / 8;
        int idx = pos % 8;
        state.getBoard()[layer][idx] = EMPTY;
        state.setAwaitingRemove(false);
    }

    private void finalizeTurn() {
        state.setWhiteToMove(!state.isWhiteToMove());
    }

    private void checkPhaseTransition() {
        if (state.getStonesToPlaceWhite() == 0 &&
                state.getStonesToPlaceBlack() == 0) {

        }

        if (countPlayable(current()) == 3) {
        }
    }

    private NodeValue current() {
        return state.isWhiteToMove() ? WHITE : BLACK;
    }

    private int countPlayable(NodeValue v) {
        int c = 0;
        for (NodeValue[] layer : state.getBoard()) {
            for (NodeValue n : layer) {
                if (n == v) c++;
            }
        }
        return c;
    }

    private boolean createsMill(int globalPos) {
        NodeValue[][] b = state.getBoard();
        NodeValue player = state.isWhiteToMove() ? WHITE : BLACK;

        for (int[] mill : MILL_COMBINATIONS) {
            boolean containsPos = false;
            for (int p : mill)
                if (p == globalPos)
                    containsPos = true;

            if (!containsPos) continue;

            boolean full = true;
            for (int p : mill) {
                int layer = p / 8;
                int index = p % 8;
                if (b[layer][index] != player) {
                    full = false;
                    break;
                }
            }

            if (full) return true;
        }

        return false;
    }

    private boolean areAdjacent(int from, int to) {
        for (int n : ADJ[from]) {
            if (n == to) return true;
        }
        return false;
    }

    private void debugState(String prefix) {
        System.out.println("=== " + prefix + " ===");
        System.out.println("White to move: " + state.isWhiteToMove());
        System.out.println("Phase: " + getPhaseForCurrentPlayer());
        System.out.println("AwaitingRemove: " + state.isAwaitingRemove());
        System.out.println("StonesToPlace W/B: " + state.getStonesToPlaceWhite()
                + "/" + state.getStonesToPlaceBlack());
    }

    @Override
    public GamePhase getPhaseForCurrentPlayer() {
        boolean white = state.isWhiteToMove();
        int stonesToPlace = white
                ? state.getStonesToPlaceWhite()
                : state.getStonesToPlaceBlack();

        int stonesOnBoard = countPlayable(white ? WHITE : BLACK);

        if (stonesToPlace > 0)
            return GamePhase.PLACE;

        if (stonesOnBoard == 3)
            return GamePhase.JUMP;

        return GamePhase.MOVE;
    }


}
