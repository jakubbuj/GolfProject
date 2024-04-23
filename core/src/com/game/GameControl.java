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

    @Override
    public void create() {
        modelBatch = new ModelBatch();
        environment = new Environment();
        terrain = new Terrain();

        camera = new PerspectiveCamera(75, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(10f, 10f, 10f);
        camera.lookAt(0f, 0f, 0f);
        camera.near = 0.1f;
        camera.far = 300f;
        camera.update();

        camController = new CameraInputController(camera);
        Gdx.input.setInputProcessor(camController);

        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        // Initialize the ball and physics engine with some arbitrary parameters for now
        ball = new GolfBall(new Vector3(0, 0, 1));
        physicsEngine = new PhysicsEngine("2 * x - y", 0, 0, 4, 1, 0.15, 0.1, 0.2, 0.3, 0.4);
    }

    @Override
    public void render() {
        // Clear the screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1);

        // Update the camera and the game state
        camController.update();
        update(Gdx.graphics.getDeltaTime());

        // Render the terrain and the ball
        modelBatch.begin(camera);
        terrain.render(modelBatch, environment);
        ball.render(modelBatch, environment);
        modelBatch.end();
    }

    private void update(float deltaTime) {
        // Convert deltaTime from seconds to the time units expected by PhysicsEngine
        double dt = deltaTime; // Assuming PhysicsEngine expects seconds.

        // Update the physics engine with the ball's current position and velocity.
        Vector3 ballPosition = ball.getPosition();
        Vector3 ballVelocity = ball.getVelocity();

        // Run a single step of the simulation.
        physicsEngine.runSingleStep(dt, ballPosition, ballVelocity);

        // Update the GolfBall instance with new state from the physics engine
        ball.setVelocity(new Vector3(
                (float) physicsEngine.getStateVector()[2],
                (float) physicsEngine.getStateVector()[3],
                0f // Assuming the game is 2D, Z velocity remains unchanged
        ));

        ball.updatePosition(new Vector3(
                (float) physicsEngine.getStateVector()[0],
                (float) physicsEngine.getStateVector()[1],
                ballPosition.z // Z position remains unchanged
        ));

        // You would then use this updated state in the ball's render method to display
        // it in the new position
    }

    @Override
    public void dispose() {
        // Dispose all the resources
        modelBatch.dispose();
        terrain.dispose();
        // Add disposal for other created resources (e.g., ball model) if necessary
    }
}
