package com.game;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;

public class Terrain {
    private Model terrainModel; // model class like schemat
    private ModelInstance terrainInstance; // instance
    private Model waterModel;
    private ModelInstance waterInstance;
    private MapBorder mapBorder;
    private Texture grassTexture;
    private Texture waterTexture;

    private static final String GRASS_TEXTURE_PATH = "assets/grass.jpg";
    private static final String WATER_TEXTURE_PATH = "assets/water.jpg";
    // Define size of the terrain
    private int width = 0; // Number of vertices along the x-axis
    private int depth = 0; // Number of vertices along the y-axis
    private float scale = 0; // Scale of the terrain

    public Terrain(int width, int depth, float scale) {
        this.width = width;
        this.depth = depth;
        this.scale = scale;

        // initialize the terrain ( grass field )
        addGrass();

        // Add the water plane after the terrain has been created.
        addWater(0.8f); // You can adjust the alpha for transparency

        // Add map border
        mapBorder = new MapBorder(width, depth, scale);
    }

    // method add grass
    public void addGrass() {
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();

        // Load the grass texture
        grassTexture = new Texture(GRASS_TEXTURE_PATH);

        // Compute half width and depth to center the terrain
        float halfWidth = width * scale * 0.5f;
        float halfDepth = depth * scale * 0.5f;

        Material material = new Material(TextureAttribute.createDiffuse(grassTexture));
        MeshPartBuilder mpb = modelBuilder.part("terrain", GL20.GL_TRIANGLES,
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | Usage.TextureCoordinates, material);

        // Generate a grid of vertices for the terrain
        for (int y = 0; y < depth - 1; y++) {
            for (int x = 0; x < width - 1; x++) {
                // Adjusted to center the terrain
                float adjustedX = (x * scale) - halfWidth;
                float adjustedY = (y * scale) - halfDepth;

                // Get the heights of the corners
                float height00 = getHeight(adjustedX, adjustedY);
                float height01 = getHeight(adjustedX, adjustedY + scale);
                float height10 = getHeight(adjustedX + scale, adjustedY);
                float height11 = getHeight(adjustedX + scale, adjustedY + scale);

                // Create vectors at point x,y,z
                Vector3 z00 = new Vector3(adjustedX, height00, adjustedY);
                Vector3 z01 = new Vector3(adjustedX, height01, adjustedY + scale);
                Vector3 z10 = new Vector3(adjustedX + scale, height10, adjustedY);
                Vector3 z11 = new Vector3(adjustedX + scale, height11, adjustedY + scale);

                // Create 2 triangles from those 4 vectors
                mpb.triangle(z01, z10, z00);
                mpb.triangle(z10, z01, z11);
            }
        }

        terrainModel = modelBuilder.end();
        terrainInstance = new ModelInstance(terrainModel);
    }

    // Method to add water
    public void addWater(float alpha) {
        ModelBuilder modelBuilder = new ModelBuilder();

        // Load the water texture
        waterTexture = new Texture(WATER_TEXTURE_PATH);

        float halfWidth = width * scale * 0.5f;
        float halfDepth = depth * scale * 0.5f;

        // Create water material with texture
        Material material = new Material(
                TextureAttribute.createDiffuse(waterTexture),
                new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA));

        waterModel = modelBuilder.createRect(
                -halfWidth, 0, halfDepth,
                halfWidth, 0, halfDepth,
                halfWidth, 0, -halfDepth,
                -halfWidth, 0, -halfDepth,
                0, 1, 0,
                material,
                Usage.Position | Usage.Normal | Usage.TextureCoordinates);

        waterInstance = new ModelInstance(waterModel);
    }

    private float getHeight(float x, float z) {

        // This is a simple example using a sine function for the height
        return (float) GetHeight.getHeight(GameControl.functionTerrain, x, z);
    }

    public void render(ModelBatch modelBatch, Environment environment) {
        modelBatch.render(terrainInstance, environment);
        modelBatch.render(mapBorder.getBorderInstance(), environment);
        modelBatch.render(waterInstance, environment);
    }

    public void dispose() {
        terrainModel.dispose();
        if (waterModel != null) {
            waterModel.dispose();
        }
        if (grassTexture != null) {
            grassTexture.dispose();
        }
        if (waterTexture != null) {
            waterTexture.dispose();
        }
        mapBorder.dispose();
    }
}