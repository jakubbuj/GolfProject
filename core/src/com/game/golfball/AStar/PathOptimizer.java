package com.game.golfball.AStar;

import com.badlogic.gdx.math.Vector3;
import java.util.*;

public class PathOptimizer {
    private List<Vector3> path;

    public PathOptimizer(List<Vector3> path) {
        this.path = path;
    }

    public List<Vector3> optimizePath() {
        if (path == null || path.isEmpty()) {
            return Collections.emptyList();
        }

        List<Vector3> optimizedPath = new ArrayList<>();
        Vector3 start = path.get(0);
        Vector3 direction = new Vector3();

        for (int i = 1; i < path.size(); i++) {
            Vector3 current = path.get(i);
            Vector3 nextDirection = new Vector3(current).sub(start).nor();

            if (direction.isZero() || nextDirection.epsilonEquals(direction, 0.1f)) {
                direction = nextDirection;
            } else {
                optimizedPath.add(start);
                direction.setZero();
            }

            start = current;
        }

        optimizedPath.add(start); // Add the last point

        return optimizedPath;
    }
}
