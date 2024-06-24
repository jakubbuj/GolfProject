package com.game.main;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.Gdx;

public class SoundManager {
    private Sound soundFellInWater;
    private Sound soundWinning;

    /**
     * Loads the sounds used in the game
     */
    public void loadSounds() {
        soundFellInWater = Gdx.audio.newSound(Gdx.files.internal("assets/falling_in_water.mp3"));
        soundWinning = Gdx.audio.newSound(Gdx.files.internal("assets/winning.mp3"));
    }

    /**
     * Plays the sound for falling in water
     */
    public void playFellInWaterSound() {
        soundFellInWater.play();
    }

    /**
     * Plays the sound for winning
     */
    public void playWinningSound() {
        soundWinning.play();
    }

    /**
     * Disposes of the sounds when no longer needed
     */
    public void dispose() {
        if (soundFellInWater != null) {
            soundFellInWater.dispose();
        }
        if (soundWinning != null) {
            soundWinning.dispose();
        }
    }
}
