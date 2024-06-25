package com.game.golfball.AStar;

import java.util.Objects;

public class Node {
    public int x;
    public int y;
    double gScore, fScore;

    /**
     * Constructs a Node with the specified x and y coordinates
     * @param x the x-coordinate of the node
     * @param y the y-coordinate of the node
     */
    public Node(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Gets the f-score of the node
     * @return the f-score of the node
     */
    public double getFScore() {
        return fScore;
    }

    /**
     * Checks if this node is equal to another object
     * @param o the object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return x == node.x && y == node.y;
    }

    /**
     * Gets the hash code of the node
     * @return the hash code of the node
     */
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    /**
     * Returns a string representation of the node
     * @return a string representation of the node in the format "(x, y)"
     */
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    /**
     * This method is not implemented
     * @return nothing as it throws an exception
     */
    public float getX() {
        throw new UnsupportedOperationException("Unimplemented method 'getX'");
    }
}
