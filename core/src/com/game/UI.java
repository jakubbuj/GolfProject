package com.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class UI {
    private Stage stage;
    private ProgressBar progressBar;
    private Skin skin;

    public UI() {
        skin = new Skin(Gdx.files.internal("assets/skins/visui/assets/uiskin.json")); // Load a skin for UI elements

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        setupLoadingBar();
    }

    private void setupLoadingBar() {
        progressBar = new ProgressBar(0, GameControl.MAX_CHARGE, 0.01f, false, skin);
        progressBar.setSize(200, 20);
        progressBar.setPosition(Gdx.graphics.getWidth() - progressBar.getWidth() - 10, 10);
        stage.addActor(progressBar);
    }

    public void render() {
        stage.act();
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

