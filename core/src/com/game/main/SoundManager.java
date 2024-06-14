package com.game.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public class SoundManager {
    private Sound soundWinning;
    private Sound soundFellInWater;
    private boolean gameOverSoundPlayed = false;
    private boolean fellInWaterSoundPlayed = false;

    public SoundManager() {
        soundWinning = Gdx.audio.newSound(Gdx.files.internal("assets/winsound.wav"));
        soundFellInWater = Gdx.audio.newSound(Gdx.files.internal("assets/ninagameoverrr.mp3"));
    }

    public Sound getSoundWinning() {
        return soundWinning;
    }

    public Sound getSoundFellInWater() {
        return soundFellInWater;
    }

    public boolean isGameOverSoundPlayed() {
        return gameOverSoundPlayed;
    }

    public void setGameOverSoundPlayed(boolean gameOverSoundPlayed) {
        this.gameOverSoundPlayed = gameOverSoundPlayed;
    }

    public boolean isFellInWaterSoundPlayed() {
        return fellInWaterSoundPlayed;
    }

    public void setFellInWaterSoundPlayed(boolean fellInWaterSoundPlayed) {
        this.fellInWaterSoundPlayed = fellInWaterSoundPlayed;
    }

    public void dispose() {
        soundWinning.dispose();
        soundFellInWater.dispose();
    }
}
