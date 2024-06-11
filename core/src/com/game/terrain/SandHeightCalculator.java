package com.game.terrain;

public class SandHeightCalculator {
    public static float getSandHeight(float x, float y) {
        return (float) (Math.sin(x * 0.1f) + Math.cos(y * 0.1f));
    }
}
