package UserInterface;
import ch.trick17.gui.Gui;
import com.github.Andiritoo.prog1_muehle.*;

public class UserInterface {

    Gui gui = Gui.create("Mühli", 1000, 1000);

    public UserInterface(GameState game){
        while(game.getGameInProgress()){
            new GameBoard(game.getGameBoard());
        }
    }
}
