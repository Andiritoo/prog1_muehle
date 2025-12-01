package org.example;

public class GameNode {
    public int layer;
    public int point;
    public NodeValue value;

    public GameNode(int layer, int point, NodeValue value) {
        this.layer = layer;
        this.point = point;
        this.value = value;
    }
}
