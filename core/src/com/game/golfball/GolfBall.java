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
    private Vector3 lastValidPosition; 
    private double mass;

    private static final float MOVEMENT_THRESHOLD = 0.05f; // Adjusted threshold to determine if the ball is moving

    /**
     * Constructs a GolfBall object with the specified start position and color
     *
     * @param startPosition The initial position of the golf ball
     * @param color          The color of the golf ball
     */
    public GolfBall(Vector3 startPosition, Color color) {
        this.position = new Vector3(startPosition);
        this.velocity = new Vector3(0, 0, 0);
        this.lastValidPosition = new Vector3(startPosition); 
        this.mass = 0.05;

        // Create the ball model
        ModelBuilder modelBuilder = new ModelBuilder();
        Model ballModel = modelBuilder.createSphere(0.4f, 0.4f, 0.4f, 24, 24,
                new Material(ColorAttribute.createDiffuse(color)),
                Usage.Position | Usage.Normal);

        this.modelInstance = new ModelInstance(ballModel); 
    }

    /**
     * Renders the golf ball using the specified model batch and environment
     *
     * @param modelBatch   The ModelBatch used for rendering
     * @param environment  The Environment used for rendering
     */
    public void render(ModelBatch modelBatch, Environment environment) {
        modelInstance.transform.setToTranslation(position);
        modelBatch.render(modelInstance, environment);
    }

    /**
     * Returns the current position of the golf ball
     *
     * @return The position vector of the golf ball
     */
    public Vector3 getPosition() {
        return position;
    }

    /**
     * Returns the current velocity of the golf ball
     *
     * @return The velocity vector of the golf ball
     */
    public Vector3 getVelocity() {
        return velocity;
    }

    /**
     * Returns the mass of the golf ball
     *
     * @return The mass of the golf ball
     */
    public double getMass() {
        return mass;
    }

    /**
     * Sets the position of the golf ball to the specified position
     *
     * @param position The new position vector of the golf ball
     */
    public void setPosition(Vector3 position) {
        this.position = position;
    }

    /**
     * sets the velocity of the golf ball to the specified velocity
     *
     * @param velocity The new velocity vector of the golf ball
     */
    public void setVelocity(Vector3 velocity) {
        this.velocity = velocity;
    }

    /**
     * Returns the model instance of the golf ball
     *
     * @return The model instance representing the golf ball
     */
    public ModelInstance getModelInstance() {
        return modelInstance;
    }

    /**
     * Updates the last valid position of the golf ball to the current position
     */
    public void updateLastValidPosition() {
        this.lastValidPosition.set(this.position);
    }

    /**
     * Returns the last valid position of the golf ball
     *
     * @return The last valid position vector of the golf ball
     */
    public Vector3 getLastValidPosition() {
        return lastValidPosition;
    }

    /**
     * Checks if the golf ball is moving based on its velocity
     *
     * @return True if the golf ball is moving, false otherwise
     */
    public boolean isMoving() {
        boolean moving = velocity.len2() > MOVEMENT_THRESHOLD;
        return moving;
    }

    /**
     * Disposes of resources associated with the golf ball.
     */
    public void dispose() {
        modelInstance.model.dispose();
    }
}
