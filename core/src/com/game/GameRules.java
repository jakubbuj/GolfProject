package com.game;

public class GameRules {
    private Target target;
    private GolfBall ball;
    private String functionTerrain;


    public GameRules(Target target, GolfBall ball, String functionTerrain) {
        this.target = target;
        this.ball = ball;
        this.functionTerrain = functionTerrain;
    }

    // Method to check if the game is over (i.e., ball reaches the target)
    public boolean isGameOver() {
        double distance = Math.sqrt(Math.pow((ball.getPosition().x - target.getX()), 2) +
                Math.pow((ball.getPosition().z - target.getY()), 2));
        return distance <= target.getRadius();
    }

    //Method to check if ball is in water
    public boolean fellInWater() {
        double positionx = ball.getPosition().x;
        double positiony = ball.getPosition().y;
        double height = GetHeight.getHeight(functionTerrain, positionx, positiony); 
        return height < 0;
    }

    //Method to check if ball fell out of border
    // public boolean outOfBorder() {
    //     double positionx = ball.getPosition().x;
    //     double positiony = ball.getPosition().y;

    // }

    // Display the game over message
    public void checkGameStatus() {
        if (isGameOver()) {
            System.out.println("Game Over! Ball has reached the target.");
        } 
        if (fellInWater()) {
            System.out.println("Game Over! Ball fell into water.");
        }
    }
}
