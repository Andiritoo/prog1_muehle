package com.github.Andiritoo.prog1_muehle.botPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import com.github.Andiritoo.prog1_muehle.common.Move;
import com.github.Andiritoo.prog1_muehle.common.NodeValue;
import com.github.Andiritoo.prog1_muehle.game.GameEngineImpl;
import com.github.Andiritoo.prog1_muehle.game.GameState;
import com.github.Andiritoo.prog1_muehle.player.BasePlayer;
import com.github.Andiritoo.prog1_muehle.player.Player;

public class BotPlayer extends BasePlayer implements Player {

  private final NodeValue playerColor;
  private final NodeValue opponentColor;

  public BotPlayer(NodeValue playerColor) {
    this.playerColor = playerColor; // we need to remember whether we are black or white
    this.opponentColor = playerColor == NodeValue.WHITE ? NodeValue.BLACK : NodeValue.WHITE;
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
    HashSet<Integer> opponentIndexes = getAllIndexes(gs.getBoard(), opponentColor);
    Integer[] opponentIndexesArr = opponentIndexes.toArray(new Integer[opponentIndexes.size()]);

    // TODO: find a clever way to determine which node should be removed
    Random rand = new Random();
    int optimalIndex = opponentIndexesArr[rand.nextInt(opponentIndexesArr.length)];

    return new Move(optimalIndex, -1);
  }

  private Move placeNode(GameState gs) {
    HashSet<Integer> freeIndexes = getAllIndexes(gs.getBoard(), NodeValue.EMPTY);
    Integer[] freeIndexesArr = freeIndexes.toArray(new Integer[freeIndexes.size()]);
    
    // TODO find a clever way to determine where to place a new node
    Random rand = new Random();
    int optimalIndex =  freeIndexesArr[rand.nextInt(freeIndexesArr.length)];

    return new Move(-1, optimalIndex);
  }

  private Move moveNode(GameState gs) {

    HashSet<Integer> myIndexes = getAllIndexes(gs.getBoard(), playerColor);
    HashSet<Integer> freeIndexes = getAllIndexes(gs.getBoard(), NodeValue.EMPTY);

    // placeHolders to fill
    int myBestPos = 0;
    int myBestDest = 0;

    // TODO rank the valid moves and take the best one
    if (myIndexes.size() <= 3) { 
      Integer[] myIndexesArr = myIndexes.toArray(new Integer[myIndexes.size()]);
      Integer[] freeIndexesArr = freeIndexes.toArray(new Integer[freeIndexes.size()]);

      // TODO check if I have a mill

      Random rand = new Random();

      myBestPos = myIndexesArr[rand.nextInt(myIndexesArr.length)];
      myBestDest = freeIndexesArr[rand.nextInt(freeIndexesArr.length)];
      
    } else { 

      // get all valid moves into a map
      HashMap<Integer, HashSet<Integer>> validMoves = new HashMap<Integer, HashSet<Integer>>();
      for (int index : myIndexes) {
        validMoves.put(index, getAdjacentEmptyNodes(gs.getBoard(), index));
      }

      // find best valid move
      for (Map.Entry<Integer, HashSet<Integer>> entry : validMoves.entrySet()) {
        Integer key = entry.getKey();
        HashSet<Integer> value = entry.getValue();
        if (value.size() > 0) {
          myBestPos = key;
          Random rand = new Random();
          Integer[] freeIndexesArr = value.toArray(new Integer[value.size()]);
          myBestDest = freeIndexesArr[rand.nextInt(freeIndexesArr.length)];
        }
      }
    }

    return new Move(myBestPos, myBestDest);
  }

  private HashSet<Integer> getAllIndexes(NodeValue[][] board, NodeValue playerColor) {
    HashSet<Integer> indexes = new HashSet<>();
    for (int layer = 0; layer < 3; layer++) {
      for (int position = 0; position < 8; position++) {
        if (board[layer][position] == playerColor) {
          indexes.add(Move.convertToIndex(layer, position));
        }
      }
    }
    return indexes;
  }

  private HashSet<Integer> getAdjacentEmptyNodes(NodeValue[][] board, int index) {
    // all adjactent indexes 
    HashSet<Integer> adjacentIndexes = new HashSet<>();
    int[] pos = Move.convertToPosition(index);
    int layer = pos[0];
    int position = pos[1];

    // Check adjacent positions on same layer (circular)
    int prevPosition = position == 0 ? 7 : position - 1;
    int nextPosition = position == 7 ? 0 : position + 1;
    if (board[layer][prevPosition] == NodeValue.EMPTY) {
      adjacentIndexes.add(layer * 8 + prevPosition);
    }
    if (board[layer][nextPosition] == NodeValue.EMPTY) {
      adjacentIndexes.add(layer * 8 + nextPosition);
    }

    // According to GameState comment: even positions (2,4,6,8 in 1-based indexing) allow layer changes
    // In 0-based indexing, that's positions 1, 3, 5, 7
    if (position % 2 == 1) {
      int prevLayer = layer == 0 ? -1 : layer - 1;
      int nextLayer = layer == 2 ? -1 : layer + 1;
      if (prevLayer != -1 && board[prevLayer][position] == NodeValue.EMPTY) {
        adjacentIndexes.add(Move.convertToIndex(prevLayer, position));
      } 
      if (nextLayer != -1 &&  board[nextLayer][position] == NodeValue.EMPTY) {
        adjacentIndexes.add(Move.convertToIndex(nextLayer, position));
      }

    } 

    return adjacentIndexes;
  }
}

