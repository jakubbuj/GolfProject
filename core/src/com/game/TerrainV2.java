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
    private MapBorder mapBorder;

    // Define size of the terrain
    private int width = 100; // Number of vertices along the x-axis
    private int depth = 100; // Number of vertices along the y-axis
    private float scale = 0.1f; // Scale of the terrain

    public final Material grassMaterial = new Material(ColorAttribute.createDiffuse(Color.GREEN)); // Use green color
                                                                                                   // for grass
    public final Material sandMaterial = new Material(ColorAttribute.createDiffuse(Color.YELLOW)); // Use yellow color
                                                                                                   // for sand

    public TerrainV2(int width, int depth, float scale) {
        this.width = width;
        this.depth = depth;
        this.scale = scale;
        // initialize the terrain ( grass field )
        addTerrain();

        // Add the water plane after the terrain has been created.
        addWater(0.8f); // You can adjust the alpha for transparency

        mapBorder = new MapBorder(width, depth, scale);
    }

    // Getter methods for terrain dimensions
    public int getWidth() {
        return width;
    }

    public int getDepth() {
        return depth;
    }

    public float getScale() {
        return scale;
    }

    public void addTerrain() {
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();

        float halfWidth = width * scale * 0.5f;
        float halfDepth = depth * scale * 0.5f;

        float waterLevel = getHeight(0, 0);

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

                float height = getHeight(adjustedX, adjustedY);

                Material material = determineMaterial(height, waterLevel, x, y, grassTiles);

                createVerticesAndTriangles(modelBuilder, adjustedX, adjustedY, height, material, x, y, grassTiles);
            }
        }
    }

    private Material determineMaterial(float height, float waterLevel, int x, int y, boolean[][] grassTiles) {
        Material material;
        float sandHeight = getSandHeight(x, y);

        if (sandHeight > 0.5) {
            material = sandMaterial;
        } else {
            material = grassMaterial;
            grassTiles[x][y] = true;
        }
        return material;
    }

    

    private void createVerticesAndTriangles(ModelBuilder modelBuilder, float adjustedX, float adjustedY, float height,
            Material material, int x, int y, boolean[][] grassTiles) {
        Vector3 bottomLeft = new Vector3(adjustedX, height, adjustedY);
        Vector3 bottomRight = new Vector3(adjustedX + scale, getHeight(adjustedX + scale, adjustedY), adjustedY);
        Vector3 topLeft = new Vector3(adjustedX, getHeight(adjustedX, adjustedY + scale), adjustedY + scale);
        Vector3 topRight = new Vector3(adjustedX + scale, getHeight(adjustedX + scale, adjustedY + scale),
                adjustedY + scale);

        MeshPartBuilder mpb = modelBuilder.part("terrain", GL20.GL_TRIANGLES,
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material);
        mpb.triangle(topLeft, bottomRight, bottomLeft);
        mpb.triangle(topLeft, topRight, bottomRight);
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

    protected float getSandHeight(float x, float y) {
        return (float) (Math.sin(x * 0.1f) + Math.cos(y * 0.1f));
    }

    private float getHeight(float x, float y) {
        // Compute the expression sqrt((sin(x) + cos(y))^2)
        double result = Math.sqrt(Math.pow(Math.sin(x) + Math.cos(y), 2));
        return (float) result; // Convert double to float
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
