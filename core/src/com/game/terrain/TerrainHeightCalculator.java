package com.game.terrain;

import com.game.main.SettingsScreen;

public class TerrainHeightCalculator {

    /**
     * Gets the height of the terrain at the specified coordinates
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @return the height of the terrain at the specified coordinates
     */
    public float getHeight(float x, float y) {
        double result = GetHeight.getHeight(SettingsScreen.terrainFunction, x, y);
        return (float) result;
    }
}
