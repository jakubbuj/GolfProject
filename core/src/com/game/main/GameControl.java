package com.game.main;

import com.badlogic.gdx.Screen;
import java.util.List;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Vector3;
import com.game.golfball.GolfAI;
import com.game.golfball.GolfBall;
import com.game.golfball.GolfBallMovement;
import com.game.golfball.PhysicsEngine;
import com.game.golfball.RuleBasedBot;
import com.game.golfball.AStar.AStarMazeSolver;
import com.game.golfball.AStar.Node;
import com.game.golfball.AStar.PathSegmenter;
import com.game.terrain.GameRules;
import com.game.terrain.Target;
import com.game.terrain.TerrainV2;
import com.game.terrain.Maze.Maze;
import com.game.terrain.Maze.Wall;
import com.game.ui.UI;

import java.util.ArrayList;

public class GameControl implements Screen {

    private boolean gameOverSoundPlayed = false;
    private boolean fellInWaterSoundPlayed = false;
    private UI ui;
    private ModelBatch modelBatch;
    static Environment environment;
    private TerrainV2 terrain;
    private PhysicsEngine physicsEngine;
    private GameRules gameRules;
    private GameRules gameRulesRB;
    private GameRules gameRulesAI;
    private Target target;
    private List<Vector3> path;
    // used in applying force to ball
    private boolean isCharging;
    private float chargePower;
    public static final float MAX_CHARGE = 5.0f;
    // balls
    private GolfBall ball;
    private GolfBall AIball;
    private GolfBall RBball;
    private GolfBall Astar;
    private GolfBallMovement ballMovement;
    private GolfAI golfAI;
    private RuleBasedBot ruleBasedBot;
    private GolfAI aStarBot; // Add AStarBot instance
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
    private SettingsScreen settingsScreen; // Reference to SettingsScreen instance
    // maze
    private Maze maze;
    private List<Wall> walls;
    private List<Vector3> segmentedPath = new ArrayList<>();

    private SoundManager soundManager; // SoundManager instance
    private LightSetup lightSetup; // LightSetup instance

    GameRules gameRulesAstar; 

    /**
     * Constructs a GameControl object with the specified GolfGame instance.
     *
     * @param game The instance of the GolfGame.
     */
    public GameControl(GolfGame game) {
        this.game = game;
        this.soundManager = new SoundManager();
        this.lightSetup = new LightSetup(); // Initialize LightSetup
    }

    /**
     * Called when this screen becomes the current screen
     * Initializes the UI, environment, terrain, sounds, background, camera, lights,
     * and input processing
     */
    @Override
    public void show() {
        ui = new UI(this);
        modelBatch = new ModelBatch();
        environment = new Environment();
        terrain = new TerrainV2(width, depth, scale);
        soundManager.loadSounds(); // Load sounds

        backgroundTexture = new Texture("assets/clouds.jpg");

        spriteBatch = new SpriteBatch();

        CameraSetup.setupCamera();
        lightSetup.setupLights(environment); // Setup lights

        setupInput();

        // setup maze
        if (OptionsScreen.GT.equals("maze")) {
            maze = new Maze();
            walls = maze.getWalls();
        }

        physicsEngine = new PhysicsEngine(functionTerrain, X0, Y0, targetPosition.x, targetPosition.z, targetRadius,
                GRASS_K, GRASS_S, SAND_K, SAND_S, 0.0, 0.0, walls);
        // create balls
        ball = new GolfBall(new Vector3(X0, 20, Y0), Color.valueOf("2e3d49"));
        AIball = new GolfBall(new Vector3(X0, 20, Y0), Color.valueOf("007d8d"));
        RBball = new GolfBall(new Vector3(X0, 20, Y0), Color.valueOf("880808"));
        Astar = new GolfBall(new Vector3(X0, 20, Y0), Color.valueOf("ffa500"));

        // target and rules
        target = new Target(targetPosition.x, targetPosition.z, targetRadius); // Example values
        gameRules = new GameRules(target, ball, functionTerrain, terrain);
        gameRulesRB = new GameRules(target, RBball, functionTerrain, terrain);
        gameRulesAI = new GameRules(target, AIball, functionTerrain, terrain);

        // movement of balls
        ballMovement = new GolfBallMovement(ball, physicsEngine, gameRules, walls);
        golfAI = new GolfAI(AIball, targetPosition, physicsEngine, gameRulesAI, walls);
        ruleBasedBot = new RuleBasedBot(RBball, targetPosition, targetRadius, physicsEngine, gameRulesRB, walls);
        aStarBot = new GolfAI(Astar, targetPosition, physicsEngine, gameRulesAI, walls); // Initialize AStarBot

    }

    /**
     * Sets up the input processing for the game, including handling camera controls
     * and charging bar
     */
    private void setupInput() {
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(ui.getStage());
        inputMultiplexer.addProcessor(CameraSetup.camController);
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
     * Triggers a shot by the AI bot
     */
    public void triggerAIShot() {
        Vector3 aiShot = golfAI.findBestShot();
        AIball.setVelocity(aiShot);
        golfAI.update(AIball);
    }

    /**
     * Triggers the rule-based bot to play a shot, calculating the new velocity and
     * updating its state
     */
    public void triggerRuleBasedBotPlay() {
        Vector3 newShotVelocity = ruleBasedBot.calculateNewVelocity();
        RBball.setVelocity(newShotVelocity);
        ruleBasedBot.update(); // Bot makes one shot
    }

    /**
     * Triggers a shot by the A* bot
     */
    public void triggerAStarShot() {
        AStarMazeSolver mazeSolver = new AStarMazeSolver(new Vector3(X0, 20, Y0), targetPosition);
        List<Node> path = mazeSolver.findBestPath();

        PathSegmenter segmenter = new PathSegmenter(path, 6); // Divide into 10 parts
        for (Node node : segmenter.segmentPath()) {
            segmentedPath.add(new Vector3(node.x, 0, node.y));
        }
        aStarBot.setPathSegments(segmentedPath);
        System.out.println(path);
        System.out.println(segmentedPath);
    }

    /**
     * Applies a force to the ball based on the current charge power
     */
    private void applyForceBasedOnCharge() {
        Vector3 direction = new Vector3(CameraSetup.camera.direction).nor();
        Vector3 hitForce = direction.scl(chargePower);
        ballMovement.applyForce(hitForce);
    }

    /**
     * Gets the current charge power
     *
     * @return The current charge power
     */
    public float getChargePower() {
        return chargePower;
    }

    /**
     * Called every frame to render the game screen
     *
     * @param delta The time in seconds since the last render
     */
    @Override
    public void render(float delta) {
        float deltaTime = Gdx.graphics.getDeltaTime();

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        spriteBatch.begin();
        spriteBatch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        spriteBatch.end();

        CameraSetup.camController.update();

        if (isCharging) {
            chargePower += deltaTime;
            chargePower = Math.min(chargePower, MAX_CHARGE);
        }

        update();

        modelBatch.begin(CameraSetup.camera);
        terrain.render(modelBatch, environment);
        AIball.render(modelBatch, environment);
        ball.render(modelBatch, environment);
        RBball.render(modelBatch, environment);
        Astar.render(modelBatch, environment); // Ensure Astar is being rendered
        target.render(modelBatch, environment);
        if (maze != null) { // Check if maze is initialized
            maze.render(modelBatch, environment);
        }
        modelBatch.end();

        // ui
        ui.setChargePower(chargePower);
        ui.render();
    }

    /**
     * Updates the game state, including the ball movements and game rules checks
     */
    private void update() {
        ballMovement.update(); // make ball move
        golfAI.update(AIball); // make AI ball move
        ruleBasedBot.update(); // make rule based bot play

        // Update A* ball's state and target if necessary
        aStarBot.update(Astar);

        // game rules
        gameRules.checkGameStatus();
        gameRulesAstar.checkGameStatus();

        // Check if game is over for A star
        if (gameRulesAstar.isGameOver()) {
            ui.setGameOverLabelVisible(true);
        }

        // Check if game is over
        if ((gameRules.isGameOver() || gameRulesRB.isGameOver() || gameRulesAI.isGameOver()) && !gameOverSoundPlayed) {
            ui.setGameOverLabelVisible(true);
            soundManager.playWinningSound(); // Play winning sound
            gameOverSoundPlayed = true;
        }

        // Check if game is over
        if ((gameRules.fellInWater() || gameRulesRB.fellInWater() || gameRulesAI.fellInWater())
                && !fellInWaterSoundPlayed) {
            ui.setFellInWaterLabelVisible(true);
            soundManager.playFellInWaterSound(); // Play fell in water sound
            fellInWaterSoundPlayed = true;
        }

        // check if ball fell out of bounds
        if ((gameRules.outOfBorder() || gameRulesRB.outOfBorder() || gameRulesAI.outOfBorder())
                && !fellInWaterSoundPlayed) {
            ui.setFellOutOfBoundsLabelVisible(true);
            soundManager.playFellInWaterSound(); // Play fell in water sound
            fellInWaterSoundPlayed = true;
        }
    }

    /**
     * Called when the screen is resized
     *
     * @param width  The new width of the screen
     * @param height The new height of the screen
     */
    @Override
    public void resize(int width, int height) {
    }

    /**
     * Called when the game is paused
     */
    @Override
    public void pause() {
    }

    /**
     * Called when the game is resumed after being paused
     */
    @Override
    public void resume() {
    }

    /**
     * Called when this screen is no longer the current screen
     */
    @Override
    public void hide() {
    }

    /**
     * Restarts the game by resetting all relevant variables to their initial values
     */
    public void restartGame() {
        // Reset all relevant variables to their initial values
        chargePower = 0;
        isCharging = false;
        // Reset ball positions
        ball.setPosition(new Vector3(X0, 20, Y0));
        AIball.setPosition(new Vector3(X0, 20, Y0));
        RBball.setPosition(new Vector3(X0, 20, Y0));
        Astar.setPosition(new Vector3(X0, 20, Y0)); // Reset A* ball position

        gameOverSoundPlayed = false;
        ui.setGameOverLabelVisible(false);
        fellInWaterSoundPlayed = false;
        ui.setFellInWaterLabelVisible(false);
        ui.setFellOutOfBoundsLabelVisible(false);
        path = null; // Reset path
    }

    /**
     * Navigates back to the main menu
     */
    public void backToMainMenu() {
        game.setScreen(new MainMenu(game));
    }

    /**
     * Disposes of resources used by this screen
     */
    @Override
    public void dispose() {
        modelBatch.dispose();
        if (terrain != null)
            terrain.dispose();
        if (ball != null)
            ball.dispose();
        if (CameraSetup.camera != null)
            CameraSetup.camera = null;
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
        if (spriteBatch != null) {
            spriteBatch.dispose();
        }
        soundManager.dispose(); // Dispose sounds
    }
}
