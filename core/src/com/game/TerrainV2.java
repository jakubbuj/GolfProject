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
import java.util.Random;

public class TerrainV2 {
    private Model terrainModel;  // model class like schemat
    private ModelInstance terrainInstance;  // instance
    private Model waterModel;
    private ModelInstance waterInstance;
    private Model sandModel;  // Model for sand spots
    private ModelInstance sandInstance;  // Instance for rendering sand spots
    private MapBorder mapBorder;

    // Define size of the terrain
    private int width = 100;  // Number of vertices along the x-axis
    private int depth = 100;  // Number of vertices along the y-axis
    private float scale = 0.5f;  // Scale of the terrain

    private Random random;

    public final Material grassMaterial = new Material(ColorAttribute.createDiffuse(Color.GREEN)); // Use green color for grass
    public final Material earthMaterial = new Material(ColorAttribute.createDiffuse(new Color(0.5f, 0.3f, 0.1f, 1.0f))); // Earth color material
    public final Material sandMaterial = new Material(ColorAttribute.createDiffuse(Color.YELLOW)); // Use yellow color for sand
    
    public TerrainV2(int seed) {
        this.random = new Random(seed);
        // initialize the terrain ( grass field )
        addTerrain(); 

        // Add the water plane after the terrain has been created.
        addWater(0.8f); // You can adjust the alpha for transparency

        mapBorder = new MapBorder(width, depth, scale);
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
    
    private void generateTerrain(ModelBuilder modelBuilder, float halfWidth, float halfDepth, float waterLevel, boolean[][] grassTiles) {
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

        if (height <= waterLevel) {
            material = earthMaterial;
        } else if (grassTiles[x][y] || random.nextDouble() < 0.1) {
            material = sandMaterial;
            if (!grassTiles[x][y]) {
                propagateSand(x, y, grassTiles);
            }
        } else {
            material = grassMaterial;
            grassTiles[x][y] = true;
        }

        return material;
    }
    
    private void propagateSand(int x, int y, boolean[][] grassTiles) {
        // Define the range of neighboring tiles to cover
        int spreadRange = 1; // Increase this value to spread sand to a larger area
    
        // Loop through all neighboring tiles within the spread range
        for (int dx = -spreadRange; dx <= spreadRange; dx++) {
            for (int dy = -spreadRange; dy <= spreadRange; dy++) {
                int nx = x + dx;
                int ny = y + dy;
    
                // Check if the neighboring tile is within bounds and not already sand
                if (nx >= 0 && nx < width && ny >= 0 && ny < depth && !grassTiles[nx][ny]) {
                    // Check if any adjacent tile is already sand
                    boolean adjacentToSand = false;
                    for (int i = -1; i <= 1; i++) {
                        for (int j = -1; j <= 1; j++) {
                            int ax = nx + i;
                            int ay = ny + j;
                            if (ax >= 0 && ax < width && ay >= 0 && ay < depth && grassTiles[ax][ay]) {
                                adjacentToSand = true;
                                break;
                            }
                        }
                        if (adjacentToSand) break;
                    }
                    
                    // If adjacent to sand, assign sand material to neighboring tile
                    if (adjacentToSand) {
                        grassTiles[nx][ny] = true; // Mark neighboring tile as sand
                    }
                }
            }
        }
    }
    
    
    private void createVerticesAndTriangles(ModelBuilder modelBuilder, float adjustedX, float adjustedY, float height, Material material, int x, int y, boolean[][] grassTiles) {
        Vector3 bottomLeft = new Vector3(adjustedX, height, adjustedY);
        Vector3 bottomRight = new Vector3(adjustedX + scale, getHeight(adjustedX + scale, adjustedY), adjustedY);
        Vector3 topLeft = new Vector3(adjustedX, getHeight(adjustedX, adjustedY + scale), adjustedY + scale);
        Vector3 topRight = new Vector3(adjustedX + scale, getHeight(adjustedX + scale, adjustedY + scale), adjustedY + scale);
    
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
    

    private float getHeight(float x, float y) {
        // This is a simple example using a sine function for the height
        return (float)(0.4 * (0.9 - Math.exp(-Math.pow(x, 2) / 8 + Math.pow(y, 2) / 8)));
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