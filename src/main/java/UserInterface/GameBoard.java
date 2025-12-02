package UserInterface;
import ch.trick17.gui.Gui;
import ch.trick17.gui.component.Drawable;
import com.github.Andiritoo.prog1_muehle.*;


public class GameBoard implements Drawable {

    private NodeValue[][] board;

    public GameBoard(NodeValue[][] board){
        this.board = board;
    }

    @Override
    public void draw(Gui gui) {


    }
}
