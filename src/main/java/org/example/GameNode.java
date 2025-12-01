package org.example;

public class GameNode {
    private int layer;
    private int point;
    private NodeValue value;

    public GameNode(int layer, int point, NodeValue value) {
        this.layer = layer;
        this.point = point;
        this.value = value;
    }
}
