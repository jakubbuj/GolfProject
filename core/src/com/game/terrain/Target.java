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

/**
 * The {@code Target} class represents a target in the game terrain. It consists
 * of a sphere at ground level and a flag model positioned on top of the sphere.
 */
public class Target {
    private Vector3 position;
    private float radius;
    private ModelInstance sphereModel;
    private ModelInstance flagModel;
    public String functionT = SettingsScreen.terrainFunction;

    /**
     * Constructs a {@code Target} at the specified coordinates with the given radius.
     * The target's height is adjusted based on the terrain function.
     *
     * @param x      the x-coordinate of the target
     * @param z      the z-coordinate of the target
     * @param radius the radius of the target area
     */
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

    /**
     * Creates the sphere model representing the base of the target.
     */
    private void createSphere() {
        ModelBuilder modelBuilder = new ModelBuilder();
        sphereModel = new ModelInstance(modelBuilder.createCylinder(radius * 2, 0.01f, radius * 2, 24,
                new Material(ColorAttribute.createDiffuse(Color.BLACK)),
                Usage.Position | Usage.Normal));
        sphereModel.transform.setToTranslation(position.x, position.y, position.z);
    }

    /**
     * Creates the flag model positioned on top of the sphere.
     */
    private void createFlag() {
        ObjLoader loader = new ObjLoader();
        Model flagPoleModel = loader.loadModel(Gdx.files.internal("assets/yourMesh (3).obj"));

        flagModel = new ModelInstance(flagPoleModel);
        flagModel.transform.setToTranslation(position.x, position.y + 1, position.z);
        flagModel.transform.scale(0.25f, 0.25f, 0.25f); 
    }

    /**
     * Renders the target models using the given {@code ModelBatch} and {@code Environment}.
     *
     * @param modelBatch the {@code ModelBatch} used for rendering
     * @param environment the {@code Environment} providing lighting and other effects
     */
    public void render(ModelBatch modelBatch, Environment environment) {
        modelBatch.render(sphereModel, environment);
        modelBatch.render(flagModel, environment);
    }

    /**
     * Returns the radius of the target.
     *
     * @return the radius of the target
     */
    public double getRadius() {
        return radius;
    }

    /**
     * Returns the x-coordinate of the target.
     *
     * @return the x-coordinate of the target
     */
    public double getX() {
        return position.x;
    }

    /**
     * Returns the z-coordinate of the target.
     *
     * @return the z-coordinate of the target
     */
    public double getZ() {
        return position.z;
    }
}
