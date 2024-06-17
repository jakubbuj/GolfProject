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

    public Wall(float x, float y, float z, float width, float height, float depth) {
        this.position = new Vector3(x, y, z);
        this.width = width;
        this.height = height;
        this.depth = depth;
        createWall();
    }

    private void createWall() {
        ModelBuilder modelBuilder = new ModelBuilder();
        Model wall = modelBuilder.createBox(width, height, depth, 
            new Material(ColorAttribute.createDiffuse(Color.GRAY)), 
            Usage.Position | Usage.Normal);
        wallModel = new ModelInstance(wall);
        wallModel.transform.setToTranslation(position.x, position.y, position.z);
    }

    public void render(ModelBatch modelBatch, Environment environment) {
        modelBatch.render(wallModel, environment);
    }

    public Vector3 getPosition() {
        return position;
    }

    public float getWidth() {
        return width;
    }

    public float getDepth() {
        return depth;
    }
}
