package com.game.terrain;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.game.main.SettingsScreen;

public class TerrainV2 {
    private ModelInstance terrainInstance;
    private ModelInstance waterInstance;
    private ModelInstance sandInstance;
    private MapBorder mapBorder;
    private TerrainGenerator terrainGenerator;
    private WaterPlane waterPlane;

    // Define size of the terrain
    private int width;
    private int depth;
    private float scale;

    public TerrainV2(int width, int depth, float scale) {
        this.width = width;
        this.depth = depth;
        this.scale = scale;

        terrainGenerator = new TerrainGenerator(width, depth, scale);
        terrainInstance = terrainGenerator.createTerrain();

        waterPlane = new WaterPlane(width, depth, scale);
        waterInstance = waterPlane.createWater(0.8f);

        mapBorder = new MapBorder(width, depth, scale);
    }

    public int getWidth() {
        return width;
    }

    public int getDepth() {
        return depth;
    }

    public float getScale() {
        return scale;
    }

    public void render(ModelBatch modelBatch, Environment environment) {
        terrainGenerator.render(modelBatch, environment);
        waterPlane.render(modelBatch, environment);

        if (sandInstance != null) {
            modelBatch.render(sandInstance, environment);
        }
    }

    public void dispose() {
        terrainGenerator.dispose();
        waterPlane.dispose();
    }
}
