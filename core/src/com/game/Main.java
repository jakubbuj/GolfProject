package com.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Vector3;

public class Main extends ApplicationAdapter {
    private ModelBatch modelBatch; // combine all model elements into big one
    private Terrain terrain;
    private Cube cube;
    private Environment environment;
    private PerspectiveCamera camera;
    private CameraInputController camController;

    @Override
    public void create() {
        // Set up the camera, where it starts and where it looks
        camera = new PerspectiveCamera(69, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(0, 5, 5f); // Position the camera to look from an angle
        camera.lookAt(0, 0, 0); // Look at the center of the terrain

        // how far and near can camera see
        camera.near = 0.1f;
        camera.far = 1000;
        camera.update();

        // Set up the camera controller
        camController = new CameraInputController(camera);
        Gdx.input.setInputProcessor(camController);

        modelBatch = new ModelBatch();

        // Initialize the terrain
        terrain = new Terrain();

        // cube = new Cube();

        // Set up the environment with some light
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 1.0f, 1.0f, 1.0f, 10f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, 0, 2, 0)); // Stronger light
        environment.add(new PointLight().set(1f, 1f, 1f, new Vector3(0, 50, 29), 200f)); // Point light

    }

    @Override
    public void render() {

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST); // Ensure depth testing is enabled
        Gdx.gl.glClearColor(0.75f, 0.85f, 1.0f, 1.0f); // Set a clear color distinct from your terrain color
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        camController.update();
        camera.update();

        modelBatch.begin(camera);
        // cube.render(modelBatch, environment);
        terrain.render(modelBatch, environment);
        modelBatch.end();
    }

    @Override
    public void dispose() {
        modelBatch.dispose();
        terrain.dispose();
    }
}
