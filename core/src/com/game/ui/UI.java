package com.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.game.golfball.AStar.AStarBot;
import com.game.main.GameControl;

/**
 * The {@code UI} class is responsible for creating and managing the user
 * interface elements in the game.
 * This includes buttons, labels, and a progress bar.
 */
public class UI {
    private GameControl gameControl;
    private Stage stage;
    private ProgressBar progressBar;
    private Skin skin;
    private Button aiShotButton;
    private Button rbbButton;
    private TextButton gameOverLabel;
    private TextButton fellInWaterLabel;
    private TextButton backLabel;
    private TextButton outOfBoundsLabel;
    private TextButton starButton;

    /**
     * Constructs a {@code UI} instance and initializes the UI elements.
     *
     * @param gameControl the {@code GameControl} instance to interact with the game
     *                    logic
     */
    public UI(GameControl gameControl) {
        this.gameControl = gameControl;
        skin = new Skin(Gdx.files.internal("assets/skins/visui/assets/uiskin.json"));
        stage = new Stage();
        setupLoadingBar();
        setupAIShotButton();
        setupRuleBasedBotButton();
        setupGameOverLabel();
        setupFellInWaterLabel();
        setupBallOutOfBoundsLabel();
        setupStarButton();
        Gdx.input.setInputProcessor(stage); // Set this after all UI elements are added
        System.out.println("UI: Input processor set for UI stage.");
    }

    /**
     * Sets up the loading bar (progress bar) for displaying the charge power.
     */
    private void setupLoadingBar() {
        progressBar = new ProgressBar(0, GameControl.MAX_CHARGE, 0.01f, false, skin);
        progressBar.setSize(200, 20);
        progressBar.setPosition(Gdx.graphics.getWidth() - progressBar.getWidth() - 10, 10);
        stage.addActor(progressBar);
        System.out.println("UI: Loading bar setup completed.");
    }

    /**
     * Sets up the AI Shot button which triggers an AI shot when pressed.
     */
    private void setupAIShotButton() {
        aiShotButton = new TextButton("AI Shot", skin);
        aiShotButton.setPosition(Gdx.graphics.getWidth() - 210, 30); // Check these coordinates carefully
        aiShotButton.setSize(200, 30);
        aiShotButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println("UI: AI Shot button pressed.");
                gameControl.triggerAIShot();
                event.handle(); // Mark the event as handled
            }
        });
        stage.addActor(aiShotButton);
        System.out.println("UI: AI Shot button setup completed at " + aiShotButton.getX() + ", " + aiShotButton.getY());
    }

    /**
     * Sets up the Rule Based Bot Game button which triggers a rule-based bot play
     * when pressed.
     */
    private void setupRuleBasedBotButton() {
        rbbButton = new TextButton("Rule Based Bot Game", skin);
        rbbButton.setPosition(Gdx.graphics.getWidth() - 210, 70);
        rbbButton.setSize(200, 30);
        rbbButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameControl.triggerRuleBasedBotPlay();
                System.out.println("button pressed");
            }
        });
        stage.addActor(rbbButton);
    }

    public void setupStarButton() {
        starButton = new TextButton("Star", skin);
        starButton.setSize(200, 30);
        starButton.setPosition(Gdx.graphics.getWidth() - starButton.getWidth() - 10, 115);
        starButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameControl.triggerAStarShot();
                System.out.println("starbutton pressed");
            }
        });
        stage.addActor(starButton);
        System.out.println("UI: Star button loading completed.");
    }

    /**
     * Sets up the game over label which is displayed when the game is won.
     */
    private void setupGameOverLabel() {
        gameOverLabel = new TextButton("You win! Ball reached the target! Number of shots taken: #  RESTART ", skin);
        gameOverLabel.setPosition(Gdx.graphics.getWidth() / 2 - 300, Gdx.graphics.getHeight() / 2);
        gameOverLabel.setSize(600, 30);
        gameOverLabel.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameControl.restartGame();
            }
        });
        backLabel = new TextButton("Back to main menu", skin);
        backLabel.setPosition(Gdx.graphics.getWidth() / 2 - 100, (Gdx.graphics.getHeight() / 2) - 30);
        backLabel.setSize(400, 30);
        backLabel.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameControl.backToMainMenu();
            }
        });
        gameOverLabel.setVisible(false); // Initially hide the game over label
        backLabel.setVisible(false);
        stage.addActor(gameOverLabel);
        stage.addActor(backLabel);
    }

    /**
     * Sets the visibility of the game over label.
     *
     * @param visible true to make the label visible, false to hide it
     */
    public void setGameOverLabelVisible(boolean visible) {
        gameOverLabel.setVisible(visible);
        backLabel.setVisible(visible);
    }

    /**
     * Sets up the label displayed when the ball falls in water.
     */
    private void setupFellInWaterLabel() {
        fellInWaterLabel = new TextButton("Game Over! Ball fell in water! RESTART", skin);
        fellInWaterLabel.setPosition(Gdx.graphics.getWidth() / 2 - 100, Gdx.graphics.getHeight() / 2);
        fellInWaterLabel.setSize(400, 30);
        fellInWaterLabel.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameControl.restartGame();
            }
        });
        backLabel = new TextButton("Back to main menu", skin);
        backLabel.setPosition(Gdx.graphics.getWidth() / 2 - 100, (Gdx.graphics.getHeight() / 2) - 30);
        backLabel.setSize(400, 30);
        backLabel.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameControl.backToMainMenu();
            }
        });
        fellInWaterLabel.setVisible(false); // Initially hide the game over label
        backLabel.setVisible(false);
        stage.addActor(fellInWaterLabel);
        stage.addActor(backLabel);
    }

    /**
     * Sets the visibility of the label indicating the ball fell in water.
     *
     * @param visible true to make the label visible, false to hide it
     */
    public void setFellInWaterLabelVisible(boolean visible) {
        fellInWaterLabel.setVisible(visible);
        backLabel.setVisible(visible);
    }

    /**
     * Sets up the label displayed when the ball falls out of bounds.
     */
    public void setupBallOutOfBoundsLabel() {
        outOfBoundsLabel = new TextButton("Game Over! Ball fell out of bounds! RESTART", skin);
        outOfBoundsLabel.setPosition(Gdx.graphics.getWidth() / 2 - 100, Gdx.graphics.getHeight() / 2);
        outOfBoundsLabel.setSize(400, 30);
        outOfBoundsLabel.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameControl.restartGame();
            }
        });
        backLabel = new TextButton("Back to main menu", skin);
        backLabel.setPosition(Gdx.graphics.getWidth() / 2 - 100, (Gdx.graphics.getHeight() / 2) - 30);
        backLabel.setSize(400, 30);
        backLabel.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameControl.backToMainMenu();
            }
        });
        outOfBoundsLabel.setVisible(false); // Initially hide the game over label
        backLabel.setVisible(false);
        stage.addActor(outOfBoundsLabel);
        stage.addActor(backLabel);
    }

    /**
     * Sets the visibility of the label indicating the ball fell out of bounds.
     *
     * @param visible true to make the label visible, false to hide it
     */
    public void setFellOutOfBoundsLabelVisible(boolean visible) {
        outOfBoundsLabel.setVisible(visible);
        backLabel.setVisible(visible);
    }

    /**
     * Renders the UI elements, updating the progress bar value and drawing the
     * stage.
     */
    public void render() {
        progressBar.setValue(gameControl.getChargePower());
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
        // System.out.println("UI: Stage rendered."); // This will confirm that render
        // is being called
    }

    /**
     * Resizes the stage viewport to the new width and height.
     *
     * @param width  the new width of the viewport
     * @param height the new height of the viewport
     */
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    /**
     * Disposes of the stage and skin resources.
     */
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }

    /**
     * Sets the charge power value on the progress bar.
     *
     * @param chargePower the charge power value to set
     */
    public void setChargePower(float chargePower) {
        progressBar.setValue(chargePower);
    }

    /**
     * Returns the stage containing all UI elements.
     *
     * @return the {@code Stage} instance
     */
    public Stage getStage() {
        return stage;
    }
}
