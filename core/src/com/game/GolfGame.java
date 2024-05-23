package com.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GolfGame extends Game {

    public static final int WIDTH = 720;
    public static final int HEIGHT = 720;
    private Sound music;


    public SpriteBatch batch;

    @Override
    public void create() {
        batch = new SpriteBatch();
        this.setScreen(new MainMenu(this));
        music = Gdx.audio.newSound(Gdx.files.internal("assets/Minecraft.mp3")); // you can change the music to "assets/Minecraft.mp3"
        playmusic();

        
    }

    public void render() {
        super.render();
    }

    public void playmusic() {
        music.play();
    }

    public void stopMusic() {
        music.stop();
    }
    

}
