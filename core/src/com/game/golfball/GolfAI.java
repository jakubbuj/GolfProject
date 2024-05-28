package com.game.golfball;

import com.badlogic.gdx.math.Vector3;
import com.game.terrain.GameRules;

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
    private static final double TOLERANCE = 0.4; // Tolerance for stopping condition
    private static final double BETA1 = 0.9; // Decay rate for the first moment estimate
    private static final double BETA2 = 0.999; // Decay rate for the second moment estimate

    private Vector3 m; // First moment vector
    private Vector3 v; // Second moment vector
    private double learningRate; // Current learning rate
    private double epsilonGrad; // Current epsilon for gradient approximation
    private int t; // Time step
    private GameRules gameRules; // Game rules

    /**
     * Constructs a GolfAI object with the specified parameters
     *
     * @param AIball         The golf ball controlled by the AI
     * @param targetPosition The target position for the golf ball
     * @param physicsEngine  The physics engine used for simulations
     * @param gameRules      The game rules defining constraints and objectives
     */
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

    /**
     * Calculates the best shot ( figure out perfect velocity ) for the golf ball to reach the target position
     *
     * @return The velocity vector representing the best shot
     */

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

            t++;

            m.x = (float) (BETA1 * m.x + (1 - BETA1) * gradient.x);
            m.z = (float) (BETA1 * m.z + (1 - BETA1) * gradient.z);

            v.x = (float) (BETA2 * v.x + (1 - BETA2) * gradient.x * gradient.x);
            v.z = (float) (BETA2 * v.z + (1 - BETA2) * gradient.z * gradient.z);


            float mHatX = (float) (m.x / (1 - Math.pow(BETA1, t)));
            float mHatZ = (float) (m.z / (1 - Math.pow(BETA1, t)));


            float vHatX = (float) (v.x / (1 - Math.pow(BETA2, t)));
            float vHatZ = (float) (v.z / (1 - Math.pow(BETA2, t)));

            // Update velocity with momentum
            currentVelocity.x -= learningRate * mHatX / (Math.sqrt(vHatX) + EPSILON_ADAM);
            currentVelocity.z -= learningRate * mHatZ / (Math.sqrt(vHatZ) + EPSILON_ADAM);

            //gradient and velocity clipping to prevent overshooting
            gradientClip(gradient);
            clipVelocity(currentVelocity);

            //Update learning rate and epsilon for gradient approximation
            learningRate = Math.max(MIN_LEARNING_RATE, learningRate * 0.98);
            epsilonGrad = Math.max(MIN_EPSILON_GRAD, epsilonGrad * 0.99);
        }

        return currentVelocity;
    }

    /**
     * Calculates   function representing the deviation between the current shot
     * and the target position.
     *
     * @param velocity The velocity vector representing the current shot
     * @return The deviation vector between the current shot and the target position.
     */
    private Vector3 calculateFunction(Vector3 velocity) {
        physicsEngine.setState(AIball.getPosition().x, AIball.getPosition().z, velocity.x, velocity.z);
        double[] afterShot = physicsEngine.runSimulation(velocity.x, velocity.z);
        Vector3 finalPosition = new Vector3((float) afterShot[0], 0, (float) afterShot[1]);
        return new Vector3(finalPosition.x - targetPosition.x, 0, finalPosition.z - targetPosition.z);
    }

    /**
     * Approximates the gradient of the deviation function with respect to the velocity ( we do it this way to and not by caclulating derivative of distance function cuz we need to take terrain somehow into account)
     *
     * @param velocity         The velocity vector representing the current shot
     * @param originalDeviation The original deviation between the current shot and the target position
     * @return The gradient vector approximating the rate of change of deviation with respect to velocity
     */
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

    /**
     * Clips the gradient vector to prevent overshooting during optimization
     *
     * @param gradient  gradient vector to be clipped
     */
    private void gradientClip(Vector3 gradient) {
        // Clipping the gradient to ensure it doesn't overshoot
        double maxGradient = 2.0; // Example value, adjust as needed
        if (gradient.len() > maxGradient) {
            gradient.scl((float) (maxGradient / gradient.len()));
        }
    }

    /**
     * Clips the velocity vector to prevent overshooting during optimization
     *
     * @param velocity The velocity vector to be clipped
     */
    private void clipVelocity(Vector3 velocity) {

        double maxVelocity = 70.0; 
        if (velocity.len() > maxVelocity) {
            velocity.scl((float) (maxVelocity / velocity.len()));
        }
    }

    /**
     * Updates the position and velocity of the golf ball according to the calculated shot (this is the shot we see on screan so it moves the  ball)
     */
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
