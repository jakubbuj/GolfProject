package com.game;

import com.badlogic.gdx.math.Vector3;

public class GameRules {
    private Target target;
    private GolfBall ball;
    private String functionTerrain;
    private float borderXMin;
    private float borderXMax;
    private float borderZMin;
    private float borderZMax;
    private boolean gameOver;
    public int shotCounter;

    public GameRules(Target target, GolfBall ball, String functionTerrain, TerrainV2 terrain) {
        this.target = target;
        this.ball = ball;
        this.functionTerrain = functionTerrain;

        this.borderXMin = -terrain.getWidth() * terrain.getScale() / 2;
        this.borderXMax = terrain.getWidth() * terrain.getScale() / 2;
        this.borderZMin = -terrain.getDepth() * terrain.getScale() / 2;
        this.borderZMax = terrain.getDepth() * terrain.getScale() / 2;
        this.shotCounter = 0;
        this.gameOver = false;
    }

    public boolean isGameOver() {
        double distance = Math.sqrt(Math.pow((ball.getPosition().x - target.getX()), 2) +
                Math.pow((ball.getPosition().z - target.getZ()), 2));
        return distance <= target.getRadius();
    }

    public boolean fellInWater() {
        double positionx = ball.getPosition().x;
        double positiony = ball.getPosition().y;
        double height = GetHeight.getHeight(functionTerrain, positionx, positiony);
        return height < 0;
    }

    public boolean outOfBorder() {
        float positionX = ball.getPosition().x;
        float positionZ = ball.getPosition().z;
        return (positionX < borderXMin || positionX > borderXMax || positionZ < borderZMin || positionZ > borderZMax);
    }

    private void stopBallMovement() {
        ball.setVelocity(new Vector3(0, 0, 0));
    }

    private void revertBallPosition() {
        ball.setPosition(ball.getLastValidPosition());
        stopBallMovement();
    }

    public void incrementShotCounter() {
        shotCounter++;
    }

    public int getShotCounter() {
        return shotCounter;
    }

    public void checkGameStatus() {
        if(!gameOver){
            if (isGameOver()) {
                gameOver = true;
                System.out.println("Game Over! Ball has reached the target.");
                System.out.println("Number of shots taken: " + shotCounter);
                stopBallMovement();
            } else if (fellInWater()) {
                System.out.println("Game Over! Ball fell into water.");
                revertBallPosition();
            } else if (outOfBorder()) {
                System.out.println("Game Over! Ball went out of bounds.");
                revertBallPosition();
            }
        }
    }
}
