package com.game.golfball;

import com.badlogic.gdx.math.Vector3;
import com.game.terrain.GameRules;
import com.game.terrain.GetHeight;
import com.game.golfball.Bouncing;
import com.game.terrain.Maze.Wall;

import java.util.List;

public class GolfBallMovement {
    private GolfBall ball;
    private PhysicsEngine physicsEngine;
    private GameRules gameRules;
    private List<Wall> walls;

    /**
     * Constructs a GolfBallMovement object with the specified ball, physics engine, and game rules.
     *
     * @param ball          The golf ball to be moved.
     * @param physicsEngine The physics engine used for simulating ball movement.
     * @param gameRules     The game rules governing ball movement and game status.
     * @param walls         The list of walls in the maze.
     */
    public GolfBallMovement(GolfBall ball, PhysicsEngine physicsEngine, GameRules gameRules, List<Wall> walls) {
        this.ball = ball;
        this.physicsEngine = physicsEngine;
        if (gameRules == null) {
            throw new IllegalArgumentException("gameRules cannot be null");
        }
        this.gameRules = gameRules;
        this.walls = walls;
    }

    /**
     * Applies the given force to the golf ball, updating its velocity based on the force and mass.
     *
     * @param force The force vector to be applied to the ball.
     */
    public void applyForce(Vector3 force) {
        if (gameRules == null || gameRules.isGameOver()) {
            System.out.println("Cannot make a shot. The game is over.");
            return;
        }

        if (ball.isMoving()) {
            System.out.println("Cannot make a shot while the ball is moving.");
            return;
        }

        // Increment the shot counter
        gameRules.incrementShotCounter();

        // Update the last valid position before applying force
        ball.updateLastValidPosition();

        // Calculate the velocity given the force and mass of the ball
        Vector3 velocity = new Vector3(force).scl((float) (1 / ball.getMass()));
        ball.setVelocity(velocity);
    }

    /**
     * Updates the position and velocity of the golf ball based on the current state and physics calculations.
     * Also adjusts the ball's vertical position based on the terrain height and checks game status.
     */
    public void update() {
        if (gameRules == null || gameRules.isGameOver()) {
            return;
        }

        Vector3 currentPosition = ball.getPosition();
        Vector3 currentVelocity = ball.getVelocity();

        physicsEngine.setState(currentPosition.x, currentPosition.z, currentVelocity.x, currentVelocity.z);

        double[] newState = physicsEngine.runSingleStep(currentPosition, currentVelocity);

        // Update ball position and velocity
        Vector3 newPosition = new Vector3((float) newState[0], ball.getPosition().y, (float) newState[1]);
        Vector3 newVelocity = new Vector3((float) newState[2], 0, (float) newState[3]);

        // Check for collisions and apply bounce logic
        newVelocity = Bouncing.detectCollisionAndBounce(newPosition, newVelocity, walls);

        ball.setPosition(newPosition);
        ball.setVelocity(newVelocity);

        // Adjust the ball's vertical position based on the terrain height
        ball.getPosition().y = (float) GetHeight.getHeight(PhysicsEngine.heightFunction, ball.getPosition().x, ball.getPosition().z);

        // Check if the ball has effectively stopped moving and set velocity to zero
        if (!ball.isMoving()) {
            ball.setVelocity(new Vector3(0, 0, 0));
        }

        // Check the game status after updating the ball position
        gameRules.checkGameStatus();
    }
}
