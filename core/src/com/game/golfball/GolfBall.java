package com.game.golfball;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;

public class GolfBall {
    private ModelInstance modelInstance;
    private Vector3 position;
    private Vector3 velocity;
    private Vector3 lastValidPosition; // Track the last valid position
    private double mass;

    private static final float MOVEMENT_THRESHOLD = 0.05f; // Adjusted threshold to determine if the ball is moving

    public GolfBall(Vector3 startPosition, Color color) {
        this.position = new Vector3(startPosition);
        this.velocity = new Vector3(0, 0, 0);
        this.lastValidPosition = new Vector3(startPosition); // Initialize the last valid position
        this.mass = 0.05;

        // Create the ball model
        ModelBuilder modelBuilder = new ModelBuilder();
        Model ballModel = modelBuilder.createSphere(1f, 1f, 1f, 24, 24,
                new Material(ColorAttribute.createDiffuse(color)),
                Usage.Position | Usage.Normal);

        this.modelInstance = new ModelInstance(ballModel); // Create a ModelInstance from the Model
    }

    public void render(ModelBatch modelBatch, Environment environment) {
        modelInstance.transform.setToTranslation(position);
        modelBatch.render(modelInstance, environment);
    }

    public Vector3 getPosition() {
        return position;
    }

    public Vector3 getVelocity() {
        return velocity;
    }

    public double getMass() {
        return mass;
    }

    public void setPosition(Vector3 position) {
        this.position = position;
    }

    public void setVelocity(Vector3 velocity) {
        this.velocity = velocity;
    }

    public ModelInstance getModelInstance() {
        return modelInstance;
    }

    public void updateLastValidPosition() {
        this.lastValidPosition.set(this.position);
    }

    public Vector3 getLastValidPosition() {
        return lastValidPosition;
    }

    public boolean isMoving() {
        boolean moving = velocity.len2() > MOVEMENT_THRESHOLD;
        return moving;
    }

    public void dispose() {
        modelInstance.model.dispose();
    }
}
