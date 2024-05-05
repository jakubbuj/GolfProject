package com.game;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.CylinderShapeBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.ConeShapeBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.graphics.VertexAttributes;

public class Target {
    private double x;
    private double y;
    private double radius;
    private ModelBatch modelBatch;
    private Model holeModel;
    private Model poleModel;
    private Model flagModel;
    private ModelBuilder modelBuilder;

    public Target(double x, double y, double radius, ModelBatch modelBatch) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.modelBatch = modelBatch;
        this.modelBuilder = new ModelBuilder();
        createModels();
    }

    private void createModels() {
        // Create hole
        modelBuilder.begin();
        CylinderShapeBuilder.build(
                modelBuilder.part("hole", GL20.GL_TRIANGLES,
                        VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal,
                        new Material(ColorAttribute.createDiffuse(Color.BLACK))),
                (float) (radius * 2), 0.1f, (float) (radius * 2), 16);
        holeModel = modelBuilder.end();

        // Create pole
        modelBuilder.begin();
        CylinderShapeBuilder.build(
                modelBuilder.part("pole", GL20.GL_TRIANGLES,
                        VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal,
                        new Material(ColorAttribute.createDiffuse(Color.WHITE))),
                0.1f, 5.0f, 0.1f, 16);
        poleModel = modelBuilder.end();

        // Create flag
        modelBuilder.begin();
        ConeShapeBuilder.build(
                modelBuilder.part("flag", GL20.GL_TRIANGLES,
                        VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal,
                        new Material(ColorAttribute.createDiffuse(Color.RED))),
                1f, 2f, 1f, 4);
        flagModel = modelBuilder.end();
    }

    public void render(Camera camera) {
        float terrainHeight = (float) GetHeight.getHeight(GameControl.functionTerrain, x, y);

        // modelBatch.begin(camera); // Ensure correct Camera instance is passed
        // modelBatch.render(holeModel, new Matrix4().translate((float) x,
        // terrainHeight, (float) y).scale(1.0f, 1.0f, 1.0f));
        // modelBatch.render(poleModel, new Matrix4().translate((float) x, terrainHeight
        // + 2.5f, (float) y).scale(1.0f, 1.0f, 1.0f));
        // modelBatch.render(flagModel, new Matrix4().translate((float) x, terrainHeight
        // + 5f, (float) y).scale(1.0f, 1.0f, 1.0f));
        modelBatch.end();
    }

    // Getters
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getRadius() {
        return radius;
    }
}
