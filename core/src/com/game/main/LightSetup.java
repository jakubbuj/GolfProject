package com.game.main;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;

public class LightSetup {

    /**
     * Sets up the lights for the game environment
     *
     * @param environment The environment to which the lights will be added
     */
    public void setupLights(Environment environment) {
        // Add a point light
        environment.add(new PointLight().set(Color.WHITE, 10f, 10f, 10f, 100f));
    }
}
