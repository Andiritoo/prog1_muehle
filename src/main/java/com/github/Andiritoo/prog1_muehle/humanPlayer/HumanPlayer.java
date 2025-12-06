package com.github.Andiritoo.prog1_muehle.humanPlayer;


import com.github.Andiritoo.prog1_muehle.common.Move;
import com.github.Andiritoo.prog1_muehle.game.GameState;
import com.github.Andiritoo.prog1_muehle.player.BasePlayer;
import com.github.Andiritoo.prog1_muehle.player.Player;
import com.github.Andiritoo.prog1_muehle.user_interface.UserInputProvider;

public class HumanPlayer extends BasePlayer implements Player {
    private UserInputProvider inputProvider;

    public void setInputProvider(UserInputProvider inputProvider) {
        this.inputProvider = inputProvider;
    }

    @Override
    public Move move(GameState gameState) {
        if (inputProvider == null) {
            return null;
        }

        Integer clickedPos = inputProvider.getClickedPosition();
        if (clickedPos == null) {
            return null;
        }

        boolean isWhiteTurn = gameState.isWhiteToMove();

        if (gameState.isAwaitingRemove()) {
            inputProvider.clearClickedPosition();
            return new Move(clickedPos, -1);
        }

        int stonesToPlace = isWhiteTurn
            ? gameState.getStonesToPlaceWhite()
            : gameState.getStonesToPlaceBlack();

        if (stonesToPlace > 0) {
            // PLACE
            inputProvider.clearClickedPosition();
            return new Move(-1, clickedPos);
        }

        Integer selected = inputProvider.getSelectedPosition();
        if (selected == null) {
            // First click
            inputProvider.setSelectedPosition(clickedPos);
            inputProvider.clearClickedPosition();
            return null;
        } else {
            // Second click
            Move move = new Move(selected, clickedPos);
            inputProvider.setSelectedPosition(null);
            inputProvider.clearClickedPosition();
            return move;
        }
    }
}

