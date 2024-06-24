package com.game.main;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector3;


public class LightSetup {

    /**
     * Sets up the lights for the game environment
     *
     * @param environment The environment to which the lights will be added
     */
    public void setupLights(Environment environment) {
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.8f, 0.8f, 0.8f, 5.0f));
        environment.add(new DirectionalLight().set(0.9f, 0.9f, 0.9f, -3f, -10f, -0f));
        environment.add(new DirectionalLight().set(0.9f, 0.9f, 0.9f, 3f, 10f, -0f));
        environment.add(new PointLight().set(0.8f, 0.8f, 0.8f, new Vector3(0, 30, 0), 500f));

    }
}
