package com.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
    static Environment environment;
    private Terrain terrain;
    public static PerspectiveCamera camera;
    private CameraInputController camController;
    private PhysicsEngine physicsEngine;
    private GameRules gameRules;
    private Target target;
    // used in applaying force to ball
    private boolean isCharging;
    private float chargePower;
    static final float MAX_CHARGE = 5.0f;
    // balls
    private GolfBall ball;
    private GolfBall AIball;
    private GolfBallMovement ballMovement;
    private GolfAI golfAI;
    // game parameters
    public static String functionTerrain = " sqrt ( ( sin ( 0.1 * x ) + cos ( 0.1 * y ) ) ^ 2 ) + 0.5 * sin ( 0.3 * x ) * cos ( 0.3 * y ) ";
    private int width = 100;
    private int depth = 100;
    private float scale = 0.9f;
    // target
    private Vector3 targetposition = new Vector3(4.0f, 0.0f, 1.0f);
    private float targetRadius = 0.15f;
    // background
    private Texture backgroundTexture;
    private SpriteBatch spriteBatch;

    @Override
    public void create() {

        ui = new UI(this);
        modelBatch = new ModelBatch();
        environment = new Environment();
        terrain = new Terrain(width, depth, scale);

        backgroundTexture = new Texture("assets/clouds.jpg");
        spriteBatch = new SpriteBatch();

        setupCamera();
        setupLights();
        setupInput();

        physicsEngine = new PhysicsEngine(functionTerrain, 3, 0, 4, 1, targetRadius, 0.6, 0.6, 0.3, 0.4, 0.0, 0.0);

        // Initialize the ball and physics engine with some arbitrary parameters for now
        ball = new GolfBall(new Vector3(10, 20, 10), Color.WHITE);
        AIball = new GolfBall(new Vector3(10, 20, 11), Color.MAGENTA);

        ballMovement = new GolfBallMovement(ball, physicsEngine);
        golfAI = new GolfAI(AIball, targetposition, targetRadius, physicsEngine);

        target = new Target(4, 1, 1f); // Example values
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
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.8f, 0.8f, 0.8f, 5.0f));
        environment.add(new DirectionalLight().set(0.9f, 0.9f, 0.9f, -3f, -10f, -0f));
        environment.add(new DirectionalLight().set(0.9f, 0.9f, 0.9f, 3f, 10f, -0f));
        environment.add(new PointLight().set(0.8f, 0.8f, 0.8f, new Vector3(0, 30, 0), 500f));
    }

    private void setupInput() {
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        // Add UI stage first to ensure UI events are processed before anything else
        inputMultiplexer.addProcessor(ui.getStage());
        inputMultiplexer.addProcessor(camController);
        inputMultiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Keys.SPACE) {
                    isCharging = true;
                    return true; // Event handled
                }
                return false;
            }

            @Override
            public boolean keyUp(int keycode) {
                if (keycode == Keys.SPACE) {
                    isCharging = false;
                    applyForceBasedOnCharge();
                    chargePower = 0;
                    return true; // Event handled
                }
                return false;
            }
        });

        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    public void triggerAIShot() {
        Vector3 aiShot = golfAI.findBestShot();
        AIball.setVelocity(aiShot);
        golfAI.update(); // This makes the ball move
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
        float deltaTime = Gdx.graphics.getDeltaTime();

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        spriteBatch.begin();
        spriteBatch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        spriteBatch.end();

        camController.update();

        if (isCharging) {
            chargePower += deltaTime;
            chargePower = Math.min(chargePower, MAX_CHARGE);
        }

        update(); // Update game logic

        modelBatch.begin(camera);
        terrain.render(modelBatch, environment);
        AIball.render(modelBatch, environment);
        ball.render(modelBatch, environment);
        target.render(modelBatch, environment);
        modelBatch.end();

        ui.render(); // Make sure UI elements are drawn last
    }

    private void update() {
        ballMovement.update(); // moke a golfbal move
        golfAI.update(); // make aiball move
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
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
        if (spriteBatch != null) {
            spriteBatch.dispose();
        }
    }
}
