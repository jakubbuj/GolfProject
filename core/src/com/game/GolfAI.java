package com.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

public class GolfAI {
    private static Vector3 targetPosition;
    private float targetRadius;
    private PhysicsEngine physicsEngine;
    private GolfBall AIball;

    public GolfAI(GolfBall AIball, Vector3 targetPosition, float targetRadius, PhysicsEngine physicsEngine) {
        this.AIball = AIball;
        this.targetPosition = targetPosition;
        this.targetRadius = targetRadius;
        this.physicsEngine = physicsEngine;
    }

    public Vector3 findBestShot() {
        System.out.println("GolfAI: Calculating best shot...");
        Vector3 initialVelocity = new Vector3(5, 0, 5); // Start with some reasonable initial velocity
        float tolerance = 0.01f;
        Vector3 currentVelocity = new Vector3(initialVelocity);
        while (true) {
            Vector3 F = calculateFunction(currentVelocity);
            if (F.len() < tolerance)
                break;

            float[][] J = calculateJacobian(currentVelocity);
            float[][] J_inv = invertMatrix(J);
            if (J_inv == null) {
                System.out.println("GolfAI: Unable to proceed with inversion, skipping adjustment.");
                break;
            }
            Vector3 delta = multiplyMatrixVector(J_inv, F);
            currentVelocity.sub(delta);
            System.out.println("GolfAI: Adjusting velocity by " + delta);
        }

        System.out.println("GolfAI: Best shot velocity found: " + currentVelocity);
        return currentVelocity;
    }

    private Vector3 calculateFunction(Vector3 velocity) {
        System.out.println("GolfAI: Running simulation for velocity: " + velocity);
        physicsEngine.setState(AIball.getPosition().x, AIball.getPosition().z, velocity.x, velocity.z);
        double[] afterShot = physicsEngine.runSimulation(velocity.x, velocity.z);
        Vector3 finalPosition = new Vector3((float) afterShot[0], 0, (float) afterShot[1]);
        Vector3 deviation = new Vector3(finalPosition.x - targetPosition.x, 0, finalPosition.z - targetPosition.z);
        System.out.println("GolfAI: Deviation after shot: " + deviation);
        return deviation;
    }

    private float[][] calculateJacobian(Vector3 velocity) {
        System.out.println("GolfAI: Calculating Jacobian for velocity: " + velocity);
        float h = 0.01f; // small perturbation for finite difference
        // Perturb velocity in the x and z directions separately
        Vector3 vxPlus = new Vector3(velocity.x + h, 0, velocity.z);
        Vector3 vxMinus = new Vector3(velocity.x - h, 0, velocity.z);
        Vector3 vzPlus = new Vector3(velocity.x, 0, velocity.z + h);
        Vector3 vzMinus = new Vector3(velocity.x, 0, velocity.z - h);

        // Calculate deviations for perturbed velocities
        Vector3 dfdx = calculateFunction(vxPlus).sub(calculateFunction(vxMinus)).scl(1 / (2 * h));
        Vector3 dfdz = calculateFunction(vzPlus).sub(calculateFunction(vzMinus)).scl(1 / (2 * h));

        float[][] J = new float[][] {
                { dfdx.x, dfdx.z },
                { dfdz.x, dfdz.z }
        };
        return J;
    }

    private float[][] invertMatrix(float[][] matrix) {
        float a = matrix[0][0], b = matrix[0][1], c = matrix[1][0], d = matrix[1][1];
        float det = a * d - b * c;
        System.out.println("GolfAI: Attempting to invert matrix with determinant: " + det);
        if (Math.abs(det) < 0.001) { // Detect near-zero determinant
            System.out.println("GolfAI: Matrix is not invertible or poorly conditioned, det=" + det);
            return null;
        }
        float[][] inv = new float[][] {
                { d / det, -b / det },
                { -c / det, a / det }
        };
        return inv;
    }

    private Vector3 multiplyMatrixVector(float[][] matrix, Vector3 vector) {
        System.out.println("GolfAI: Multiplying matrix with vector: " + vector);
        return new Vector3(
                matrix[0][0] * vector.x + matrix[0][1] * vector.z,
                0,
                matrix[1][0] * vector.x + matrix[1][1] * vector.z);
    }

    public void update() {
        // Get the current position and velocity of the ball
        Vector3 currentPosition = AIball.getPosition();
        Vector3 currentVelocity = AIball.getVelocity();

        // Set the current state in the physics engine
        physicsEngine.setState(currentPosition.x, currentPosition.z, currentVelocity.x, currentVelocity.z);

        // Run the physics simulation for one timestep
        double[] newState = physicsEngine.runSingleStep(currentPosition, currentVelocity);

        // Update the ball's position based on the physics engine output
        AIball.setPosition(new Vector3((float) newState[0], AIball.getPosition().y, (float) newState[1]));

        // Update the ball's velocity based on the physics engine output
        AIball.setVelocity(new Vector3((float) newState[2], 0, (float) newState[3]));

        // Adjust the ball's vertical position based on the terrain height
        AIball.getPosition().y = (float) (physicsEngine.terrainHeight + 0.2f); // Keeping the ball slightly above the
                                                                               // terrain
    }
}
