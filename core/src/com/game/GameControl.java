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

public class GameControl extends ApplicationAdapter {
    private ModelBatch modelBatch;
    private Environment environment;
    private Terrain terrain;
    private GolfBall ball;
    private PerspectiveCamera camera;
    private CameraInputController camController;
    private PhysicsEngine physicsEngine;
    public static String functionTerrain="";

    @Override
    public void create() {
        modelBatch = new ModelBatch();
        environment = new Environment();
        functionTerrain = "sin x + sin y";
        terrain = new Terrain();

        camera = new PerspectiveCamera(75, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(10f, 10f, 10f);
        camera.lookAt(0f, 0f, 0f);
        camera.near = 0.1f;
        camera.far = 300f;
        camera.update();

        camController = new CameraInputController(camera);
        Gdx.input.setInputProcessor(camController);

        // ligts
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 1.0f, 1.0f, 1.0f, 10f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, 0, 2, 0)); // Stronger light
        environment.add(new PointLight().set(1f, 1f, 1f, new Vector3(0, 50, 29), 200f)); // Point light

        // Initialize the ball and physics engine with some arbitrary parameters for now
        ball = new GolfBall(new Vector3(10, 20, 10));
        physicsEngine = new PhysicsEngine(functionTerrain, 3, 0, 4, 1, 0.15, 0.1, 0.2, 0.3, 0.4, 0.5, 0.0);
    }

    @Override
    public void render() {
        // Clear the screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1);

        camController.update();

        float deltaTime = Gdx.graphics.getDeltaTime();
        update(deltaTime);

        // Render the terrain and the ball
        modelBatch.begin(camera);
        terrain.render(modelBatch, environment);
        ball.render(modelBatch, environment);
        modelBatch.end();
    }

    private void update(float deltaTime) {

        System.out.println("Ball position: " + ball.getPosition()+"  "+ ball.getVelocity());

        // Convert deltaTime from seconds to the time units expected by PhysicsEngine
        double dt = deltaTime; // Assuming PhysicsEngine expects seconds.

        // Update the physics engine with the ball's current position and velocity.
        Vector3 ballPosition = ball.getPosition();
        Vector3 ballVelocity = ball.getVelocity();

        // Run a single step of the simulation.
        physicsEngine.runSingleStep(dt, ballPosition, ballVelocity);

        //Update the GolfBall instance with new state from the physics engine
        ball.setVelocity(new Vector3(
            (float) physicsEngine.getStateVector()[2], // X velocity
            0f, // Y velocity remains 0 because we don't have vertical movement in this physics engine
            (float) physicsEngine.getStateVector()[3] // Z velocity
        ));

        ball.setPosition(new Vector3(
            (float) physicsEngine.getStateVector()[0],
            (float) physicsEngine.terrainHeight,
            (float) physicsEngine.getStateVector()[1]
        ));
    }

    @Override
    public void dispose() {
        modelBatch.dispose();
        if (terrain != null) {
            terrain.dispose();
        }
        if (ball != null) {
            ball.dispose(); // Ensure this dispose method exists in GolfBall to clean up the model
        }
        if (camera != null) {
            camera = null; // Release the camera if needed
        }
    }
}
