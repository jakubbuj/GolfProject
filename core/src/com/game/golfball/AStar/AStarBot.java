package com.game.golfball.AStar;

import com.badlogic.gdx.math.Vector3;
import com.game.golfball.GolfBall;
import com.game.golfball.PhysicsEngine;
import com.game.terrain.GameRules;
import java.util.*;

public class AStarBot {
    private static Vector3 targetPosition;
    private PhysicsEngine physicsEngine;
    private GolfBall AIball;
    private GameRules gameRules;

    // A* specific variables
    private PriorityQueue<Node> openSet;
    private Set<Node> closedSet;
    private Map<Node, Double> gScore;
    private Map<Node, Node> cameFrom;
    private Map<Node, Vector3> bestShot;

    /**
     * Constructs an AStarBot object with the specified parameters
     *
     * @param AIball         The golf ball controlled by the AI
     * @param targetPosition The target position for the golf ball
     * @param physicsEngine  The physics engine used for simulations
     * @param gameRules      The game rules defining constraints and objectives
     */
    public AStarBot(GolfBall AIball, Vector3 targetPosition, PhysicsEngine physicsEngine, GameRules gameRules) {
        this.AIball = AIball;
        AStarBot.targetPosition = targetPosition;
        this.physicsEngine = physicsEngine;
        if (gameRules == null) {
            throw new IllegalArgumentException("gameRules cannot be null");
        }
        this.gameRules = gameRules;

        // Initialize A* specific variables
        this.openSet = new PriorityQueue<>(Comparator.comparingDouble(Node::getFScore));
        this.closedSet = new HashSet<>();
        this.gScore = new HashMap<>();
        this.cameFrom = new HashMap<>();
        this.bestShot = new HashMap<>();
    }

    /**
     * A* Search to find the best path
     *
     * @return The best path as a list of velocity vectors
     */
    public List<Vector3> findBestPath() {
        Vector3 startPosition = AIball.getPosition();
        Node startNode = new Node(startPosition, 0, heuristic(startPosition));

        openSet.add(startNode);
        gScore.put(startNode, 0.0);

        int iterations = 0;
        final int MAX_ITERATIONS = 10000; // Prevent long computations

        while (!openSet.isEmpty() && iterations < MAX_ITERATIONS) {
            Node current = openSet.poll();

            if (isTargetReached(current.position)) {
                return reconstructPath(current);
            }

            closedSet.add(current);

            for (Vector3 shot : generatePossibleShots(current.position)) {
                Vector3 newPosition = calculateNewPosition(current.position, shot);
                if (isOutOfBounds(newPosition))
                    continue;

                Node neighbor = new Node(newPosition, 0, 0);
                if (closedSet.contains(neighbor))
                    continue;

                double tentativeGScore = gScore.getOrDefault(current, Double.MAX_VALUE) + shot.len();

                if (!openSet.contains(neighbor)) {
                    openSet.add(neighbor);
                } else if (tentativeGScore >= gScore.getOrDefault(neighbor, Double.MAX_VALUE)) {
                    continue;
                }

                cameFrom.put(neighbor, current);
                bestShot.put(neighbor, shot);
                gScore.put(neighbor, tentativeGScore);
                neighbor.gScore = tentativeGScore;
                neighbor.fScore = tentativeGScore + heuristic(neighbor.position);
            }

            iterations++;
        }

        // If no path is found within the iteration limit, return an empty list
        return Collections.emptyList();
    }

    /**
     * Generates possible shots from the current position
     *
     * @param position The current position
     * @return A list of possible velocity vectors
     */
    private List<Vector3> generatePossibleShots(Vector3 position) {
        List<Vector3> shots = new ArrayList<>();

        double distanceToTarget = heuristic(position);
        double scalingFactor = Math.max(1, distanceToTarget / 10); // arbitrary scaling factor (adjust as necessary)

        // Generate shots with varying velocities and directions, scaled by the distance
        // to target
        for (float vx = (float) -physicsEngine.maxVelocity / (float) scalingFactor; vx <= physicsEngine.maxVelocity
                / (float) scalingFactor; vx += 1) {
            for (float vz = (float) -physicsEngine.maxVelocity / (float) scalingFactor; vz <= physicsEngine.maxVelocity
                    / (float) scalingFactor; vz += 1) {
                shots.add(new Vector3(vx, 0, vz));
            }
        }
        return shots;
    }

    /**
     * Calculates the new position of the ball after taking a shot
     *
     * @param position The current position
     * @param velocity The velocity vector representing the shot
     * @return The new position after the shot
     */
    private Vector3 calculateNewPosition(Vector3 position, Vector3 velocity) {
        physicsEngine.setState(position.x, position.z, velocity.x, velocity.z);
        double[] afterShot = physicsEngine.runSimulation(velocity.x, velocity.z);
        return new Vector3((float) afterShot[0], 0, (float) afterShot[1]);
    }

    /**
     * Heuristic function to estimate the distance to the target
     *
     * @param position The current position
     * @return The estimated distance to the target
     */
    private double heuristic(Vector3 position) {
        return position.dst(targetPosition);
    }

    /**
     * Checks if the target position is reached
     *
     * @param position The current position
     * @return True if the target is reached, false otherwise
     */
    private boolean isTargetReached(Vector3 position) {
        return gameRules.isGameOver();
    }

    /**
     * Checks if the position is out of bounds or in water
     *
     * @param position The position to check
     * @return True if the position is out of bounds or in water, false otherwise
     */
    private boolean isOutOfBounds(Vector3 position) {
        // Implement game-specific bounds checking
        return gameRules.outOfBorder() || gameRules.fellInWater();
    }

    /**
     * Reconstructs the path to the target
     *
     * @param current The current node
     * @return The path as a list of velocity vectors
     */
    private List<Vector3> reconstructPath(Node current) {
        List<Vector3> path = new ArrayList<>();
        while (current != null) {
            path.add(bestShot.get(current));
            current = cameFrom.get(current);
        }
        Collections.reverse(path); // Reverse the path to start from the initial position
        return path;
    }

    /**
     * Updates the position of the AI ball along the optimized path
     */
    public void update(List<Vector3> path) {
        if (path == null || path.isEmpty())
            return;

        Vector3 nextShot = path.remove(0);
        AIball.setVelocity(nextShot);
        physicsEngine.setState(AIball.getPosition().x, AIball.getPosition().z, nextShot.x, nextShot.z);
        double[] newState = physicsEngine.runSingleStep(AIball.getPosition(), nextShot);

        AIball.setPosition(new Vector3((float) newState[0], AIball.getPosition().y, (float) newState[1]));
        AIball.setVelocity(new Vector3((float) newState[2], 0, (float) newState[3]));
    }
}
