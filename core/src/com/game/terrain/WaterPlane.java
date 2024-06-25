package com.game.terrain;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.Gdx;

public class WaterPlane {
    private Model waterModel;
    private ModelInstance waterInstance;
    private int width;
    private int depth;
    private float scale;

    /**
     * Constructs a WaterPlane with the specified width, depth, and scale
     * @param width the width of the water plane
     * @param depth the depth of the water plane
     * @param scale the scale of the water plane
     */
    public WaterPlane(int width, int depth, float scale) {
        this.width = width;
        this.depth = depth;
        this.scale = scale;
    }

    /**
     * Creates the water model instance with the specified transparency
     * @param alpha the transparency level of the water
     * @return the created water model instance
     */
    public ModelInstance createWater(float alpha) {
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
        return waterInstance;
    }

    /**
     * Renders the water plane
     * @param modelBatch the ModelBatch used for rendering
     * @param environment the Environment for the rendering context
     */
    public void render(ModelBatch modelBatch, Environment environment) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        modelBatch.render(waterInstance, environment);

        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    /**
     * Disposes of the water model resources
     */
    public void dispose() {
        if (waterModel != null) {
            waterModel.dispose();
        }
    }
}
