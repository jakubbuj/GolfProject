package com.game;

import com.badlogic.gdx.math.Vector3;

public class GolfAI {
    private static Vector3 targetPosition;
    private PhysicsEngine physicsEngine;
    private GolfBall AIball;
    private static final double EPSILON_ADAM = 1e-8; // Epsilon for Adam optimizer to ensure numerical stability
    private static final double INITIAL_EPSILON_GRAD = 0.5; // Initial epsilon for gradient approximation
    private static final double MIN_EPSILON_GRAD = 1e-4; // Minimum epsilon for gradient approximation
    private static final int MAX_ITERATIONS = 1000; // Max iterations for convergence
    private static final double INITIAL_LEARNING_RATE = 1; // Initial learning rate for large steps
    private static final double MIN_LEARNING_RATE = 0.005; // Minimum learning rate for fine adjustments
    private static final double TOLERANCE = 0.3; // Tolerance for stopping condition
    private static final double BETA1 = 0.9; // Decay rate for the first moment estimate
    private static final double BETA2 = 0.999; // Decay rate for the second moment estimate

    private Vector3 m; // First moment vector
    private Vector3 v; // Second moment vector
    private double learningRate; // Current learning rate
    private double epsilonGrad; // Current epsilon for gradient approximation
    private int t; // Time step
    private GameRules gameRules; // Game rules

    public GolfAI(GolfBall AIball, Vector3 targetPosition, PhysicsEngine physicsEngine, GameRules gameRules) {
        this.AIball = AIball;
        GolfAI.targetPosition = targetPosition;
        this.physicsEngine = physicsEngine;
        this.m = new Vector3(0, 0, 0);
        this.v = new Vector3(0, 0, 0);
        this.learningRate = INITIAL_LEARNING_RATE;
        this.epsilonGrad = INITIAL_EPSILON_GRAD;
        this.t = 0;
        if (gameRules == null) {
            throw new IllegalArgumentException("gameRules cannot be null");
        }
        this.gameRules = gameRules;
    }

    public Vector3 findBestShot() {
        Vector3 currentVelocity = new Vector3(2f, 0f, -6f);

        for (int iteration = 0; iteration < MAX_ITERATIONS; iteration++) {
            Vector3 deviation = calculateFunction(currentVelocity);

            System.out.println();
            System.out.println("Iteration: " + iteration + ", Velocity: " + currentVelocity + ", Deviation: " + deviation);

            if (deviation.len() < TOLERANCE) {
                System.out.println("Convergence achieved.");
                break;
            }

            Vector3 gradient = approximateGradient(currentVelocity,deviation);
            //System.out.println("Gradient: " + gradient);

            // Update time step
            t++;

            // Update biased first moment estimate
            m.x = (float) (BETA1 * m.x + (1 - BETA1) * gradient.x);
            m.z = (float) (BETA1 * m.z + (1 - BETA1) * gradient.z);

            // Update biased second moment estimate
            v.x = (float) (BETA2 * v.x + (1 - BETA2) * gradient.x * gradient.x);
            v.z = (float) (BETA2 * v.z + (1 - BETA2) * gradient.z * gradient.z);

            // Compute bias-corrected first moment estimate
            float mHatX = (float) (m.x / (1 - Math.pow(BETA1, t)));
            float mHatZ = (float) (m.z / (1 - Math.pow(BETA1, t)));

            // Compute bias-corrected second moment estimate
            float vHatX = (float) (v.x / (1 - Math.pow(BETA2, t)));
            float vHatZ = (float) (v.z / (1 - Math.pow(BETA2, t)));

            // Update velocity with momentum
            currentVelocity.x -= learningRate * mHatX / (Math.sqrt(vHatX) + EPSILON_ADAM);
            currentVelocity.z -= learningRate * mHatZ / (Math.sqrt(vHatZ) + EPSILON_ADAM);

            // Gradient and velocity clipping to prevent overshooting
            gradientClip(gradient);
            clipVelocity(currentVelocity);

            // Update learning rate and epsilon for gradient approximation
            learningRate = Math.max(MIN_LEARNING_RATE, learningRate * 0.98);
            epsilonGrad = Math.max(MIN_EPSILON_GRAD, epsilonGrad * 0.99);
        }

        return currentVelocity;
    }

    private Vector3 calculateFunction(Vector3 velocity) {
        physicsEngine.setState(AIball.getPosition().x, AIball.getPosition().z, velocity.x, velocity.z);
        double[] afterShot = physicsEngine.runSimulation(velocity.x, velocity.z);
        Vector3 finalPosition = new Vector3((float) afterShot[0], 0, (float) afterShot[1]);
        return new Vector3(finalPosition.x - targetPosition.x, 0, finalPosition.z - targetPosition.z);
    }

    private Vector3 approximateGradient(Vector3 velocity, Vector3 originalDeviation) {
        Vector3 gradient = new Vector3();

        // Perturbation for x velocity
        Vector3 perturbedVelocityX = new Vector3(velocity);
        perturbedVelocityX.x += epsilonGrad;
        Vector3 deviationX = calculateFunction(perturbedVelocityX);
        gradient.x = (float) ((deviationX.len() - originalDeviation.len()) / epsilonGrad);

        // Perturbation for z velocity
        Vector3 perturbedVelocityZ = new Vector3(velocity);
        perturbedVelocityZ.z += epsilonGrad;
        Vector3 deviationZ = calculateFunction(perturbedVelocityZ);
        gradient.z = (float) ((deviationZ.len() - originalDeviation.len()) / epsilonGrad);

        return gradient;
    }

    private void gradientClip(Vector3 gradient) {
        // Clipping the gradient to ensure it doesn't overshoot
        double maxGradient = 2.0; // Example value, adjust as needed
        if (gradient.len() > maxGradient) {
            gradient.scl((float) (maxGradient / gradient.len()));
        }
    }

    private void clipVelocity(Vector3 velocity) {
        // Clipping the velocity to ensure it doesn't overshoot
        double maxVelocity = 70.0; // Example value, adjust as needed
        if (velocity.len() > maxVelocity) {
            velocity.scl((float) (maxVelocity / velocity.len()));
        }
    }

    public void update() {
        Vector3 currentPosition = AIball.getPosition();
        Vector3 currentVelocity = AIball.getVelocity();

        physicsEngine.setState(currentPosition.x, currentPosition.z, currentVelocity.x, currentVelocity.z);
        double[] newState = physicsEngine.runSingleStep(currentPosition, currentVelocity);

        AIball.setPosition(new Vector3((float) newState[0], AIball.getPosition().y, (float) newState[1]));
        AIball.setVelocity(new Vector3((float) newState[2], 0, (float) newState[3]));
        AIball.getPosition().y = (float) (physicsEngine.terrainHeight);
    }
}
