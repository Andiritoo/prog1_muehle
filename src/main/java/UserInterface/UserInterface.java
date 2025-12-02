package UserInterface;
import ch.trick17.gui.Gui;
import com.github.Andiritoo.prog1_muehle.*;


public class UserInterface {

    public static void main(String[] args){

        Gui gui = Gui.create("Mühli", 1000, 1000);
        GameState game = new GameState();
        GameBoard board = new GameBoard(game.getGameBoard(), Math.min(gui.getHeight(), gui.getWidth()));
        gui.addComponent(board);



        gui.setResizable(true);
        gui.open();
        gui.runUntilClosed(20);



    }


}
