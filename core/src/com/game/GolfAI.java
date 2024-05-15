package com.game;

import com.badlogic.gdx.math.Vector3;

public class GolfAI {
    private static Vector3 targetPosition;
    private float targetRadius;
    private PhysicsEngine physicsEngine;
    private GolfBall AIball;

    public GolfAI(GolfBall AIball, Vector3 targetPosition, float targetRadius, PhysicsEngine physicsEngine) {
        this.AIball = AIball;
        GolfAI.targetPosition = targetPosition;
        this.targetRadius = targetRadius;
        this.physicsEngine = physicsEngine;
    }

    public Vector3 findBestShot() {
        System.out.println("GolfAI: Calculating best shot...");
        Vector3 initialVelocity = new Vector3(1, 0, 1); // Start with a smaller and more realistic initial velocity
        float tolerance = 0.01f; // Convergence threshold
        Vector3 currentVelocity = new Vector3(initialVelocity);
        float damping = 0.8f; // Damping factor to prevent overshooting

        int iteration = 0;
        while (iteration < 100) {
            Vector3 deviation = calculateFunction(currentVelocity);
            if (deviation.len() < tolerance) {
                System.out.println("Converged to the best shot.");
                break;
            }

            float[][] J = calculateJacobian(currentVelocity);
            float[][] J_inv = invertMatrix(J);
            if (J_inv == null) {
                System.out.println("Jacobian inversion failed. Adjustments cannot be made.");
                break;
            }

            Vector3 adjustment = multiplyMatrixVector(J_inv, deviation);
            adjustment.scl(damping); // Apply damping to the adjustment

            currentVelocity.sub(adjustment);
            System.out.println("Iteration: " + iteration + ", Adjusting velocity by " + adjustment + ", New velocity: " + currentVelocity);
            System.out.println();

            iteration++;
        }

        System.out.println("Best shot velocity found: " + currentVelocity);
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
        float h = 0.2f; // small perturbation for finite difference

        Vector3 vxPlus = new Vector3(velocity.x + h, 0, velocity.z);
        Vector3 vxMinus = new Vector3(velocity.x - h, 0, velocity.z);
        Vector3 vzPlus = new Vector3(velocity.x, 0, velocity.z + h);
        Vector3 vzMinus = new Vector3(velocity.x, 0, velocity.z - h);

        Vector3 f_vxPlus = calculateFunction(vxPlus);
        Vector3 f_vxMinus = calculateFunction(vxMinus);
        Vector3 f_vzPlus = calculateFunction(vzPlus);
        Vector3 f_vzMinus = calculateFunction(vzMinus);

        Vector3 dfdx = new Vector3(
            (f_vxPlus.x - f_vxMinus.x) / (2 * h),
            0,
            (f_vxPlus.z - f_vxMinus.z) / (2 * h)
        );
        Vector3 dfdz = new Vector3(
            (f_vzPlus.x - f_vzMinus.x) / (2 * h),
            0,
            (f_vzPlus.z - f_vzMinus.z) / (2 * h)
        );

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
        Vector3 currentPosition = AIball.getPosition();
        Vector3 currentVelocity = AIball.getVelocity();

        physicsEngine.setState(currentPosition.x, currentPosition.z, currentVelocity.x, currentVelocity.z);

        double[] newState = physicsEngine.runSingleStep(currentPosition, currentVelocity);

        AIball.setPosition(new Vector3((float) newState[0], AIball.getPosition().y, (float) newState[1]));

        AIball.setVelocity(new Vector3((float) newState[2], 0, (float) newState[3]));

        AIball.getPosition().y = (float) (physicsEngine.terrainHeight + 0.2f);
    }
}
