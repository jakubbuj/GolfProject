package com.game.terrain;

public class SandHeightCalculator {

    /**
     * Calculates the height of the sand at the specified coordinates
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @return the height of the sand at the specified coordinates
     */
    public static float getSandHeight(float x, float y) {
        return (float) (Math.sin(x * 0.1f) + Math.cos(y * 0.1f));
    }
}
