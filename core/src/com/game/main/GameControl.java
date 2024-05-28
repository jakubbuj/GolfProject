package com.game.main;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
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
import com.game.golfball.GolfAI;
import com.game.golfball.GolfBall;
import com.game.golfball.GolfBallMovement;
import com.game.golfball.PhysicsEngine;
import com.game.golfball.RuleBasedBot;
import com.game.terrain.GameRules;
import com.game.terrain.Target;
import com.game.terrain.TerrainV2;
import com.game.ui.UI;

public class GameControl implements Screen {

    private static Sound soundwinning;
    private boolean gameOverSoundPlayed = false;
    private Sound soundFellInWater;
    private boolean fellInWaterSoundPlayed = false; 
    private UI ui;
    private ModelBatch modelBatch;
    static Environment environment;
    private TerrainV2 terrain;
    public static PerspectiveCamera camera;
    private CameraInputController camController;
    private PhysicsEngine physicsEngine;
    private GameRules gameRules;
    private GameRules gameRulesRB;
    private GameRules gameRulesAI;
    private Target target;
    // used in applying force to ball
    private boolean isCharging;
    private float chargePower;
    public static final float MAX_CHARGE = 5.0f;
    // balls
    private GolfBall ball;
    private GolfBall AIball;
    private GolfBall RBball;
    private GolfBallMovement ballMovement;
    private GolfAI golfAI;
    private RuleBasedBot ruleBasedBot;
    // game parameters
    public static String functionTerrain = SettingsScreen.terrainFunction;
    public float X0 = SettingsScreen.InitialX.floatValue();
    public float Y0 = SettingsScreen.InitialY.floatValue();
    public Double GRASS_K = SettingsScreen.grassK;
    public Double GRASS_S = SettingsScreen.grassS;
    public Double SAND_K = SettingsScreen.sandK;
    public Double SAND_S = SettingsScreen.sandS;
    public float Tx = SettingsScreen.TargetXo.floatValue();
    public float Tz = SettingsScreen.TargetYo.floatValue();
    static int width = 100;
    static int depth = 100;
    static float scale = 0.9f;
    // target
    private Vector3 targetPosition = new Vector3(Tx, 0.0f, Tz);
    private float targetRadius = SettingsScreen.Radius.floatValue();
    // background
    private Texture backgroundTexture;
    private SpriteBatch spriteBatch;
    private GolfGame game;
    private SettingsScreen settingsScreen;  // Reference to SettingsScreen instance

    
    /**
     * Constructs a GameControl object with the specified GolfGame instance.
     *
     * @param game The instance of the GolfGame.
     */
    public GameControl(GolfGame game) {
        this.game = game;
    }

    /**
     * Called when this screen becomes the current screen for a {@link com.badlogic.gdx.Game}.
     * Initializes the UI, environment, terrain, sounds, background, camera, lights, and input processing.
     */
    @Override
    public void show() {
        ui = new UI(this);
        modelBatch = new ModelBatch();
        environment = new Environment();
        terrain = new TerrainV2(width, depth, scale);
        soundwinning = Gdx.audio.newSound(Gdx.files.internal("assets/winsound.wav"));
        soundFellInWater = Gdx.audio.newSound(Gdx.files.internal("assets/ninagameoverrr.mp3"));
        backgroundTexture = new Texture("assets/clouds.jpg");

        spriteBatch = new SpriteBatch();

        setupCamera();
        setupLights();
        setupInput();


        physicsEngine = new PhysicsEngine(functionTerrain, X0, Y0, targetPosition.x, targetPosition.z, targetRadius,
                GRASS_K, GRASS_S, SAND_K, SAND_S, 0.0, 0.0);

        ball = new GolfBall(new Vector3(X0, 20, Y0), Color.valueOf("2e3d49"));
        AIball = new GolfBall(new Vector3(X0, 20, Y0), Color.valueOf("007d8d"));
        RBball = new GolfBall(new Vector3(X0, 20, Y0), Color.valueOf("880808"));

        target = new Target(targetPosition.x, targetPosition.z, targetRadius); // Example values
        gameRules = new GameRules(target, ball, functionTerrain, terrain);
        gameRulesRB = new GameRules(target, RBball, functionTerrain, terrain);
        gameRulesAI = new GameRules(target, AIball, functionTerrain, terrain);

        ballMovement = new GolfBallMovement(ball, physicsEngine, gameRules);
        golfAI = new GolfAI(AIball, targetPosition, physicsEngine, gameRulesAI);
        ruleBasedBot = new RuleBasedBot(RBball, targetPosition, targetRadius, physicsEngine, gameRulesRB);

        // Now that gameRules is initialized, pass it to ballMovement
        ballMovement = new GolfBallMovement(ball, physicsEngine, gameRules);
        golfAI = new GolfAI(AIball, targetPosition, physicsEngine, gameRulesAI);
        ruleBasedBot = new RuleBasedBot(RBball, targetPosition, targetRadius, physicsEngine, gameRulesRB);

    }

    /**
     * Sets up the camera for the game.
     */
    private void setupCamera() {
        camera = new PerspectiveCamera(75, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(10f, 10f, 10f);
        camera.lookAt(0f, 0f, 0f);
        camera.near = 0.1f;
        camera.far = 300f;
        camera.update();

        camController = new CameraInputController(camera);
    }

    /**
     * Sets up the lighting for the game environment.
     */
    private void setupLights() {
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.8f, 0.8f, 0.8f, 5.0f));
        environment.add(new DirectionalLight().set(0.9f, 0.9f, 0.9f, -3f, -10f, -0f));
        environment.add(new DirectionalLight().set(0.9f, 0.9f, 0.9f, 3f, 10f, -0f));
        environment.add(new PointLight().set(0.8f, 0.8f, 0.8f, new Vector3(0, 30, 0), 500f));
    }

    /**
     * Sets up the input processing for the game, including handling camera controls and charging shots.
     */
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

    /**
     * Triggers a shot by the AI, setting its velocity and updating its state.
     */
    public void triggerAIShot() {
        Vector3 aiShot = golfAI.findBestShot();
        AIball.setVelocity(aiShot);
        golfAI.update();
    }

    /**
     * Triggers the rule-based bot to play a shot, calculating the new velocity and updating its state.
     */
    public void triggerRuleBasedBotPlay() {
        Vector3 newShotVelocity = ruleBasedBot.calculateNewVelocity();
        RBball.setVelocity(newShotVelocity);
        ruleBasedBot.update(); // Bot makes one shot
    }

    /**
     * Applies a force to the ball based on the current charge power.
     */
    private void applyForceBasedOnCharge() {
        Vector3 direction = new Vector3(camera.direction).nor();
        Vector3 hitForce = direction.scl(chargePower);
        ballMovement.applyForce(hitForce);
    }

    /**
     * Gets the current charge power.
     *
     * @return The current charge power.
     */
    public float getChargePower() {
        return chargePower;
    }

    /**
     * Called every frame to render the game screen.
     *
     * @param delta The time in seconds since the last render.
     */
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

    /**
     * Updates the game state, including the ball movements and game rules checks.
     */
    private void update() {
        ballMovement.update(); // moke a golfbal move
        golfAI.update(); // make aiball move
        ruleBasedBot.update(); // make rule based bot play
        // game rules
        gameRules.checkGameStatus();

        //Check if game is over 
        if ((gameRules.isGameOver() || gameRulesRB.isGameOver() || gameRulesAI.isGameOver()) && !gameOverSoundPlayed) {
            ui.setGameOverLabelVisible(true);
            soundwinning.play();
            gameOverSoundPlayed = true;
        }
         //Check if game is over 
         if ((gameRules.fellInWater() || gameRulesRB.fellInWater() || gameRulesAI.fellInWater()) && !fellInWaterSoundPlayed) {
            ui.setFellInWaterLabelVisible(true);
            soundFellInWater.play();
            fellInWaterSoundPlayed = true;
        }
        //check if ball fell out of bounds
        if ((gameRules.outOfBorder() || gameRulesRB.outOfBorder() || gameRulesAI.outOfBorder()) && !fellInWaterSoundPlayed) {
            ui.setFellOutOfBoundsLabelVisible(true);
            soundFellInWater.play();
            fellInWaterSoundPlayed = true;
        }
    }

    /**
     * Called when the screen is resized.
     *
     * @param width The new width of the screen.
     * @param height The new height of the screen.
     */
    @Override
    public void resize(int width, int height) {
    }

    /**
     * Called when the game is paused.
     */
    @Override
    public void pause() {
    }

    /**
     * Called when the game is resumed after being paused.
     */
    @Override
    public void resume() {
    }

    /**
     * Called when this screen is no longer the current screen for a {@link com.badlogic.gdx.Game}.
     */
    @Override
    public void hide() {
    }

    /**
     * Restarts the game by resetting all relevant variables to their initial values.
     */
    public void restartGame() {
        // Reset all relevant variables to their initial values
        chargePower = 0;
        isCharging = false;
        // Reset ball positions
        ball.setPosition(new Vector3(X0, 20, Y0));
        AIball.setPosition(new Vector3(X0, 20, Y0));
        RBball.setPosition(new Vector3(X0, 20, Y0));

        gameOverSoundPlayed = false;
        ui.setGameOverLabelVisible(false);
        fellInWaterSoundPlayed = false;
        ui.setFellInWaterLabelVisible(false);
        ui.setFellOutOfBoundsLabelVisible(false);

    }

    /**
     * Navigates back to the main menu.
     */
    public void backToMainMenu() {
        game.setScreen(new MainMenu(game));
    }

    /**
     * Disposes of resources used by this screen.
     */
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

