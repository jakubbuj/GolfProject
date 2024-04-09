package com.game;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.Color;

public class Terrain {
    private Model terrainModel;
    private ModelInstance terrainInstance;

    public Terrain() {
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        MeshPartBuilder mpb = modelBuilder.part("terrain", GL20.GL_TRIANGLES, 
            Usage.Position | Usage.Normal, new Material());
        mpb.setColor(Color.GREEN); // So you can see the terrain

        // Create a simple flat terrain
        mpb.box(10f, 1f, 10f);

        terrainModel = modelBuilder.end();
        terrainInstance = new ModelInstance(terrainModel);
    }

    public void render(ModelBatch modelBatch, Environment environment) {
        modelBatch.render(terrainInstance, environment);
    }

    public void dispose() {
        terrainModel.dispose();
    }
}
