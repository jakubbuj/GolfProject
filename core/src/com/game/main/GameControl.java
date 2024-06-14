package com.game.main;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.*;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.Input.Keys;
import com.game.golfball.*;
import com.game.terrain.*;
import com.game.ui.UI;

public class GameControl implements Screen {

    private SoundManager soundManager;
    private UI ui;
    private ModelBatch modelBatch;
    private static Environment environment;
    private TerrainV2 terrain;
    public static PerspectiveCamera camera;
    private CameraInputController camController;
    private PhysicsEngine physicsEngine;
    private GameRules gameRules;
    private GameRules gameRulesRB;
    private GameRules gameRulesAI;
    private Target target;
    private boolean isCharging;
    private float chargePower;
    public static final float MAX_CHARGE = 5.0f;
    private BallManager ballManager;
    private GameParameters gameParameters;
    private Vector3 targetPosition;
    private Texture backgroundTexture;
    private SpriteBatch spriteBatch;
    private GolfGame game;
    public static String functionTerrain = SettingsScreen.terrainFunction;

    public GameControl(GolfGame game) {
        this.game = game;
        this.gameParameters = new GameParameters();
        this.targetPosition = new Vector3(gameParameters.Tx, 0.0f, gameParameters.Tz);
        this.soundManager = new SoundManager();
    }

    @Override
    public void show() {
        ui = new UI(this);
        modelBatch = new ModelBatch();
        environment = new Environment();
        terrain = new TerrainV2(EnvironmentSettings.width, EnvironmentSettings.depth, EnvironmentSettings.scale);
        backgroundTexture = new Texture("assets/clouds.jpg");
        spriteBatch = new SpriteBatch();
        
        setupCamera();
        setupLights();
        setupInput();

        physicsEngine = new PhysicsEngine(gameParameters.functionTerrain, gameParameters.X0, gameParameters.Y0, targetPosition.x, targetPosition.z, gameParameters.targetRadius,
                gameParameters.GRASS_K, gameParameters.GRASS_S, gameParameters.SAND_K, gameParameters.SAND_S, 0.0, 0.0);
        
        target = new Target(targetPosition.x, targetPosition.z, gameParameters.targetRadius);
        gameRules = new GameRules(target, null, gameParameters.functionTerrain, terrain);
        gameRulesRB = new GameRules(target, null, gameParameters.functionTerrain, terrain);
        gameRulesAI = new GameRules(target, null, gameParameters.functionTerrain, terrain);

        ballManager = new BallManager(physicsEngine, gameRules, gameRulesAI, gameRulesRB, new Vector3(gameParameters.X0, 20, gameParameters.Y0), targetPosition, gameParameters.targetRadius);
        
        gameRules.setBall(ballManager.getBall());
        gameRulesRB.setBall(ballManager.getRBball());
        gameRulesAI.setBall(ballManager.getAIball());
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
        Vector3 aiShot = ballManager.getGolfAI().findBestShot();
        ballManager.getAIball().setVelocity(aiShot);
        ballManager.getGolfAI().update();
    }

    public void triggerRuleBasedBotPlay() {
        Vector3 newShotVelocity = ballManager.getRuleBasedBot().calculateNewVelocity();
        ballManager.getRBball().setVelocity(newShotVelocity);
        ballManager.getRuleBasedBot().update();
    }

    private void applyForceBasedOnCharge() {
        Vector3 direction = new Vector3(camera.direction).nor();
        Vector3 hitForce = direction.scl(chargePower);
        ballManager.getBallMovement().applyForce(hitForce);
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
        ballManager.getAIball().render(modelBatch, environment);
        ballManager.getBall().render(modelBatch, environment);
        ballManager.getRBball().render(modelBatch, environment);
        target.render(modelBatch, environment);
        modelBatch.end();

        ui.setChargePower(chargePower);
        ui.render();
    }

    private void update() {
        ballManager.getBallMovement().update();
        ballManager.getGolfAI().update();
        ballManager.getRuleBasedBot().update();

        gameRules.checkGameStatus();

        if ((gameRules.isGameOver() || gameRulesRB.isGameOver() || gameRulesAI.isGameOver()) && !soundManager.isGameOverSoundPlayed()) {
            ui.setGameOverLabelVisible(true);
            soundManager.getSoundWinning().play();
            soundManager.setGameOverSoundPlayed(true);
        }
        if ((gameRules.fellInWater() || gameRulesRB.fellInWater() || gameRulesAI.fellInWater()) && !soundManager.isFellInWaterSoundPlayed()) {
            ui.setFellInWaterLabelVisible(true);
            soundManager.getSoundFellInWater().play();
            soundManager.setFellInWaterSoundPlayed(true);
        }
        if ((gameRules.outOfBorder() || gameRulesRB.outOfBorder() || gameRulesAI.outOfBorder()) && !soundManager.isFellInWaterSoundPlayed()) {
            ui.setFellOutOfBoundsLabelVisible(true);
            soundManager.getSoundFellInWater().play();
            soundManager.setFellInWaterSoundPlayed(true);
        }
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

    public void restartGame() {
        chargePower = 0;
        isCharging = false;
        ballManager.getBall().setPosition(new Vector3(gameParameters.X0, 20, gameParameters.Y0));
        ballManager.getAIball().setPosition(new Vector3(gameParameters.X0, 20, gameParameters.Y0));
        ballManager.getRBball().setPosition(new Vector3(gameParameters.X0, 20, gameParameters.Y0));

        soundManager.setGameOverSoundPlayed(false);
        ui.setGameOverLabelVisible(false);
        soundManager.setFellInWaterSoundPlayed(false);
        ui.setFellInWaterLabelVisible(false);
        ui.setFellOutOfBoundsLabelVisible(false);
    }

    public void backToMainMenu() {
        game.setScreen(new MainMenu(game));
    }

    @Override
    public void dispose() {
        modelBatch.dispose();
        if (terrain != null) terrain.dispose();
        if (ballManager.getBall() != null) ballManager.getBall().dispose();
        if (camera != null) camera = null;
        if (backgroundTexture != null) backgroundTexture.dispose();
        if (spriteBatch != null) spriteBatch.dispose();
        ui.dispose();
        soundManager.dispose();
    }
}
