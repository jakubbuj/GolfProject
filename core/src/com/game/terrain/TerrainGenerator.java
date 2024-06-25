package com.game.terrain;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.g3d.Environment;

public class TerrainGenerator {
    private Model terrainModel;
    private ModelInstance terrainInstance;
    private int width;
    private int depth;
    private float scale;
    private TerrainHeightCalculator heightCalculator;
    private MaterialFactory materialFactory;

    /**
     * Constructs a TerrainGenerator with the specified width, depth, and scale
     * @param width the width of the terrain
     * @param depth the depth of the terrain
     * @param scale the scale of the terrain
     */
    public TerrainGenerator(int width, int depth, float scale) {
        this.width = width;
        this.depth = depth;
        this.scale = scale;
        this.heightCalculator = new TerrainHeightCalculator();
        this.materialFactory = new MaterialFactory();

        addTerrain();
    }

    /**
     * Creates the terrain model instance
     * @return the created terrain model instance
     */
    public ModelInstance createTerrain() {
        return terrainInstance;
    }

    /**
     * Adds the terrain by generating the terrain model
     */
    private void addTerrain() {
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();

        float halfWidth = width * scale * 0.5f;
        float halfDepth = depth * scale * 0.5f;

        float waterLevel = heightCalculator.getHeight(0, 0);

        boolean[][] grassTiles = new boolean[width][depth];

        generateTerrain(modelBuilder, halfWidth, halfDepth, waterLevel, grassTiles);

        terrainModel = modelBuilder.end();
        terrainInstance = new ModelInstance(terrainModel);
    }

    /**
     * Generates the terrain by creating vertices and triangles for the terrain mesh
     * @param modelBuilder the ModelBuilder used for creating the terrain
     * @param halfWidth half the width of the terrain
     * @param halfDepth half the depth of the terrain
     * @param waterLevel the water level of the terrain
     * @param grassTiles a boolean array to determine grass tile placement
     */
    private void generateTerrain(ModelBuilder modelBuilder, float halfWidth, float halfDepth, float waterLevel,
                                 boolean[][] grassTiles) {
        for (int y = 0; y < depth - 1; y++) {
            for (int x = 0; x < width - 1; x++) {
                float adjustedX = (x * scale) - halfWidth;
                float adjustedY = (y * scale) - halfDepth;

                float height = heightCalculator.getHeight(adjustedX, adjustedY);

                Material material = materialFactory.determineMaterial(height, waterLevel, x, y, grassTiles);

                createVerticesAndTriangles(modelBuilder, adjustedX, adjustedY, height, material, x, y, grassTiles);
            }
        }
    }

    /**
     * Creates vertices and triangles for the terrain mesh
     * @param modelBuilder the ModelBuilder used for creating the terrain
     * @param adjustedX the adjusted x-coordinate
     * @param adjustedY the adjusted y-coordinate
     * @param height the height at the given coordinates
     * @param material the material used for the terrain
     * @param x the x-coordinate index in the grid
     * @param y the y-coordinate index in the grid
     * @param grassTiles a boolean array to determine grass tile placement
     */
    private void createVerticesAndTriangles(ModelBuilder modelBuilder, float adjustedX, float adjustedY, float height,
                                            Material material, int x, int y, boolean[][] grassTiles) {
        Vector3 bottomLeft = new Vector3(adjustedX, height, adjustedY);
        Vector3 bottomRight = new Vector3(adjustedX + scale, heightCalculator.getHeight(adjustedX + scale, adjustedY), adjustedY);
        Vector3 topLeft = new Vector3(adjustedX, heightCalculator.getHeight(adjustedX, adjustedY + scale), adjustedY + scale);
        Vector3 topRight = new Vector3(adjustedX + scale, heightCalculator.getHeight(adjustedX + scale, adjustedY + scale),
                adjustedY + scale);

        MeshPartBuilder mpb = modelBuilder.part("terrain", GL20.GL_TRIANGLES,
                Usage.Position | Usage.Normal, material);
        mpb.triangle(topLeft, bottomRight, bottomLeft);
        mpb.triangle(topLeft, topRight, bottomRight);
    }

    /**
     * Renders the terrain model instance
     * @param modelBatch the ModelBatch used for rendering
     * @param environment the Environment for the rendering context
     */
    public void render(ModelBatch modelBatch, Environment environment) {
        modelBatch.render(terrainInstance, environment);
    }

    /**
     * Disposes of the terrain model resources
     */
    public void dispose() {
        terrainModel.dispose();
    }
}
