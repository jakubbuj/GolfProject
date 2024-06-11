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

    public TerrainGenerator(int width, int depth, float scale) {
        this.width = width;
        this.depth = depth;
        this.scale = scale;
        this.heightCalculator = new TerrainHeightCalculator();
        this.materialFactory = new MaterialFactory();

        addTerrain();
    }

    public ModelInstance createTerrain() {
        return terrainInstance;
    }

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

    public void render(ModelBatch modelBatch, Environment environment) {
        modelBatch.render(terrainInstance, environment);
    }

    public void dispose() {
        terrainModel.dispose();
    }
}
