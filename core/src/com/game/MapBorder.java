package com.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;

public class MapBorder {
    private Model borderModel;
    private ModelInstance borderInstance;

    public MapBorder(int width, int depth, float scale) {
        createBorder(width, depth, scale);
    }

    private void createBorder(int width, int depth, float scale) {
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();

        float halfWidth = width * scale * 0.5f;
        float halfDepth = depth * scale * 0.5f;

        Material material = new Material(ColorAttribute.createDiffuse(Color.BROWN));

        MeshPartBuilder borderBuilder = modelBuilder.part("borders", GL20.GL_TRIANGLES,
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material);

        // Create borders along the edges of the map
        createBorderAlongEdge(borderBuilder, new Vector3(-halfWidth, 0, halfDepth), new Vector3(-halfWidth, 0, -halfDepth), scale, depth);
        createBorderAlongEdge(borderBuilder, new Vector3(halfWidth, 0, halfDepth), new Vector3(-halfWidth, 0, halfDepth), scale, width);
        createBorderAlongEdge(borderBuilder, new Vector3(-halfWidth, 0, -halfDepth), new Vector3(halfWidth, 0, -halfDepth), scale, depth);
        createBorderAlongEdge(borderBuilder, new Vector3(halfWidth, 0, -halfDepth), new Vector3(halfWidth, 0, halfDepth), scale, width);

        borderModel = modelBuilder.end();
        borderInstance = new ModelInstance(borderModel);
    }

    private void createBorderAlongEdge(MeshPartBuilder builder, Vector3 start, Vector3 end, float scale, int steps) {
        Vector3 current = new Vector3(start);
        Vector3 step = new Vector3(end).sub(start).scl(1f / steps);
        for (int i = 0; i < steps; i++) {
            Vector3 next = new Vector3(current).add(step);
            Vector3 top = new Vector3(next).add(0, scale, 0);
            Vector3 topNext = new Vector3(next).add(step).add(0, scale, 0);
            builder.triangle(current, top, next);
            builder.triangle(next, top, topNext);
            current.set(next);
        }
    }

    public ModelInstance getBorderInstance() {
        return borderInstance;
    }

    public void dispose() {
        borderModel.dispose();
    }
}