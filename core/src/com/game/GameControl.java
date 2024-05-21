package com.game;

import com.game.GolfGame;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
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
import com.game.SettingsScreen; // Import the SettingsScreen class

public class GameControl implements Screen {

    private UI ui;
    private ModelBatch modelBatch;
    static Environment environment;
    private TerrainV2 terrain;
    public static PerspectiveCamera camera;
    private CameraInputController camController;
    private PhysicsEngine physicsEngine;
    private GameRules gameRules;
    private Target target;
    // used in applying force to ball
    private boolean isCharging;
    private float chargePower;
    static final float MAX_CHARGE = 5.0f;
    // balls
    private GolfBall ball;
    private GolfBall AIball;
    private GolfBall RBball;
    private GolfBallMovement ballMovement;
    private GolfAI golfAI;
    private RuleBasedBot ruleBasedBot;
    // game parameters
    public static String functionTerrain = SettingsScreen.terrainFunction;
    public static Double X0 = SettingsScreen.InitialX;
    public static Double Y0 = SettingsScreen.InitialY;
    public static Double GRASS_K = SettingsScreen.grassK;
    public static Double GRASS_S = SettingsScreen.grassS;
    public static Double SAND_K = SettingsScreen.sandK;
    public static Double SAND_S = SettingsScreen.sandS;
    // public static Double targetposition.x = SettingsScreen.TargetXo;
    // public static Double targetposition.y = SettingsScreen.TargetYo;
    static int width = 100;
    static int depth = 100;
    static float scale = 0.9f;
    // target
    private Vector3 targetPosition = new Vector3(4.0f, 0.0f, 1.0f);
    private float targetRadius = 0.15f;
    // background
    private Texture backgroundTexture;
    private SpriteBatch spriteBatch;
    private GolfGame game;

    public GameControl(GolfGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        ui = new UI(this);
        modelBatch = new ModelBatch();
        environment = new Environment();
        terrain = new TerrainV2(width, depth, scale);

        backgroundTexture = new Texture("assets/clouds.jpg");
        spriteBatch = new SpriteBatch();

        setupCamera();
        setupLights();
        setupInput();

        physicsEngine = new PhysicsEngine(functionTerrain, X0, Y0, targetPosition.x, targetPosition.z, targetRadius,
                GRASS_K, GRASS_S, SAND_K, SAND_S, 0.0, 0.0);

        ball = new GolfBall(new Vector3(10, 20, 10), Color.WHITE);
        AIball = new GolfBall(new Vector3(10, 20, 11), Color.MAGENTA);
        RBball = new GolfBall(new Vector3(10, 20, 12), Color.GOLD);

        target = new Target(targetPosition.x, targetPosition.z, targetRadius); // Example values
        gameRules = new GameRules(target, ball, functionTerrain, terrain);

        ballMovement = new GolfBallMovement(ball, physicsEngine, gameRules);
        golfAI = new GolfAI(AIball, targetPosition, targetRadius, physicsEngine);
        ruleBasedBot = new RuleBasedBot(RBball, targetPosition, targetRadius, physicsEngine);

        // Now that gameRules is initialized, pass it to ballMovement
        ballMovement = new GolfBallMovement(ball, physicsEngine, gameRules);
        golfAI = new GolfAI(AIball, targetPosition, targetRadius, physicsEngine);
        ruleBasedBot = new RuleBasedBot(RBball, targetPosition, targetRadius, physicsEngine);

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
        inputMultiplexer.addProcessor(ui.getStage());
        inputMultiplexer.addProcessor(camController);
        inputMultiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Keys.SPACE) {
                    isCharging = true;
                    return true;
                }
                return false;
            }

            @Override
            public boolean keyUp(int keycode) {
                if (keycode == Keys.SPACE) {
                    isCharging = false;
                    applyForceBasedOnCharge();
                    chargePower = 0;
                    return true;
                }
                return false;
            }
        });

        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    public void triggerAIShot() {
        Vector3 aiShot = golfAI.findBestShot();
        AIball.setVelocity(aiShot);
        golfAI.update();
    }

    public void triggerRuleBasedBotPlay() {
        Vector3 newShotVelocity = ruleBasedBot.calculateNewVelocity();
        RBball.setVelocity(newShotVelocity);
        ruleBasedBot.update(); // Bot makes one shot
    }

    private void applyForceBasedOnCharge() {
        Vector3 direction = new Vector3(camera.direction).nor();
        Vector3 hitForce = direction.scl(chargePower);
        ballMovement.applyForce(hitForce);
    }

    public float getChargePower() {
        return chargePower;
    }

    @Override
    public void render(float delta) {
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

        update();

        modelBatch.begin(camera);
        terrain.render(modelBatch, environment);
        AIball.render(modelBatch, environment);
        ball.render(modelBatch, environment);
        RBball.render(modelBatch, environment);
        target.render(modelBatch, environment);
        modelBatch.end();

        ui.setChargePower(chargePower);
        ui.render();
    }

    private void update() {
        ballMovement.update(); // moke a golfbal move
        golfAI.update(); // make aiball move
        ruleBasedBot.update(); // make rule based bot play
        // game rules
        gameRules.checkGameStatus();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
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
