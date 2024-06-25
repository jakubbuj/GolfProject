package com.game.terrain.Maze;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;

public class Wall {
    private Vector3 position;
    private float width;
    private float height;
    private float depth;
    private ModelInstance wallModel;

    /**
     * Constructs a Wall with the specified position and dimensions
     * @param x the x-coordinate of the wall's position
     * @param y the y-coordinate of the wall's position
     * @param z the z-coordinate of the wall's position
     * @param width the width of the wall
     * @param height the height of the wall
     * @param depth the depth of the wall
     */
    public Wall(float x, float y, float z, float width, float height, float depth) {
        this.position = new Vector3(x, y, z);
        this.width = width;
        this.height = height;
        this.depth = depth;
        createWall();
    }

    /**
     * Creates the wall model using the specified dimensions and color
     */
    private void createWall() {
        ModelBuilder modelBuilder = new ModelBuilder();
        Model wall = modelBuilder.createBox(width, height, depth, 
            new Material(ColorAttribute.createDiffuse(Color.GRAY)), 
            Usage.Position | Usage.Normal);
        wallModel = new ModelInstance(wall);
        wallModel.transform.setToTranslation(position.x, position.y, position.z);
    }

    /**
     * Renders the wall using the provided ModelBatch and Environment
     * @param modelBatch the ModelBatch used for rendering
     * @param environment the Environment for the rendering context
     */
    public void render(ModelBatch modelBatch, Environment environment) {
        modelBatch.render(wallModel, environment);
    }

    /**
     * Gets the position of the wall
     * @return the position of the wall as a Vector3
     */
    public Vector3 getPosition() {
        return position;
    }

    /**
     * Gets the width of the wall
     * @return the width of the wall
     */
    public float getWidth() {
        return width;
    }

    /**
     * Gets the depth of the wall
     * @return the depth of the wall
     */
    public float getDepth() {
        return depth;
    }
}
