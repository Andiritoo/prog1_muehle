package prog1_muehle;

public class Move {

    // if from is NULL then Action is "Place"
    // if to is NULL then the Action is "Remove"
    // if both are set the Action is "Move"
    private GameNode from;
    private GameNode to;

    public GameNode getFrom() {
        return from;
    }

    public void setFrom(GameNode from) {
        this.from = from;
    }

    public GameNode getTo() {
        return to;
    }

    public void setTo(GameNode to) {
        this.to = to;
    }
}
