package com.game.terrain;

import com.badlogic.gdx.math.Vector3;
import com.game.golfball.GolfBall;

/**
 * GameRules class handles the rules and logic of the game, including checking
 * if the game is over, if the ball fell in water, or if it went out of bounds.
 */
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

    /**
     * Constructor for GameRules.
     * 
     * @param target          the target object
     * @param ball            the golf ball object
     * @param functionTerrain the function describing the terrain
     * @param terrain         the terrain object
     */
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

    /**
     * Sets the ball for the game rules.
     *
     * @param ball the golf ball object
     */
    public void setBall(GolfBall ball) {
        this.ball = ball;
    }

    /**
     * Checks if the game is over by determining if the ball has reached the target
     * 
     * @return true if the game is over, false otherwise
     */
    public boolean isGameOver() {
        double distance = Math.sqrt(Math.pow((ball.getPosition().x - target.getX()), 2) +
                Math.pow((ball.getPosition().z - target.getZ()), 2));
        return distance <= target.getRadius();
    }

    /**
     * Checks if the ball has fallen into water
     * 
     * @return true if the ball fell in water, false otherwise
     */
    public boolean fellInWater() {
        double positionx = ball.getPosition().x;
        double positiony = ball.getPosition().z;
        double height = GetHeight.getHeight(functionTerrain, positionx, positiony);
        return height < 0;
    }

    /**
     * Checks if the ball is out of the defined game borders
     * 
     * @return true if the ball is out of bounds, false otherwise
     */
    public boolean outOfBorder() {
        float positionX = ball.getPosition().x;
        float positionZ = ball.getPosition().z;
        return (positionX < borderXMin || positionX > borderXMax || positionZ < borderZMin || positionZ > borderZMax);
    }

    /**
     * Stops the movement of the ball by setting its velocity to zero.
     */
    private void stopBallMovement() {
        ball.setVelocity(new Vector3(0, 0, 0));
    }

    /**
     * Reverts the ball to its last valid position and stops its movement
     */
    private void revertBallPosition() {
        stopBallMovement();
        ball.setPosition(ball.getLastValidPosition());
    }

    /**
     * Increments the shot counter by one
     */
    public void incrementShotCounter() {
        shotCounter++;
    }

    /**
     * Gets the current shot counter
     * 
     * @return the current shot counter
     */
    public int getShotCounter() {
        return shotCounter;
    }

    /**
     * Checks the game status and determines if the game is over
     * if the ball fell into water, or if it went out of bounds
     */
    public void checkGameStatus() {
        if (!gameOver) {
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
