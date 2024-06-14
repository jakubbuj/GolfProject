package com.game.golfball;

import com.badlogic.gdx.math.Vector3;
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
     * Node class for A* search
     */

    public class Node {
        Vector3 position;
        double gScore;
        double fScore;

        Node(Vector3 position, double gScore, double fScore) {
            this.position = position;
            this.gScore = gScore;
            this.fScore = fScore;
        }

        double getFScore() {
            return fScore;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return position.equals(node.position);
        }

        @Override
        public int hashCode() {
            return Objects.hash(position);
        }
    }

    /**
     * A* Search to find the best shot
     *
     * @return The velocity vector representing the best shot
     */

    public Vector3 findBestShot() {
        Vector3 startPosition = AIball.getPosition();
        Node startNode = new Node(startPosition, 0, heuristic(startPosition));

        openSet.add(startNode);
        gScore.put(startNode, 0.0);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();

            if (isTargetReached(current.position)) {
                return reconstructPath(current);
            }

            closedSet.add(current);

            for (Vector3 shot : generatePossibleShots(current.position)) {
                Vector3 newPosition = calculateNewPosition(current.position, shot);
                if (isOutOfBounds(newPosition)) continue;

                Node neighbor = new Node(newPosition, 0, 0);
                if (closedSet.contains(neighbor)) continue;

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
        }

        return new Vector3(); // Return zero vector if no path is found
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

        // Generate shots with varying velocities and directions, scaled by the distance to target
        for (float vx = (float) -physicsEngine.maxVelocity / (float) scalingFactor;
             vx <= physicsEngine.maxVelocity / (float) scalingFactor;
             vx += 1) {
            for (float vz = (float) -physicsEngine.maxVelocity / (float) scalingFactor;
                 vz <= physicsEngine.maxVelocity / (float) scalingFactor;
                 vz += 1) {
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
     * @return The velocity vector representing the best shot
     */
    
    private Vector3 reconstructPath(Node current) {
        Node previous = cameFrom.get(current);
        if (previous == null) {
            return new Vector3(); // Handles case where path reconstruction fails
        }
        return bestShot.get(previous);
    }
}