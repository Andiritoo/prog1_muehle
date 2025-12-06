package com.github.Andiritoo.prog1_muehle.botPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import com.github.Andiritoo.prog1_muehle.common.Move;
import com.github.Andiritoo.prog1_muehle.common.NodeValue;
import com.github.Andiritoo.prog1_muehle.game.GameState;
import com.github.Andiritoo.prog1_muehle.player.BasePlayer;
import com.github.Andiritoo.prog1_muehle.player.Player;

public class BotPlayer extends BasePlayer implements Player {

  private final NodeValue playerColor;
  private final NodeValue opponentColor;

  public BotPlayer(NodeValue playerColor) {
    this.playerColor = playerColor; // we need to remember whether we are black or white
    if (playerColor == NodeValue.WHITE) {
      this.opponentColor = NodeValue.BLACK;
    } else {
      this.opponentColor = NodeValue.WHITE;
    }
  }

  @Override
  public Move move(GameState gs) {
    int stonesToPlace =
        gs.isWhiteToMove() ? gs.getStonesToPlaceWhite() : gs.getStonesToPlaceBlack();

    if (gs.isAwaitingRemove()) {
      return removeNode(gs);
    } else if (stonesToPlace > 0) {
      return placeNode(gs);
    } else {
      return moveNode(gs);
    }
  }

  private Move removeNode(GameState gs) {
    ArrayList<Integer> opponentIndexes = getAllIndexes(gs.getBoard(), opponentColor);

    Random r = new Random();
    int pos = opponentIndexes.get(r.nextInt(opponentIndexes.size()));

    // TODO: find a clever way to determine which node should be removed

    return new Move(pos, -1);
  }

  private Move placeNode(GameState gs) {
    ArrayList<Integer> freeIndexes = getAllIndexes(gs.getBoard(), NodeValue.EMPTY);
    Random r = new Random();
    int pos = freeIndexes.get(r.nextInt(freeIndexes.size()));

    // TODO find a clever way to determine where to add a new node

    return new Move(-1, pos);
  }

  private Move moveNode(GameState gs) {
    ArrayList<Integer> myIndexes = getAllIndexes(gs.getBoard(), playerColor);
    ArrayList<Integer> freeIndexes = getAllIndexes(gs.getBoard(), NodeValue.EMPTY);
    if (myIndexes.size() < 3) { // jumps allowed
      Random r = new Random();
      int pos = freeIndexes.get(r.nextInt(freeIndexes.size()));
      int my = myIndexes.get(r.nextInt(myIndexes.size()));
      return new Move(my, pos);
    } else { // regular move
      HashMap<Integer, ArrayList<Integer>> validMoves = new HashMap<Integer, ArrayList<Integer>>();
      for (int index : myIndexes) {
        validMoves.put(index, getAdjacentEmptyNodes(gs.getBoard(), index));
      }


      // placeholders for the best from/to pair
      int myBestPos = 0;
      int myBestDest = 0;

      // find best valid move
      for (Map.Entry<Integer, ArrayList<Integer>> entry : validMoves.entrySet()) {
        Integer key = entry.getKey();
        ArrayList<Integer> value = entry.getValue();
        if (value.size() > 0) {
          Random r = new Random();
          myBestPos = key;
          myBestDest = value.get(r.nextInt(value.size()));
        }
      }
      return new Move(myBestPos, myBestDest);
    }

    // TODO extend: return a valid move that is ranked somehow
  }

  private ArrayList<Integer> getAllIndexes(NodeValue[][] board, NodeValue playerColor) {
    ArrayList<Integer> indexes = new ArrayList<>();
    for (int layer = 0; layer < 3; layer++) {
      for (int position = 0; position < 8; position++) {
        if (board[layer][position] == playerColor) {
          indexes.add(Move.convertToIndex(layer, position));
        }
      }
    }
    return indexes;
  }

  // private List<int[]> getAdjacentEmptyNodes(NodeValue[][] board, int pos) {
  private ArrayList<Integer> getAdjacentEmptyNodes(NodeValue[][] board, int index) {
    ArrayList<Integer> adjacent = new ArrayList<>();

    // assume position is valid
    int[] pos = Move.convertToPosition(index);
    int layer = pos[0];
    int position = pos[1];

    // Check adjacent positions on same layer (circular)
    int prevPosition = position == 0 ? 7 : position - 1;
    int nextPosition = position == 7 ? 0 : position + 1;

    if (board[layer][prevPosition] == NodeValue.EMPTY) {
      adjacent.add(layer * 8 + prevPosition);
    }
    if (board[layer][nextPosition] == NodeValue.EMPTY) {
      adjacent.add(layer * 8 + nextPosition);
    }

    // Check connections to other layers (at even positions: 1, 3, 5, 7 in 1-based, which are 0,
    // 2, 4, 6 in 0-based)
    // According to GameState comment: even positions (2,4,6,8 in 1-based) allow layer changes
    // In 0-based indexing, that's positions 1, 3, 5, 7
    // TODO this loop is not efficient
    if (position % 2 == 1) {
      for (int otherLayer = 0; otherLayer < 3; otherLayer++) {
        if (otherLayer != layer && board[otherLayer][position] == NodeValue.EMPTY
            && Math.abs(otherLayer - layer) == 1) {
          adjacent.add(Move.convertToIndex(otherLayer, position));
        }
      }
    }

    return adjacent;
  }

}
