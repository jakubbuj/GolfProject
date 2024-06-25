package com.game.golfball.AStar;

import java.util.Objects;

public class Node {
    public int x;
    public int y;
    double gScore, fScore;

    public Node(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public double getFScore() {
        return fScore;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return x == node.x && y == node.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    public float getX() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getX'");
    }
}