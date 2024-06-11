package com.game.terrain;

import com.game.main.SettingsScreen;

public class TerrainHeightCalculator {
    public float getHeight(float x, float y) {
        double result = GetHeight.getHeight(SettingsScreen.terrainFunction, x, y);
        return (float) result;
    }
}
