package com.GUI;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;


public class GolfGame extends Game {

    public static final int WIDTH = 720;
    public static final int HEIGHT = 720;


    public SpriteBatch batch;

    @Override
    public void create() {
        batch = new SpriteBatch();
        this.setScreen(new MainMenu(this));
    }

    public void render() {
        super.render();
    }
    
}
