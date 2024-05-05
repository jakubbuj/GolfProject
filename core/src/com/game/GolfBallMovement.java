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

    public void update(float deltaTime) {
        // Update the ball's position using the physics engine
        Vector3 ballPosition = ball.getPosition();
        Vector3 ballVelocity = ball.getVelocity();

        physicsEngine.runSingleStep(deltaTime, ballPosition, ballVelocity);

        // Update the ball instance with new state from the physics engine
        ball.setPosition(new Vector3(
            (float) physicsEngine.getStateVector()[0],
            (float) physicsEngine.terrainHeight + 0.2f,
            (float) physicsEngine.getStateVector()[1]
        ));

        ball.setVelocity(new Vector3(
            (float) physicsEngine.getStateVector()[2],
            0f, // Y velocity remains 0 because we don't have vertical movement in this physics engine
            (float) physicsEngine.getStateVector()[3]
        ));
    }
}


