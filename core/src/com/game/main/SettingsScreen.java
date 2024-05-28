package com.game.main;

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
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * SettingsScreen class for handling the settings of the game.
 * Implements the Screen interface from libGDX.
 */
public class SettingsScreen implements Screen {

    // back button
    private static final int BACK_BUTTON_WIDTH = 95;
    private static final int BACK_BUTTON_HEIGHT = 70;
    private static final int BACK_BUTTON_Y = 30;
    private Texture backButtonActive;
    private Texture backButtonInactive;

    // Buttons
    private TextButton submitButton;
    private TextButton defaultButton;
    private SelectBox FunctionSelect;

    // Text fields
    private TextField function;
    private TextField InitialCoordinateX;
    private TextField InitialCoordinateY;
    private TextField SAND_K;
    private TextField SAND_S;
    private TextField GRASS_K;
    private TextField GRASS_S;
    private TextField TargetXBox, TargetYBox;
    private TextField TRBox;

    // labels
    private Label Xo;
    private Label Yo;
    private Label F;
    private Label Gk, Gs;
    private Label Sk, Ss;
    private Label TXoLabel, TYoLabel;
    private Image errorIcon;
    private Label errorLabel;
    private Label RadiusLabel;

    // other
    GolfGame game;
    private Stage stage;
    private Texture backgroundTexture = new Texture("assets/clouds.jpg");
    private Skin skin = new Skin(Gdx.files.internal("assets/skins/visui/assets/uiskin.json"));

    // passing variables
    public static String terrainFunction;
    public static Double InitialX;
    public static Double InitialY;
    public static Double grassK, grassS;
    public static Double sandK, sandS;
    public static Double TargetXo, TargetYo;
    public static Double Radius;

    /**
     * Constructor for SettingsScreen.
     * 
     * @param game the main game instance
     */
    public SettingsScreen(GolfGame game) {
        this.game = game;

        stage = new Stage(new StretchViewport(GolfGame.WIDTH, GolfGame.HEIGHT));
        Gdx.input.setInputProcessor(stage);
        backButtonActive = new Texture("assets/backbuttonactive.png");
        backButtonInactive = new Texture("assets/backbuttoninactivepng.png");
        setupTextFields();
        handleSubmitButton();
        handleDefaultButton();

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
                MainMenu.clicksound.play();
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

    /**
     * Sets up the text fields for user input.
     */
    private void setupTextFields() {
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));

        // handling X initial coordinates label and textfield
        Xo = new Label("Initial start x coordinate", skin);
        Xo.setPosition(200, 650);
        Xo.setColor(Color.MAGENTA);

        InitialCoordinateX = new TextField("Enter initial X coordinate for golf ball", skin);
        InitialCoordinateX.setPosition(200, 620);
        InitialCoordinateX.setSize(300, 30);

        // handling Y initial coordinates label and textfield
        Yo = new Label("Initial start y coordinate", skin);
        Yo.setPosition(200, 600);
        Yo.setColor(Color.MAGENTA);

        InitialCoordinateY = new TextField("Enter initial Y coordinate for golf ball", skin);
        InitialCoordinateY.setPosition(200, 570);
        InitialCoordinateY.setSize(300, 30);

        // handling function label and textfield
        F = new Label("Terrain function", skin);
        F.setPosition(200, 150);
        F.setColor(Color.MAGENTA);

        function = new TextField("Type in function", skin);
        function.setPosition(180, 120);
        function.setSize(250, 30);

        // handling Grass static and kinetic friction
        Gk = new Label("Grass kinetic", skin);
        Gk.setPosition(80, 450);
        Gk.setColor(Color.MAGENTA);

        GRASS_K = new TextField("Type in the kinetic friction of grass", skin);
        GRASS_K.setPosition(80, 420);
        GRASS_K.setSize(250, 30);

        Gs = new Label("Grass static", skin);
        Gs.setPosition(370, 450);
        Gs.setColor(Color.MAGENTA);

        GRASS_S = new TextField("Type in the static friction of grass", skin);
        GRASS_S.setPosition(370, 420);
        GRASS_S.setSize(250, 30);

        // handling Sand static and kinetic friction
        Sk = new Label("Sand kinetic", skin);
        Sk.setPosition(80, 520);
        Sk.setColor(Color.MAGENTA);

        SAND_K = new TextField("Type in the kinetic friction of sand", skin);
        SAND_K.setPosition(80, 490);
        SAND_K.setSize(250, 30);

        Ss = new Label("Sand static", skin);
        Ss.setPosition(370, 520);
        Ss.setColor(Color.MAGENTA);

        SAND_S = new TextField("Type in the static friction of sand", skin);
        SAND_S.setPosition(370, 490);
        SAND_S.setSize(250, 30);

        // Target coordinates
        TXoLabel = new Label("Coordinate X of target", skin);
        TXoLabel.setPosition(200, 380);
        TXoLabel.setColor(Color.MAGENTA);

        TargetXBox = new TextField("Type in the coordinate X of the target", skin);
        TargetXBox.setPosition(200, 350);
        TargetXBox.setSize(300, 30);

        TYoLabel = new Label("Coordinate Y of target", skin);
        TYoLabel.setPosition(200, 310);
        TYoLabel.setColor(Color.MAGENTA);

        TargetYBox = new TextField("Type in the coordinate Y of the target", skin);
        TargetYBox.setPosition(200, 280);
        TargetYBox.setSize(300, 30);
        
        RadiusLabel = new Label("radius target", skin);
        RadiusLabel.setPosition(200, 240);
        RadiusLabel.setColor(Color.MAGENTA);

        TRBox = new TextField("Type in the radius of target", skin);
        TRBox.setPosition(200, 210);
        TRBox.setSize(300, 30);
        
        stage.addActor(Xo);
        stage.addActor(InitialCoordinateX);
        stage.addActor(Yo);
        stage.addActor(InitialCoordinateY);
        stage.addActor(F);
        stage.addActor(function);
        stage.addActor(Gk);
        stage.addActor(GRASS_K);
        stage.addActor(Gs);
        stage.addActor(GRASS_S);
        stage.addActor(Sk);
        stage.addActor(SAND_K);
        stage.addActor(Ss);
        stage.addActor(SAND_S);
        stage.addActor(TXoLabel);
        stage.addActor(TargetXBox);
        stage.addActor(TYoLabel);
        stage.addActor(TargetYBox);
        stage.addActor(RadiusLabel);
        stage.addActor(TRBox);

    }

    /**
     * Handles the submit button. Updates variables: InitialX, InitialY, terrainFunction,
     * grassK, grassS, sandK, sandS, TargetXo, TargetYo, Radius.
     */
    private void handleSubmitButton() {
        submitButton = new TextButton("Submit", skin);
        submitButton.setPosition(530, 100);
        submitButton.setSize(100, 30);
        submitButton.addListener(new ClickListener() {
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
                    InitialX = Double.parseDouble(InitialCoordinateX.getText());
                } catch (NumberFormatException e) {
                    errorLabel = new Label("please enter a number", skin);
                    errorIcon = new Image(new Texture("assets/Error.png"));
                    errorLabel.setColor(Color.RED);
                    errorLabel.setPosition(17, 625);
                    errorIcon.setPosition(170, 620);
                    errorIcon.setSize(20, 30);
                    stage.addActor(errorIcon);
                    stage.addActor(errorLabel);
                }

                try {
                    InitialY = Double.parseDouble(InitialCoordinateY.getText());
                } catch (NumberFormatException e) {
                    errorLabel = new Label("please enter a number", skin);
                    errorIcon = new Image(new Texture("assets/Error.png"));
                    errorLabel.setColor(Color.RED);
                    errorLabel.setPosition(17, 575);
                    errorIcon.setPosition(170, 570);
                    errorIcon.setSize(20, 30);
                    stage.addActor(errorIcon);
                    stage.addActor(errorLabel);
                }

                try {
                    terrainFunction = function.getText();
                } catch (NumberFormatException e) {
                    errorLabel = new Label("incorrect function", skin);
                    errorIcon = new Image(new Texture("assets/Error.png"));
                    errorLabel.setColor(Color.RED);
                    errorLabel.setPosition(17, 425);
                    errorIcon.setPosition(170, 420);
                    errorIcon.setSize(20, 30);
                    stage.addActor(errorIcon);
                    stage.addActor(errorLabel);
                }

                try {
                    grassK = Double.parseDouble(GRASS_K.getText());
                } catch (NumberFormatException e) {
                    errorLabel = new Label("type in number", skin);
                    errorIcon = new Image(new Texture("assets/Error.png"));
                    errorLabel.setColor(Color.RED);
                    errorLabel.setPosition(20, 400);
                    errorIcon.setPosition(40, 420);
                    errorIcon.setSize(20, 30);
                    stage.addActor(errorIcon);
                    stage.addActor(errorLabel);
                }
                try {
                    grassS = Double.parseDouble(GRASS_S.getText());
                } catch (NumberFormatException e) {
                    errorLabel = new Label("type in number", skin);
                    errorIcon = new Image(new Texture("assets/Error.png"));
                    errorLabel.setColor(Color.RED);
                    errorLabel.setPosition(600, 400);
                    errorIcon.setPosition(630, 420);
                    errorIcon.setSize(20, 30);
                    stage.addActor(errorIcon);
                    stage.addActor(errorLabel);
                }

                try {
                    sandK = Double.parseDouble(SAND_K.getText());
                } catch (NumberFormatException e) {
                    errorLabel = new Label("type in number", skin);
                    errorIcon = new Image(new Texture("assets/Error.png"));
                    errorLabel.setColor(Color.RED);
                    errorLabel.setPosition(20, 470);
                    errorIcon.setPosition(40, 490);
                    errorIcon.setSize(20, 30);
                    stage.addActor(errorIcon);
                    stage.addActor(errorLabel);
                }
                try {
                    sandS = Double.parseDouble(SAND_S.getText());
                } catch (NumberFormatException e) {
                    errorLabel = new Label("type in number", skin);
                    errorIcon = new Image(new Texture("assets/Error.png"));
                    errorLabel.setColor(Color.RED);
                    errorLabel.setPosition(600, 470);
                    errorIcon.setPosition(630, 490);
                    errorIcon.setSize(20, 30);
                    stage.addActor(errorIcon);
                    stage.addActor(errorLabel);
                }
                try {
                    TargetXo = Double.parseDouble(TargetXBox.getText());
                } catch (NumberFormatException e) {
                    errorLabel = new Label("type in number", skin);
                    errorIcon = new Image(new Texture("assets/Error.png"));
                    errorLabel.setColor(Color.RED);
                    errorLabel.setPosition(65, 345);
                    errorIcon.setPosition(170, 340);
                    errorIcon.setSize(20, 30);
                    stage.addActor(errorIcon);
                    stage.addActor(errorLabel);
                }
                try {
                    TargetYo = Double.parseDouble(TargetYBox.getText());
                } catch (NumberFormatException e) {
                    errorLabel = new Label("type in number", skin);
                    errorIcon = new Image(new Texture("assets/Error.png"));
                    errorLabel.setColor(Color.RED);
                    errorLabel.setPosition(65, 275);
                    errorIcon.setPosition(170, 270);
                    errorIcon.setSize(20, 30);
                    stage.addActor(errorIcon);
                    stage.addActor(errorLabel);
                }
                try {
                    Radius = Double.parseDouble(TRBox.getText());
                } catch (NumberFormatException e) {
                    errorLabel = new Label("type in number", skin);
                    errorIcon = new Image(new Texture("assets/Error.png"));
                    errorLabel.setColor(Color.RED);
                    errorLabel.setPosition(65, 215);
                    errorIcon.setPosition(170, 210);
                    errorIcon.setSize(20, 30);
                    stage.addActor(errorIcon);
                    stage.addActor(errorLabel);
                }
                if (errorLabel == null && errorIcon == null) {
                    game.setScreen(new MainMenu(game));
                }
               
                MainMenu.clicksound.play();

            }
        });


        stage.addActor(submitButton); // Add the submit button to the stage

    }

    /**
     * Handles the default button, which sets default values for all text fields.
     */
    public void handleDefaultButton() {
        defaultButton = new TextButton("Default", skin);
        defaultButton.setPosition(30, 100);
        defaultButton.setSize(100, 30);
        defaultButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                function.setText(" sqrt ( ( sin ( 0.1 * x ) + cos ( 0.1 * y ) ) ^ 2 ) + 0.5 * sin ( 0.3 * x ) * cos ( 0.3 * y ) ");
                InitialCoordinateX.setText("5.0");
                InitialCoordinateY.setText("2.0");
                GRASS_K.setText("1.0");
                GRASS_S.setText("0.5");
                SAND_K.setText("0.3");
                SAND_S.setText("0.4");
                TargetXBox.setText("4.0");
                TargetYBox.setText("1.0");
                TRBox.setText("0.5");

                // Update corresponding static variables
                terrainFunction = function.getText();
                InitialX = Double.parseDouble(InitialCoordinateX.getText());
                InitialY = Double.parseDouble(InitialCoordinateY.getText());
                grassK = Double.parseDouble(GRASS_K.getText());
                grassS = Double.parseDouble(GRASS_S.getText());
                sandK = Double.parseDouble(SAND_K.getText());
                sandS = Double.parseDouble(SAND_S.getText());
                TargetXo = Double.parseDouble(TargetXBox.getText());
                TargetYo = Double.parseDouble(TargetYBox.getText());
                Radius = Double.parseDouble(TRBox.getText());

                MainMenu.clicksound.play();
            }
        });

        stage.addActor(defaultButton); // Add the default button to the stage
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