package com.game;

import com.badlogic.gdx.math.Vector3;

public class GolfBallMovement {

    private GolfBall ball;
    private PhysicsEngine physicsEngine;

    public GolfBallMovement(GolfBall ball, PhysicsEngine physicsEngine) {
        this.ball = ball;
        this.physicsEngine = physicsEngine;
    }

    public void applyForce(Vector3 force) {
        // Calculate the velocity given the force and mass of the ball (assuming a unit mass for simplicity)
        Vector3 velocity = new Vector3(force).scl((float) (1 / ball.getMass())); // scl multples vector by a scalar
        ball.setVelocity(velocity);
    }

    public void update() {
        // Get the current position and velocity of the ball
        Vector3 currentPosition = ball.getPosition();
        Vector3 currentVelocity = ball.getVelocity();

        // Set the current state in the physics engine
        physicsEngine.setState(currentPosition.x, currentPosition.z, currentVelocity.x, currentVelocity.z);

        // Run the physics simulation for one timestep
        double[] newState = physicsEngine.runSingleStep(currentPosition, currentVelocity);

        // Update the ball's position based on the physics engine output
        ball.setPosition(new Vector3((float) newState[0], ball.getPosition().y, (float) newState[1]));

        // Update the ball's velocity based on the physics engine output
        ball.setVelocity(new Vector3((float) newState[2], 0, (float) newState[3]));

        // Adjust the ball's vertical position based on the terrain height
        ball.getPosition().y = (float) (physicsEngine.terrainHeight + 0.2f); // Keeping the ball slightly above the terrain
    }
}



