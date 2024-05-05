package com.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputAdapter;
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

    private UI ui;
    private ModelBatch modelBatch;
    private Environment environment;
    private Terrain terrain;
    private PerspectiveCamera camera;
    private CameraInputController camController;
    private PhysicsEngine physicsEngine;
    private GameRules gameRules;
    private Target target;
    // used in applaying force to ball
    private boolean isCharging;
    private float chargePower;
    static final float MAX_CHARGE = 5.0f;
    private GolfBall ball;
    private GolfBallMovement ballMovement;
    // game parameters
    public static String functionTerrain = " sqrt ( ( sin x + cos y ) ^ 2 )";
    private int width = 100;
    private int depth = 100;
    private float scale = 0.5f;

    @Override
    public void create() {

        ui = new UI();
        modelBatch = new ModelBatch();
        environment = new Environment();
        terrain = new Terrain(width, depth, scale);

        setupCamera();
        setupLights();
        setupInput();

        // Initialize the ball and physics engine with some arbitrary parameters for now
        ball = new GolfBall(new Vector3(10, 20, 10));
        physicsEngine = new PhysicsEngine(functionTerrain, 3, 0, 4, 1, 0.15, 0.6, 0.6, 0.3, 0.4, 0.0, 0.0);
        ballMovement = new GolfBallMovement(ball, physicsEngine);

        target = new Target(4, 1, 4, modelBatch); // Example values
        gameRules = new GameRules(target, ball, functionTerrain);

    }

    private void setupCamera() {
        camera = new PerspectiveCamera(75, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(10f, 10f, 10f);
        camera.lookAt(0f, 0f, 0f);
        camera.near = 0.1f;
        camera.far = 300f;
        camera.update();

        camController = new CameraInputController(camera);
    }

    private void setupLights() {
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 5.0f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -3f, -10f, -0f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, 3f, 10f, -0f));
        environment.add(new PointLight().set(0.6f, 0.6f, 0.6f, new Vector3(0, 30, 0), 300f));
    }

    private void setupInput() {
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(camController);
        inputMultiplexer.addProcessor(new InputAdapter() {

            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Keys.SPACE) {
                    isCharging = true; // Start charging when space is pressed
                    return true; // done with this evvent or task
                }
                return false;
            }

            @Override
            public boolean keyUp(int keycode) {
                if (keycode == Keys.SPACE) {
                    isCharging = false;
                    applyForceBasedOnCharge(); // Apply force when space is released
                    chargePower = 0; // Reset charge
                    return true; // done with this evvent or task
                }
                return false;
            }
        });

        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    private void applyForceBasedOnCharge() {
        // Apply the force in the direction you want, for example, forwards from the
        // camera's perspective
        Vector3 direction = new Vector3(camera.direction).nor(); // Normalized direction vector
        Vector3 hitForce = direction.scl(chargePower); // Scale direction by the charged power
        ballMovement.applyForce(hitForce);
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1);
        camController.update();

        float deltaTime = Gdx.graphics.getDeltaTime();

        if (isCharging) {
            chargePower += deltaTime; // Increase charge power over time
            chargePower = Math.min(chargePower, MAX_CHARGE); // Clamp to max charge
        }

        ui.setChargePower(chargePower); // vizualize charge power in chargebar

        update(deltaTime);

        modelBatch.begin(camera);
        terrain.render(modelBatch, environment);
        ball.render(modelBatch, environment);
        modelBatch.end();

        ui.render();
    }

    private void update(float deltaTime) {
        ballMovement.update(deltaTime);
        // game rules
        gameRules.checkGameStatus();
    }

    @Override
    public void dispose() {
        modelBatch.dispose();
        if (terrain != null)
            terrain.dispose();
        if (ball != null)
            ball.dispose();
        if (camera != null)
            camera = null;
    }
}
