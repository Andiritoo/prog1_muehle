package com.github.Andiritoo.prog1_muehle;

public class GameNode {
    private int layer;
    private int point;
    private NodeValue value;

    public GameNode(int layer, int point, NodeValue value) {
        this.layer = layer;
        this.point = point;
        this.value = value;
    }

    public int getLayer() {
        return layer;
    }

    public int getPoint() {
        return point;
    }

    public NodeValue getValue() {
        return value;
    }

    public void setValue(NodeValue value) {
        this.value = value;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }
}
