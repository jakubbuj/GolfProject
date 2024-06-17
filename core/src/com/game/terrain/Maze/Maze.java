package com.game.terrain.Maze;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;

import java.util.ArrayList;
import java.util.List;

public class Maze {
    public static List<Wall> walls;

    public Maze() {
        walls = new ArrayList<>();
        createMaze();
    }

    private void createMaze() {
        // Define a simple maze layout
        int[][] mazeLayout = {
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 0, 0, 0, 0, 1, 0, 0, 0, 1},
            {1, 0, 1, 1, 0, 1, 0, 1, 0, 1},
            {1, 0, 1, 0, 0, 0, 0, 1, 0, 1},
            {1, 0, 1, 0, 1, 1, 1, 1, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 1, 0, 1},
            {1, 1, 1, 1, 1, 1, 0, 1, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 1, 1, 1, 1, 1, 1, 0, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
        };
        

        float wallWidth = 1.5f;
        float wallHeight = 2.5f;
        float wallDepth = 1.5f;

        for (int z = 0; z < mazeLayout.length; z++) {
            for (int x = 0; x < mazeLayout[z].length; x++) {
                if (mazeLayout[z][x] == 1) {
                    walls.add(new Wall(x * wallWidth, wallHeight / 2, z * wallDepth, wallWidth, wallHeight, wallDepth));
                }
            }
        }
    }

    public void render(ModelBatch modelBatch, Environment environment) {
        for (Wall wall : walls) {
            wall.render(modelBatch, environment);
        }
    }

    public List<Wall> getWalls(){
        return walls;
    }
}
