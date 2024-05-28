package com.game.terrain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.game.main.GameControl;
import com.game.main.SettingsScreen;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;

public class Target {
    private Vector3 position;
    private float radius;
    private ModelInstance sphereModel;
    private ModelInstance flagModel;
    public String functionT = SettingsScreen.terrainFunction;

    public Target(float x, float z, float radius) {
        float maxHeight = (float) GetHeight.getHeight(GameControl.functionTerrain, x, z);
        for (float dx = -radius; dx <= radius; dx += 0.1f) {
            for (float dz = -radius; dz <= radius; dz += 0.1f) {
                if (Math.sqrt(dx * dx + dz * dz) <= radius) {
                    float height = (float) GetHeight.getHeight(GameControl.functionTerrain, x + dx, z + dz);
                    if (height > maxHeight) {
                        maxHeight = height;
                    }
                }
            }
        }
        this.position = new Vector3(x, maxHeight, z);
        this.radius = radius;
        createSphere();
        createFlag();
    }

    private void createSphere() {
        ModelBuilder modelBuilder = new ModelBuilder();
        sphereModel = new ModelInstance(modelBuilder.createCylinder(radius * 2, 0.01f, radius * 2, 24,
                new Material(ColorAttribute.createDiffuse(Color.BLACK)),
                Usage.Position | Usage.Normal));
        sphereModel.transform.setToTranslation(position.x, position.y, position.z);
    }

    private void createFlag() {
        ObjLoader loader = new ObjLoader();
        Model flagPoleModel = loader.loadModel(Gdx.files.internal("assets/yourMesh (3).obj"));

        flagModel = new ModelInstance(flagPoleModel);
        flagModel.transform.setToTranslation(position.x, position.y + 1, position.z);
        flagModel.transform.scale(0.25f, 0.25f, 0.25f); 
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