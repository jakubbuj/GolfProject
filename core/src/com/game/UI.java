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
    private GameControl gameControl; // Reference to the GameControl
    private Stage stage;
    private ProgressBar progressBar;
    private Skin skin;
    private Button aiShotButton;

    public UI(GameControl gameControl) {
        this.gameControl = gameControl;
        skin = new Skin(Gdx.files.internal("assets/skins/visui/assets/uiskin.json"));
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        setupLoadingBar();
        setupAIShotButton();
    }

    private void setupLoadingBar() {
        progressBar = new ProgressBar(0, GameControl.MAX_CHARGE, 0.01f, false, skin);
        progressBar.setSize(200, 20);
        progressBar.setPosition(Gdx.graphics.getWidth() - progressBar.getWidth() - 10, 10);
        stage.addActor(progressBar);
    }
    
    private void setupAIShotButton() {
        aiShotButton = new TextButton("AI Shot", skin);
        aiShotButton.setPosition(Gdx.graphics.getWidth() - 210, 30);
        aiShotButton.setSize(200, 30);
        aiShotButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameControl.triggerAIShot();
            }
        });
        stage.addActor(aiShotButton);
    }

    public void render() {
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
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
}

