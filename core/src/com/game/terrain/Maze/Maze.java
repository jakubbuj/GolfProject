package com.game.terrain.Maze;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;

import java.util.ArrayList;
import java.util.List;

public class Maze {
    public static List<Wall> walls;

    /**
     * Constructs a Maze object and initializes the maze layout
     */
    public Maze() {
        walls = new ArrayList<>();
        createMaze();
    }

    /**
     * Creates the maze by defining a layout and adding walls accordingly
     */
    private void createMaze() {
        // Define a simple maze layout
        int[][] mazeLayout = MazeLayout.getMazeLayout();
        
        float wallWidth = 1f;
        float wallHeight = 1.5f;
        float wallDepth = 1f;

        for (int z = 0; z < mazeLayout.length; z++) {
            for (int x = 0; x < mazeLayout[z].length; x++) {
                if (mazeLayout[z][x] == 1) {
                    walls.add(new Wall(x * wallWidth, wallHeight / 2, z * wallDepth, wallWidth, wallHeight, wallDepth));
                }
            }
        }
    }

    /**
     * Renders the maze walls
     *
     * @param modelBatch   the ModelBatch used for rendering
     * @param environment  the Environment for the rendering context
     */
    public void render(ModelBatch modelBatch, Environment environment) {
        for (Wall wall : walls) {
            wall.render(modelBatch, environment);
        }
    }

    /**
     * Gets the list of walls in the maze
     *
     * @return the list of walls
     */
    public List<Wall> getWalls() {
        return walls;
    }
}
