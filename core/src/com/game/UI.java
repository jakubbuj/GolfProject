package com.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class UI {
    private GameControl gameControl;
    private Stage stage;
    private ProgressBar progressBar;
    private Skin skin;
    private Button aiShotButton;

    public UI(GameControl gameControl) {
        this.gameControl = gameControl;
        skin = new Skin(Gdx.files.internal("assets/skins/visui/assets/uiskin.json"));
        stage = new Stage();
        setupLoadingBar();
        setupAIShotButton();
        Gdx.input.setInputProcessor(stage); // Set this after all UI elements are added
        System.out.println("UI: Input processor set for UI stage.");
    }

    private void setupLoadingBar() {
        progressBar = new ProgressBar(0, GameControl.MAX_CHARGE, 0.01f, false, skin);
        progressBar.setSize(200, 20);
        progressBar.setPosition(Gdx.graphics.getWidth() - progressBar.getWidth() - 10, 10);
        stage.addActor(progressBar);
        System.out.println("UI: Loading bar setup completed.");
    }

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

    public void render() {
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
        System.out.println("UI: Stage rendered."); // This will confirm that render is being called
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public void dispose() {
        stage.dispose();
        skin.dispose();
    }

    public void setChargePower(float chargePower) {
        progressBar.setValue(chargePower);
    }

    public Stage getStage() {
        return stage;
    }

}