package com.game;

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

    public GolfBall(Vector3 startPosition) {
        this.position = new Vector3(startPosition);
        this.velocity = new Vector3(0, 0, 0);

        // Create the ball model
        ModelBuilder modelBuilder = new ModelBuilder();
        Model ballModel = modelBuilder.createSphere(1f, 1f, 1f, 24, 24,
                new Material(ColorAttribute.createDiffuse(Color.GREEN)),
                Usage.Position | Usage.Normal);
        this.modelInstance = new ModelInstance(ballModel); // Create a ModelInstance from the Model
    }

    public void updatePosition(float deltaTime) {
        position.add(velocity.x * deltaTime, velocity.y * deltaTime, velocity.z * deltaTime);
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

    public void setVelocity(Vector3 velocity) {
        this.velocity = velocity;
    }

    public ModelInstance getModelInstance() {
        return modelInstance;
    }
}
