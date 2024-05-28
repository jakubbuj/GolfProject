package com.game.ui;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.Gdx;

public class CameraController {
    private PerspectiveCamera freeCamera;
    private PerspectiveCamera followCamera;
    private CameraInputController cameraController;
    private boolean isFollowing;

    public CameraController(int width, int height) {
        setupFreeCamera(width, height);
        setupFollowCamera(width, height);
        cameraController = new CameraInputController(freeCamera);
        cameraController.autoUpdate = true;
        Gdx.input.setInputProcessor(cameraController);
        isFollowing = false;
    }

    private void setupFreeCamera(int width, int height) {
        freeCamera = new PerspectiveCamera(75, width, height);
        freeCamera.position.set(10f, 10f, 10f);
        freeCamera.lookAt(0f, 0f, 0f);
        freeCamera.near = 0.1f;
        freeCamera.far = 300f;
        freeCamera.update();
    }

    private void setupFollowCamera(int width, int height) {
        followCamera = new PerspectiveCamera(75, width, height);
        followCamera.near = 0.1f;
        followCamera.far = 300f;
        followCamera.update();
    }

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
    

    public PerspectiveCamera getCurrentCamera() {
        return isFollowing ? followCamera : freeCamera;
    }

    
}
