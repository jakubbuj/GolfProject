package com.game.golfball.AStar;

import com.badlogic.gdx.math.Vector3;
import com.game.terrain.Maze.MazeLayout;

import java.util.*;

public class AStarMazeSolver {
    private int[][] maze;
    private Node start;
    private Node goal;

    public AStarMazeSolver(Vector3 start, Vector3 goal) {
        this.maze = MazeLayout.getMazeLayout();
        this.start = new Node((int) start.x, (int) start.z);
        this.goal = new Node((int) goal.x, (int) goal.z);
    }

    public List<Node> findBestPath() {
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(Node::getFScore));
        Set<Node> closedSet = new HashSet<>();
        Map<Node, Double> gScore = new HashMap<>();
        Map<Node, Node> cameFrom = new HashMap<>();

        openSet.add(start);
        gScore.put(start, 0.0);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();

            if (current.equals(goal)) {
                return reconstructPath(cameFrom, current);
            }

            closedSet.add(current);

            for (Node neighbor : getNeighbors(current)) {
                if (closedSet.contains(neighbor) || !MazeLayout.isWalkable(neighbor.x, neighbor.y)) {
                    continue;
                }

                double tentativeGScore = gScore.getOrDefault(current, Double.MAX_VALUE) + 1;

                if (!openSet.contains(neighbor) || tentativeGScore < gScore.getOrDefault(neighbor, Double.MAX_VALUE)) {
                    cameFrom.put(neighbor, current);
                    gScore.put(neighbor, tentativeGScore);
                    neighbor.gScore = tentativeGScore;
                    neighbor.fScore = tentativeGScore + heuristic(neighbor, goal);

                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    }
                }
            }
        }

        return Collections.emptyList();
    }

    private List<Node> getNeighbors(Node node) {
        List<Node> neighbors = new ArrayList<>();
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

        for (int[] dir : directions) {
            int newX = node.x + dir[0];
            int newY = node.y + dir[1];
            if (newX >= 0 && newX < maze.length && newY >= 0 && newY < maze[0].length) {
                neighbors.add(new Node(newX, newY));
            }
        }

        return neighbors;
    }

    private double heuristic(Node a, Node b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }

    private List<Node> reconstructPath(Map<Node, Node> cameFrom, Node current) {
        List<Node> path = new ArrayList<>();
        while (current != null) {
            path.add(current);
            current = cameFrom.get(current);
        }
        Collections.reverse(path);
        return path;
    }
}
