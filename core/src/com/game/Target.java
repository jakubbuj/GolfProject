package com.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;

public class Target {
    private Vector3 position;
    private float radius;
    private ModelInstance sphereModel;
    private ModelInstance flagModel;

    public Target(float x, float z, float radius) {
        
        float terrainheight = (float) GetHeight.getHeight(GameControl.functionTerrain,x,z);
        this.position = new Vector3(x, terrainheight+0.5f, z);  // Set y = 0 for simplicity, adjust based on terrain height later
        this.radius = radius;
        createSphere();
    }

    private void createSphere() {
        ModelBuilder modelBuilder = new ModelBuilder();
        sphereModel = new ModelInstance(modelBuilder.createSphere(radius * 2, radius * 2, radius * 2, 24, 24,
                new Material(ColorAttribute.createDiffuse(Color.BLACK)),
                Usage.Position | Usage.Normal));
        sphereModel.transform.setToTranslation(position.x, position.y, position.z);
    }

    private void createFlag() {
        ModelBuilder modelBuilder = new ModelBuilder();
        flagModel = new ModelInstance(modelBuilder.createCylinder(0.1f, 2f, 0.1f, 24,
                new Material(ColorAttribute.createDiffuse(Color.RED)),
                Usage.Position | Usage.Normal));
        flagModel.transform.setToTranslation(position.x, position.y + 1, position.z);
    }

    public void render(ModelBatch modelBatch, Environment environment) {
        modelBatch.render(sphereModel, environment);
        modelBatch.render(flagModel, environment);
    }

    public double getRadius() {
        return radius;
    }

    public double getX() {
        return position.x;
    }

    public double getZ() {
        return position.z;
    }
}
