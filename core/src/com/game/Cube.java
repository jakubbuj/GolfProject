package com.game;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;

public class Cube {
    private Model cubeModel;
    private ModelInstance cubeInstance;

    public Cube() {
        ModelBuilder modelBuilder = new ModelBuilder();
        cubeModel = modelBuilder.createBox(5f, 5f, 5f,
                new Material(),
                Usage.Position | Usage.Normal);
        cubeInstance = new ModelInstance(cubeModel);
    }

    public void render(ModelBatch modelBatch, Environment environment) {
        modelBatch.render(cubeInstance, environment);
    }

    public void dispose() {
        cubeModel.dispose();
    }
}

