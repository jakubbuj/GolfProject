package com.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;

public class Terrain {
    private Model terrainModel;  // model class like schemat
    private ModelInstance terrainInstance;  // instance
    private Model waterModel;
    private ModelInstance waterInstance;
    private MapBorder mapBorder;

    // Define size of the terrain
    private   int width = 100;  // Number of vertices along the x-axis
    private   int depth = 100;  // Number of vertices along the y-axis
    private   float scale = 0.5f;  // Scale of the terrain

    public Terrain() {

        // initialize the terrain ( grass field )
        addGrass();

        // Add the water plane after the terrain has been created.
        addWater(0.8f); // You can adjust the alpha for transparency

        //Add map border
        mapBorder = new MapBorder(width, depth, scale);
    }

//    method add grass
    public void addGrass(){
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();

        // Compute half width and depth to center the terrain
        float halfWidth = width * scale * 0.5f;
        float halfDepth = depth * scale * 0.5f;

        Material material = new Material(ColorAttribute.createDiffuse(Color.GREEN));
        MeshPartBuilder mpb = modelBuilder.part("terrain", GL20.GL_TRIANGLES,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material);

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

//public void render(ModelBatch modelBatch, Environment environment) {
//    modelBatch.render(terrainInstance, environment);
//
//    // Enable blending for transparent objects
//    Gdx.gl.glEnable(GL20.GL_BLEND);
//    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
//
//    modelBatch.render(waterInstance, environment);
//
//    // disable blending if you want to render more objects that are not transparent
//    Gdx.gl.glDisable(GL20.GL_BLEND);
//}

    public void render(ModelBatch modelBatch, Environment environment) {
        modelBatch.render(terrainInstance, environment);
        modelBatch.render(mapBorder.getBorderInstance(), environment);
        modelBatch.render(waterInstance, environment);
    }

    public void dispose() {
        terrainModel.dispose();
        if (waterModel != null) {
            waterModel.dispose(); // Dispose of the waterModel resources
        }
        mapBorder.dispose();
    }
}
