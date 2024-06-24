package com.game.main;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.Gdx;

public class CameraSetup {
    public static PerspectiveCamera camera;
    public static CameraInputController camController;

    /**
     * Sets up the camera for the game
     */
    public static void setupCamera() {
        camera = new PerspectiveCamera(75, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(10f, 10f, 10f);
        camera.lookAt(0f, 0f, 0f);
        camera.near = 0.1f;
        camera.far = 300f;
        camera.update();

        camController = new CameraInputController(camera);
    }
}