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
        camera.position.set(10f, 10f, 10f); // Sets the initial position of the camera
        camera.lookAt(0f, 0f, 0f); // Sets the point the camera is looking at
        camera.near = 0.1f; // Sets the near clipping plane distance
        camera.far = 300f; // Sets the far clipping plane distance
        camera.update(); // Updates the camera's projection and view matrix

        camController = new CameraInputController(camera); // Initializes the camera input controller
    }
}
