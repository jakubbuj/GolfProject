package com.game.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.StretchViewport;


public class MainMenu implements Screen {

    // buttons
    private static final int EXIT_BUTTON_WIDTH = 150;
    private static final int EXIT_BUTTON_HEIGHT = 75;
    private static final int PLAY_BUTTON_WIDTH = 300;
    private static final int PLAY_BUTTON_HEIGHT = 150;
    private static final int EXIT_BUTTON_Y = 130;
    private static final int PLAY_BUTTON_Y = 220;
    private static final int SETTINGS_BUTTON_WIDTH = 50;
    private static final int SETTINGS_BUTTON_HEIGHT = 50;
    private static final int SETTINGS_BUTTON_Y = 30;
    private static final int MUSIC_BUTTON_WIDTH = 50;
    private static final int MUSIC_BUTTON_HEIGHT = 50;
    private static final int MUSIC_BUTTON_Y = 400;
    private boolean isMusicOn = true;
    public static Sound clicksound;
    

    // Textures and skins
    private Texture exitButtonActive;
    private Texture exitButtonInactive;
    private Texture playButtonActive;
    private Texture playButtonInactive;
    private Texture settingsButtonActive;
    private Texture settingsButtonInactive;
    private Texture musicButtonActive;
    private Texture musicButtonInactive;
    private Texture backgroundTexture = new Texture("assets/clouds.jpg");

    // Lables
    private Label settings;

    // other
    GolfGame game;
    private Stage stage;
    private Skin skin = new Skin(Gdx.files.internal("assets/skins/visui/assets/uiskin.json"));
  
    /**
     * Constructs a new MainMenu screen.
     * 
     * @param game the main game instance
     */
    public MainMenu(GolfGame game) {
        this.game = game;
        stage = new Stage(new StretchViewport(GolfGame.WIDTH, GolfGame.HEIGHT));
        Gdx.input.setInputProcessor(stage);
        exitButtonActive = new Texture("assets/exit_button_active.jpg");
        exitButtonInactive = new Texture("assets/exit_button_inactive.jpg");
        playButtonActive = new Texture("assets/play_button_active.png");
        playButtonInactive = new Texture("assets/play_button_inactive.png");
        settingsButtonActive = new Texture("assets/settingsactive.png");
        settingsButtonInactive = new Texture("assets/settingsinactive.png");
        clicksound = Gdx.audio.newSound(Gdx.files.internal("assets/click.wav"));
        musicButtonActive = new Texture("assets/soundon.png");
        musicButtonInactive = new Texture("assets/soundoff.png");
        setupsettingslabel();
    }

    /**
     * Called when this screen becomes the current screen for a Game
     */
    @Override
    public void show() {

    }

    /**
     * Called when the screen should render itself
     * 
     * @param delta The time in seconds since the last render
     */
    @Override
    public void render(float delta) {

        // Clear the screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        // Draw the background texture
        game.batch.begin();
        game.batch.draw(backgroundTexture, 0, 0, GolfGame.WIDTH, GolfGame.HEIGHT);


        // handling exit button - exits game
        int x = GolfGame.WIDTH / 2 - EXIT_BUTTON_WIDTH / 2;
        if (Gdx.input.getX() < x + EXIT_BUTTON_WIDTH && Gdx.input.getX() > x
                && GolfGame.HEIGHT - Gdx.input.getY() < EXIT_BUTTON_Y + EXIT_BUTTON_HEIGHT
                && GolfGame.HEIGHT - Gdx.input.getY() > EXIT_BUTTON_Y) {
            game.batch.draw(exitButtonActive, GolfGame.WIDTH / 2 - EXIT_BUTTON_WIDTH / 2, EXIT_BUTTON_Y,
                    EXIT_BUTTON_WIDTH, EXIT_BUTTON_HEIGHT);
            if (Gdx.input.isTouched()) {
                clicksound.play();
                Gdx.app.exit();
            }
        } else {
            game.batch.draw(exitButtonInactive, GolfGame.WIDTH / 2 - EXIT_BUTTON_WIDTH / 2, EXIT_BUTTON_Y,
                    EXIT_BUTTON_WIDTH, EXIT_BUTTON_HEIGHT);
        }

        // handling play button - sets the screen to OptionsScreen
        x = GolfGame.WIDTH / 2 - PLAY_BUTTON_WIDTH / 2;
        if (Gdx.input.getX() < x + PLAY_BUTTON_WIDTH && Gdx.input.getX() > x
                && GolfGame.HEIGHT - Gdx.input.getY() < PLAY_BUTTON_Y + PLAY_BUTTON_HEIGHT
                && GolfGame.HEIGHT - Gdx.input.getY() > PLAY_BUTTON_Y) {
            game.batch.draw(playButtonActive, GolfGame.WIDTH / 2 - PLAY_BUTTON_WIDTH / 2, PLAY_BUTTON_Y,
                    PLAY_BUTTON_WIDTH, PLAY_BUTTON_HEIGHT);
            if (Gdx.input.isTouched()) {
                clicksound.play();
                game.setScreen(new OptionsScreen(game));
            }
        } else {
            game.batch.draw(playButtonInactive, GolfGame.WIDTH / 2 - PLAY_BUTTON_WIDTH / 2, PLAY_BUTTON_Y,
                    PLAY_BUTTON_WIDTH, PLAY_BUTTON_HEIGHT);
        }

        // handling settings button - sets the screen to SettingsScreen
        x = GolfGame.WIDTH / 2 - SETTINGS_BUTTON_WIDTH / 2;
        if (Gdx.input.getX() < x + SETTINGS_BUTTON_WIDTH && Gdx.input.getX() > x
                && GolfGame.HEIGHT - Gdx.input.getY() < SETTINGS_BUTTON_Y + SETTINGS_BUTTON_HEIGHT
                && GolfGame.HEIGHT - Gdx.input.getY() > SETTINGS_BUTTON_Y) {
            game.batch.draw(settingsButtonActive, GolfGame.WIDTH / 2 - SETTINGS_BUTTON_WIDTH / 2, SETTINGS_BUTTON_Y,
                    SETTINGS_BUTTON_WIDTH, SETTINGS_BUTTON_HEIGHT);
            if (Gdx.input.isTouched()) {
                clicksound.play();
                game.setScreen(new SettingsScreen(game));
                
            }
        } else {
            game.batch.draw(settingsButtonInactive, GolfGame.WIDTH / 2 - SETTINGS_BUTTON_WIDTH / 2, SETTINGS_BUTTON_Y,
                    SETTINGS_BUTTON_WIDTH, SETTINGS_BUTTON_HEIGHT);
        }

        // Handling music button
x = GolfGame.WIDTH / 2 - MUSIC_BUTTON_WIDTH / 2;
float mouseX = Gdx.input.getX();
float mouseY = GolfGame.HEIGHT - Gdx.input.getY();

boolean isHovering = mouseX < x + MUSIC_BUTTON_WIDTH && mouseX > x && mouseY < MUSIC_BUTTON_Y + MUSIC_BUTTON_HEIGHT && mouseY > MUSIC_BUTTON_Y;

// Draw the button (active or inactive) based on music state
game.batch.draw(isMusicOn ? musicButtonActive : musicButtonInactive, x, MUSIC_BUTTON_Y, MUSIC_BUTTON_WIDTH, MUSIC_BUTTON_HEIGHT);

// Check if the button is clicked
if (isHovering && Gdx.input.justTouched()) {
    clicksound.play();
    isMusicOn = !isMusicOn;
    if (isMusicOn) {
        game.playmusic();
    } else {
        game.stopMusic();
    }
}

        game.batch.end();

        // Draw the stage
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    /**
     * Sets up the settings label and adds it to the stage.
     */
    public void setupsettingslabel() {
        settings = new Label("SETTINGS", skin);
        settings.setColor(Color.DARK_GRAY);
        settings.setPosition(GolfGame.WIDTH / 2 - 33, SETTINGS_BUTTON_Y - 14);
        settings.setSize(10, 5);
        stage.addActor(settings);

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

    }
}
