package com.game.main;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GolfGame extends Game {

    public static final int WIDTH = 720;
    public static final int HEIGHT = 720;
    private Sound music;

    public SpriteBatch batch;

    /**
     * Called when the game is first created.
     * Initializes the sprite batch, sets the initial screen to the main menu
     * and starts playing the background music.
     */
    @Override
    public void create() {
        batch = new SpriteBatch();
        this.setScreen(new MainMenu(this));
        // you can change the music to "assets/Minecraft.mp3" or "assets/Polka.mp3" or "assets/Calm.mp3" or "assets/FoamParty.mp3" or "assets/303.mp3"
        music = Gdx.audio.newSound(Gdx.files.internal("assets/FoamParty.mp3")); 
        playmusic();
    }

    /**
     * Called every frame to render the game
     * Delegates the rendering to the active screen
     */
    @Override
    public void render() {
        super.render();
    }

    /**
     * Plays the background music
     */
    public void playmusic() {
        music.play();
    }

    /**
     * Stops the background music
     */
    public void stopMusic() {
        music.stop();
    }
}
