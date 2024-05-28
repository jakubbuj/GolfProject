package com.game.ui;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.Gdx;

/**
 * The {@code CameraController} class handles the setup and control of the free camera and follow camera.
 * It allows switching between a free camera mode and a follow camera mode that tracks a moving object.
 */
public class CameraController {
    private PerspectiveCamera freeCamera;
    private PerspectiveCamera followCamera;
    private CameraInputController cameraController;
    private boolean isFollowing;

    /**
     * Constructs a {@code CameraController} with the specified width and height.
     * Initializes both the free camera and follow camera, and sets up the input processor.
     *
     * @param width  the width of the camera's viewport
     * @param height the height of the camera's viewport
     */
    public CameraController(int width, int height) {
        setupFreeCamera(width, height);
        setupFollowCamera(width, height);
        cameraController = new CameraInputController(freeCamera);
        cameraController.autoUpdate = true;
        Gdx.input.setInputProcessor(cameraController);
        isFollowing = false;
    }

    /**
     * Sets up the free camera with the specified width and height.
     *
     * @param width  the width of the camera's viewport
     * @param height the height of the camera's viewport
     */
    private void setupFreeCamera(int width, int height) {
        freeCamera = new PerspectiveCamera(75, width, height);
        freeCamera.position.set(10f, 10f, 10f);
        freeCamera.lookAt(0f, 0f, 0f);
        freeCamera.near = 0.1f;
        freeCamera.far = 300f;
        freeCamera.update();
    }

    /**
     * Sets up the follow camera with the specified width and height.
     *
     * @param width  the width of the camera's viewport
     * @param height the height of the camera's viewport
     */
    private void setupFollowCamera(int width, int height) {
        followCamera = new PerspectiveCamera(75, width, height);
        followCamera.near = 0.1f;
        followCamera.far = 300f;
        followCamera.update();
    }

    /**
     * Updates the camera's position based on whether an object is moving and its position.
     * Switches between the free camera and follow camera as needed.
     *
     * @param ballMoving  a boolean indicating if the object is moving
     * @param ballPosition the current position of the object being tracked
     */
    public void updateCamera(boolean ballMoving, Vector3 ballPosition) {
        followCamera.position.set(ballPosition.x, ballPosition.y + 10, ballPosition.z + 10);
        followCamera.lookAt(ballPosition);
        followCamera.update();

        if (ballMoving) {
            if (!isFollowing) {
                isFollowing = true;
                followCamera.position.set(ballPosition.x, ballPosition.y + 10, ballPosition.z + 10);
                followCamera.lookAt(ballPosition);
                followCamera.update();
            } else {
                followCamera.position.lerp(new Vector3(ballPosition.x, ballPosition.y + 10, ballPosition.z + 10), 0.1f);
                followCamera.lookAt(ballPosition);
                followCamera.update();
            }
            cameraController.camera = followCamera; // This line replaces setCamera()
        } else {
            if (isFollowing) {
                freeCamera.position.set(followCamera.position);
                freeCamera.update();
                isFollowing = false;
            }
            cameraController.camera = freeCamera; // This line replaces setCamera()
        }
        cameraController.camera = ballMoving ? followCamera : freeCamera;
        cameraController.update();
    }

    /**
     * Returns the current active camera, either the free camera or the follow camera.
     *
     * @return the current active {@code PerspectiveCamera}
     */
    public PerspectiveCamera getCurrentCamera() {
        return isFollowing ? followCamera : freeCamera;
    }
}
