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

    /**
     * Constructs a TerrainV2 object with the specified width, depth, and scale
     * @param width the width of the terrain
     * @param depth the depth of the terrain
     * @param scale the scale of the terrain
     */
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

    /**
     * Gets the width of the terrain
     * @return the width of the terrain
     */
    public int getWidth() {
        return width;
    }

    /**
     * Gets the depth of the terrain
     * @return the depth of the terrain
     */
    public int getDepth() {
        return depth;
    }

    /**
     * Gets the scale of the terrain
     * @return the scale of the terrain
     */
    public float getScale() {
        return scale;
    }

    /**
     * Renders the terrain, water plane, and optionally the sand instance
     * @param modelBatch the ModelBatch used for rendering
     * @param environment the Environment for the rendering context
     */
    public void render(ModelBatch modelBatch, Environment environment) {
        terrainGenerator.render(modelBatch, environment);
        waterPlane.render(modelBatch, environment);

        if (sandInstance != null) {
            modelBatch.render(sandInstance, environment);
        }
    }

    /**
     * Disposes of the terrain resources
     */
    public void dispose() {
        terrainGenerator.dispose();
        waterPlane.dispose();
    }
}
