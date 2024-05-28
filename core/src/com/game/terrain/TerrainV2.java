package com.game.terrain;

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
import com.game.main.SettingsScreen;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

/**
 * The TerrainV2 class represents the terrain in the game, including
 * grass, sand spots, water, and a border. It is responsible for generating and
 * rendering the terrain.
 */
public class TerrainV2 {
    private Model terrainModel; 
    private ModelInstance terrainInstance; 
    private Model waterModel;
    private ModelInstance waterInstance;
    private Model sandModel; 
    private ModelInstance sandInstance; 
    private MapBorder mapBorder;

    // Define size of the terrain
    private int width = 100; 
    private int depth = 100; 
    private float scale = 0.1f; 

    public final Material grassMaterial = new Material(ColorAttribute.createDiffuse(Color.valueOf("228B22")));
                                                                                                   
    public final Material sandMaterial = new Material(ColorAttribute.createDiffuse(Color.valueOf("E1C16E")));
                                                                                                   

    /**
     * Constructs a TerrainV2 object with the specified width, depth, and scale
     * Initializes the terrain and water, and creates a map border
     *
     * @param width  the width of the terrain
     * @param depth  the depth of the terrain
     * @param scale  the scale of the terrain
     */
    public TerrainV2(int width, int depth, float scale) {
        this.width = width;
        this.depth = depth;
        this.scale = scale;
        // initialize the terrain ( grass field )
        addTerrain();

        // Add the water plane after the terrain has been created.
        addWater(0.8f); // adjust the alpha for transparency ( working badly when u have applied texture to water)

        mapBorder = new MapBorder(width, depth, scale);
    }

    /**
     * Returns the width of the terrain
     *
     * @return the width of the terrain
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns the depth of the terrain
     *
     * @return the depth of the terrai
     */
    public int getDepth() {
        return depth;
    }

    /**
     * Returns the scale of the terrain
     *
     * @return the scale of the terrai
     */
    public float getScale() {
        return scale;
    }

    /**
     * Creates and adds the terrain model
     */
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

    /**
     * Generates the terrain by creating vertices and triangles for each tile.
     *
     * @param modelBuilder the  ModelBuilder used to create the terrain
     * @param halfWidth       half the width of the terrain
     * @param halfDepth   half the depth of the terrain
     * @param waterLevel   the height of the water level
     * @param grassTiles    a 2D array to mark grass tiles
     */
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

    /**
     * Determines the material for the terrain based on the height and water level
     *
     * @param height     the height of the terrain at the current position
     * @param waterLevel the height of the water level
     * @param x         the x-coordinate of the current tile
     * @param y        the y-coordinate of the current tile
     * @param grassTiles a 2D array to mark grass tiles
     * @return the material to be used for the current tile
     */
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

    /**
     * Creates vertices and triangles for the current tile
     *
     * @param modelBuilder the ModelBuilder used to create the terrain
     * @param adjustedX    the adjusted x-coordinate of the current tile
     * @param adjustedY    the adjusted y-coordinate of the current tile
     * @param height       the height of the terrain at the current position
     * @param material     the material to be used for the current tile
     * @param x            the x-coordinate of the current tile
     * @param y            the y-coordinate of the current tile
     * @param grassTiles   a 2D array to mark grass tiles
     */
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

    /**
     * Adds a water plane to the terrain with the specified transparency
     *
     * @param alpha the alpha value for the water transparency
     */
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

    /**
     * Returns the height of the sand at the specified coordinates
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @return the height of the sand at the specified coordinates
     */
    public static float getSandHeight(float x, float y) {
        return (float) (Math.sin(x * 0.1f) + Math.cos(y * 0.1f));
    }

    /**
     * Returns the height of the terrain at the specified coordinates
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @return the height of the terrain at the specified coordinates
     */
    private float getHeight(float x, float y) {
        // Compute the expression sqrt((sin(x) + cos(y))^2)
        double result = GetHeight.getHeight(SettingsScreen.terrainFunction, x, y);
        return (float) result; // Convert double to float
    }

    /**
     * Renders the terrain, water, and sand using the given  ModelBatch and  Environment.
     *
     * @param modelBatch  the  ModelBatch used for rendering
     * @param environment the  Environment providing lighting and other effects
     */
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

    /**
     * Disposes of the resources used by the terrain and water models
     */
    public void dispose() {
        terrainModel.dispose();
        if (waterModel != null) {
            waterModel.dispose(); 
        }
    }
}
