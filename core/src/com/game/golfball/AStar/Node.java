package com.game.golfball.AStar;

import com.badlogic.gdx.math.Vector3;
import java.util.Objects;

public class Node {
    Vector3 position;
    double gScore;
    double fScore;

    public Node(Vector3 position, double gScore, double fScore) {
        this.position = position;
        this.gScore = gScore;
        this.fScore = fScore;
    }

    public double getFScore() {
        return fScore;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Node node = (Node) o;
        return position.equals(node.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position);
    }
}
