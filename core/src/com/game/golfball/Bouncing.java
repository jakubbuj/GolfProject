package com.game.golfball;

import com.badlogic.gdx.math.Vector3;
import com.game.terrain.Maze.Wall;

import java.util.List;

public class Bouncing {
    private static final float BUFFER_ZONE = 0.01f; // Buffer zone around the walls

    /**
     * Detects collision with a wall and calculates the new velocity after the bounce.
     * 
     * @param position The current position of the ball.
     * @param velocity The current velocity of the ball.
     * @param walls The list of walls in the maze.
     * @return The new velocity vector after the bounce.
     */
    public static Vector3 detectCollisionAndBounce(Vector3 position, Vector3 velocity, List<Wall> walls) {
        if(walls == null) return velocity;
        for (Wall wall : walls) {
            if (isColliding(position, wall)) {
                return calculateBounce(position, velocity, wall);
            }
        }
        return velocity;
    }

    /**
     * Checks if the ball is colliding with the given wall.
     * 
     * @param position The current position of the ball.
     * @param wall The wall to check for collision.
     * @return True if the ball is colliding with the wall, false otherwise.
     */
    private static boolean isColliding(Vector3 position, Wall wall) {
        float wallHalfWidth = wall.getWidth() / 2 + BUFFER_ZONE;
        float wallHalfDepth = wall.getDepth() / 2 + BUFFER_ZONE;
        Vector3 wallPosition = wall.getPosition();

        // Check collision with the wall's bounding box
        return (position.x > wallPosition.x - wallHalfWidth && position.x < wallPosition.x + wallHalfWidth) &&
               (position.z > wallPosition.z - wallHalfDepth && position.z < wallPosition.z + wallHalfDepth);
    }

    /**
     * Calculates the new velocity vector after bouncing off the wall.
     * 
     * @param position The current position of the ball.
     * @param velocity The current velocity of the ball.
     * @param wall The wall the ball collided with.
     * @return The new velocity vector after the bounce.
     */
    private static Vector3 calculateBounce(Vector3 position, Vector3 velocity, Wall wall) {
        Vector3 normal = calculateNormal(position, wall);
        return velocity.sub(normal.scl(2 * velocity.dot(normal)));
    }

    /**
     * Calculates the normal vector of the wall surface.
     * 
     * @param position The current position of the ball.
     * @param wall The wall to calculate the normal for.
     * @return The normal vector of the wall surface.
     */
    private static Vector3 calculateNormal(Vector3 position, Wall wall) {
        float wallHalfWidth = wall.getWidth() / 2;
        float wallHalfDepth = wall.getDepth() / 2;
        Vector3 wallPosition = wall.getPosition();

        // Check which side of the wall the ball is colliding with
        float deltaX = position.x - wallPosition.x;
        float deltaZ = position.z - wallPosition.z;

        if (Math.abs(deltaX) > Math.abs(deltaZ)) {
            // Colliding with vertical sides (left or right)
            return new Vector3(Math.signum(deltaX), 0, 0);
        } else {
            // Colliding with horizontal sides (top or bottom)
            return new Vector3(0, 0, Math.signum(deltaZ));
        }
    }
}
