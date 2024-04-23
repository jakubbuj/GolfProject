package com.game;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

public class TerrainV2 {
    private Model terrainModel; // model class like schemat
    private ModelInstance terrainInstance; // instance
    private Model waterModel;
    private ModelInstance waterInstance;
    private Model sandModel; // Model for sand spots
    private ModelInstance sandInstance; // Instance for rendering sand spots

    // Define size of the terrain
    private int width = 100; // Number of vertices along the x-axis
    private int depth = 100; // Number of vertices along the y-axis
    private float scale = 0.1f; // Scale of the terrain

    public TerrainV2() {

        // initialize the terrain ( grass field )
        addTerrain();

        // Add the water plane after the terrain has been created.
        addWater(0.8f); // You can adjust the alpha for transparency

    }

    public void addTerrain() {
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();

        // Generate the terrain with grass, sand, and earth
        generateTerrain(modelBuilder);

        terrainModel = modelBuilder.end();
        terrainInstance = new ModelInstance(terrainModel);
    }

    private void generateTerrain(ModelBuilder modelBuilder) {
        // Compute half width and depth to center the terrain
        float halfWidth = width * scale * 0.5f;
        float halfDepth = depth * scale * 0.5f;

        // Get the height of the water level
        float waterLevel = getHeight(0, 0); // Assuming water level at the center

        // Boolean arrays to track grass and sand tiles
        boolean[][] grassTiles = new boolean[width][depth];
        boolean[][] sandTiles = new boolean[width][depth];

        // Generate the terrain with grass, sand, and earth
        for (int y = 0; y < depth - 1; y++) {
            for (int x = 0; x < width - 1; x++) {
                // Adjusted to center the terrain
                float adjustedX = (x * scale) - halfWidth;
                float adjustedY = (y * scale) - halfDepth;

                // Get the height of the terrain at this point
                float height = getHeight(adjustedX, adjustedY);

                // Material variable declaration
                Material material;

                if (height <= waterLevel) {
                    material = createEarthMaterial();
                } else if (checkSandNearby(sandTiles, x, y)) {
                    continue; // Skip generating grass if sand is nearby
                } else if (Math.random() < 0.003) {
                    material = createSandMaterial(modelBuilder, adjustedX, adjustedY);
                    propagateSand(sandTiles, modelBuilder, x, y, adjustedX, adjustedY);
                } else {
                    material = createGrassMaterial();
                    grassTiles[x][y] = true; // Mark this tile as grass
                }

                // Method call to add terrain part
                addTerrainPart(modelBuilder, material, adjustedX, adjustedY);
            }
        }
    }

    private boolean checkSandNearby(boolean[][] sandTiles, int x, int y) {
        // Check neighboring tiles within the range of the patch size of sand
        int patchSize = 2; // Define the patch size of sand

        for (int dx = -patchSize; dx <= patchSize; dx++) {
            for (int dy = -patchSize; dy <= patchSize; dy++) {
                int nx = x + dx;
                int ny = y + dy;

                // Check if the neighboring tile is within bounds and contains sand
                if (nx >= 0 && nx < width && ny >= 0 && ny < depth && sandTiles[nx][ny]) {
                    return true; // Sand found nearby
                }
            }
        }

        return false; // No sand found nearby
    }

    private Material createGrassMaterial() {
        return new Material(ColorAttribute.createDiffuse(Color.GREEN));
    }

    private Material createEarthMaterial() {
        return new Material(ColorAttribute.createDiffuse(new Color(0.5f, 0.3f, 0.1f, 1.0f)));
    }

    private Material createSandMaterial(ModelBuilder modelBuilder, float adjustedX, float adjustedY) {
        // Adjust sand size
        float patchSize = scale * 2.0f; // Double the size of sand patches

        Vector3 bottomLeft = new Vector3(adjustedX - patchSize, getHeight(adjustedX - patchSize, adjustedY - patchSize),
                adjustedY - patchSize);
        Vector3 bottomRight = new Vector3(adjustedX + scale + patchSize,
                getHeight(adjustedX + scale + patchSize, adjustedY - patchSize), adjustedY - patchSize);
        Vector3 topLeft = new Vector3(adjustedX - patchSize,
                getHeight(adjustedX - patchSize, adjustedY + scale + patchSize), adjustedY + scale + patchSize);
        Vector3 topRight = new Vector3(adjustedX + scale + patchSize,
                getHeight(adjustedX + scale + patchSize, adjustedY + scale + patchSize), adjustedY + scale + patchSize);

        // Material variable declaration
        Material sandMaterial = new Material(ColorAttribute.createDiffuse(Color.YELLOW)); // Use yellow color for sand

        MeshPartBuilder mpb = modelBuilder.part("terrain", GL20.GL_TRIANGLES,
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, sandMaterial);
        mpb.rect(topLeft, topRight, bottomRight, bottomLeft, new Vector3(0, 1, 0)); // Use rect for larger sand patches

        return sandMaterial;
    }

    private void propagateSand(boolean[][] grassTiles, ModelBuilder modelBuilder, int x, int y, float adjustedX,
            float adjustedY) {
        // Propagate sand to neighboring tiles (up, down, left, right)
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                int nx = x + dx;
                int ny = y + dy;

                // Check if the neighboring tile is within bounds, not already sand or grass
                if (nx >= 0 && nx < width && ny >= 0 && ny < depth && !grassTiles[nx][ny]) {
                    // Assign sand material to neighboring tile
                    createSandMaterial(modelBuilder, adjustedX + dx * scale, adjustedY + dy * scale);
                    grassTiles[nx][ny] = true; // Mark this neighboring tile as sand
                }
            }
        }
    }

    private void addTerrainPart(ModelBuilder modelBuilder, Material material, float adjustedX, float adjustedY) {
        // Create vertices for the square mesh at this point
        Vector3 bottomLeft = new Vector3(adjustedX, getHeight(adjustedX, adjustedY), adjustedY);
        Vector3 bottomRight = new Vector3(adjustedX + scale, getHeight(adjustedX + scale, adjustedY), adjustedY);
        Vector3 topLeft = new Vector3(adjustedX, getHeight(adjustedX, adjustedY + scale), adjustedY + scale);
        Vector3 topRight = new Vector3(adjustedX + scale, getHeight(adjustedX + scale, adjustedY + scale),
                adjustedY + scale);

        // Create triangles for the square mesh
        MeshPartBuilder mpb = modelBuilder.part("terrain", GL20.GL_TRIANGLES,
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material);
        mpb.triangle(topLeft, bottomRight, bottomLeft); // Triangle 1
        mpb.triangle(topLeft, topRight, bottomRight); // Triangle 2
    }

    // Method to add water
    public void addWater(float alpha) {
        ModelBuilder modelBuilder = new ModelBuilder();

        float halfWidth = width * scale * 0.5f;
        float halfDepth = depth * scale * 0.5f;

        waterModel = modelBuilder.createRect(
                -halfWidth, 0, halfDepth,
                halfWidth, 0, halfDepth,
                halfWidth, 0, -halfDepth,
                -halfWidth, 0, -halfDepth,
                0, 1, 0,
                new Material(
                        ColorAttribute.createDiffuse(new Color(0, 0, 1, alpha)),
                        new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)),
                Usage.Position | Usage.Normal);

        waterInstance = new ModelInstance(waterModel);
    }

    private float getHeight(float x, float y) {
        // This is a simple example using a sine function for the height
        return (float) (0.4 * (0.9 - Math.exp(-Math.pow(x, 2) / 8 + Math.pow(y, 2) / 8)));
    }

    public void render(ModelBatch modelBatch, Environment environment) {
        // Render the terrain
        modelBatch.render(terrainInstance, environment);

        // Enable blending for transparent objects
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        // Render the water
        modelBatch.render(waterInstance, environment);

        // Render the sand spots
        if (sandInstance != null) {
            modelBatch.render(sandInstance, environment);
        }

        // Disable blending
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    public void dispose() {
        terrainModel.dispose();
        if (waterModel != null) {
            waterModel.dispose(); // Dispose of the waterModel resources
        }
    }
}
