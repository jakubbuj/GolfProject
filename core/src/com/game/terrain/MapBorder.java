package com.game.terrain;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;

/**
 * The {@code MapBorder} class is responsible for creating and managing a border
 * around the game map. The border is rendered as a transparent rectangle
 * surrounding the map.
 */
public class MapBorder {
    private Model borderModel;
    private ModelInstance borderInstance;

    private int width;
    private int depth;
    private float scale;

    /**
     * Constructs a {@code MapBorder} with the specified dimensions and scale.
     *
     * @param width  the width of the game map
     * @param depth  the depth of the game map
     * @param scale  the scale factor for the map and border
     */
    public MapBorder(int width, int depth, float scale) {
        this.width = width;
        this.depth = depth;
        this.scale = scale;

        addBorder();
    }

    /**
     * Creates the border model around the game map. The border is defined as a
     * rectangle surrounding the map with a specified border width.
     */
    public void addBorder() {
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();

        Material material = new Material(ColorAttribute.createDiffuse(new Color(0, 0, 0, 0))); // Transparent border

        MeshPartBuilder mpb = modelBuilder.part("border", GL20.GL_TRIANGLES,
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material);

        // Define border vertices
        float halfWidth = width * scale * 0.5f;
        float halfDepth = depth * scale * 0.5f;
        float borderWidth = 0.7f; // Adjust border width as needed

        Vector3 topLeft = new Vector3(-halfWidth - borderWidth, 0, halfDepth + borderWidth);
        Vector3 topRight = new Vector3(halfWidth + borderWidth, 0, halfDepth + borderWidth);
        Vector3 bottomRight = new Vector3(halfWidth + borderWidth, 0, -halfDepth - borderWidth);
        Vector3 bottomLeft = new Vector3(-halfWidth - borderWidth, 0, -halfDepth - borderWidth);

        // Create border geometry
        mpb.rect(
                topLeft, topRight, bottomRight, bottomLeft,
                Vector3.Y
        );

        borderModel = modelBuilder.end();
        borderInstance = new ModelInstance(borderModel);
    }

    /**
     * Returns the {@code ModelInstance} of the border, which can be used for rendering.
     *
     * @return the {@code ModelInstance} of the border
     */
    public ModelInstance getBorderInstance() {
        return borderInstance;
    }

    /**
     * Disposes of the resources used by the border model.
     */
    public void dispose() {
        borderModel.dispose();
    }
}
