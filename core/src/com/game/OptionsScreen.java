package com.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.game.GameControl;
import com.game.GolfGame;

public class OptionsScreen implements Screen {

    // Buttons and fields
    private TextField nameBox;
    private SelectBox gameTypeBox;
    private TextButton playButton;
    private static final int BACK_BUTTON_WIDTH = 95;
    private static final int BACK_BUTTON_HEIGHT = 70;
    private static final int BACK_BUTTON_Y = 30;

    // Labels and images
    private Image errorIcon;
    private Label errorLabel;
    private Label NameLabel;
    private Label gameTypeLabel;

    // Textures
    private Texture backButtonActive;
    private Texture backButtonInactive;

    // Passing variables
    public static String name;
    public static Object GT;

    // other
    GolfGame game;
    private Stage stage;
    private Texture backgroundTexture = new Texture("assets/clouds.jpg");
    private Skin skin = new Skin(Gdx.files.internal("assets/skins/visui/assets/uiskin.json"));

    public OptionsScreen(GolfGame game) {
        this.game = game;
        stage = new Stage(new StretchViewport(GolfGame.WIDTH, GolfGame.HEIGHT));
        Gdx.input.setInputProcessor(stage);
        backButtonActive = new Texture("assets/backbuttonactive.png");
        backButtonInactive = new Texture("assets/backbuttoninactivepng.png");
        setUpNamebox();
        setUpSelectBox();
        handlePlayButton();

    }

    // method for setting up a textfield for the player's name
    public void setUpNamebox() {

        nameBox = new TextField("name", skin);
        nameBox.setPosition(300, 500);

        NameLabel = new Label("What is your name?", skin);
        NameLabel.setPosition(300, 530);
        NameLabel.setColor(Color.MAGENTA);

        stage.addActor(nameBox);
        stage.addActor(NameLabel);

    }

    // method for setting up the selection box with game mode options
    public void setUpSelectBox() {

        gameTypeLabel = new Label("What mode do you want to play in?", skin);
        gameTypeLabel.setPosition(300, 360);
        gameTypeLabel.setColor(Color.MAGENTA);

        gameTypeBox = new SelectBox<>(skin);
        gameTypeBox.setPosition(300, 330);
        gameTypeBox.setSize(200, 30);
        gameTypeBox.setItems("multiplayer", "against AI", "singleplayer");

        stage.addActor(gameTypeBox);
        stage.addActor(gameTypeLabel);
    }

    // method for handling the play button. Updates the variables "GT" (game type)
    // and "name". Sets the screen to the GameControl class.
    public void handlePlayButton() {
        playButton = new TextButton("SUBMIT AND PLAY", skin);
        playButton.setPosition(500, 100);
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                // ERROR HANDLING
                if (errorLabel != null) {
                    errorLabel.remove();
                    errorLabel = null;
                }
                if (errorIcon != null) {
                    errorIcon.remove();
                    errorIcon = null;
                }

                try {
                    name = nameBox.getText();

                    if (name == null) {
                        errorLabel = new Label("please enter your name", skin);
                        errorIcon = new Image(new Texture("assets/Error.png"));
                        errorLabel.setColor(Color.RED);
                        errorLabel.setPosition(300, 400);
                        errorIcon.setPosition(350, 400);
                        errorIcon.setSize(20, 30);
                        stage.addActor(errorIcon);
                        stage.addActor(errorLabel);
                    }
                } catch (NumberFormatException e) {

                }

                try {
                    GT = gameTypeBox.getSelected();

                } catch (NumberFormatException e) {
                    errorLabel = new Label("please enter your name", skin);
                    errorIcon = new Image(new Texture("assets/Error.png"));
                    errorLabel.setColor(Color.RED);
                    errorLabel.setPosition(300, 400);
                    errorIcon.setPosition(350, 400);
                    errorIcon.setSize(20, 30);
                    stage.addActor(errorIcon);
                    stage.addActor(errorLabel);
                }

                game.setScreen(new GameControl(game));
                // System.out.println(name);
                // System.out.println(GT);

            }
        });

        stage.addActor(playButton);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw the background texture
        game.batch.begin();
        game.batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Setting up the back button using textures. (The back button sets the screen
        // to MainMenu class)

        int x = 30;
        if (Gdx.input.getX() >= x && Gdx.input.getX() <= x + BACK_BUTTON_WIDTH
                && GolfGame.HEIGHT - Gdx.input.getY() >= BACK_BUTTON_Y
                && GolfGame.HEIGHT - Gdx.input.getY() <= BACK_BUTTON_Y + BACK_BUTTON_HEIGHT) {
            game.batch.draw(backButtonActive, x, BACK_BUTTON_Y, BACK_BUTTON_WIDTH, BACK_BUTTON_HEIGHT);
            if (Gdx.input.isTouched()) {
                game.setScreen(new MainMenu(game));
            }
        } else {
            game.batch.draw(backButtonInactive, x, BACK_BUTTON_Y, BACK_BUTTON_WIDTH, BACK_BUTTON_HEIGHT);
        }

        game.batch.end();

        // Draw the stage
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();

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
        stage.dispose();
        skin.dispose();
    }

}
