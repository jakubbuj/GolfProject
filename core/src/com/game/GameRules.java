package com.game;

public class GameRules {
    private Target target;
    private GolfBall ball;

    public GameRules(Target target, GolfBall ball) {
        this.target = target;
        this.ball = ball;
    }

    // Method to check if the game is over (i.e., ball reaches the target)
    public boolean isGameOver() {
        double distance = Math.sqrt(Math.pow((ball.getPosition().x - target.getX()), 2) +
                Math.pow((ball.getPosition().z - target.getY()), 2));
        return distance <= target.getRadius();
    }

    // Display the game over message
    public void checkGameStatus() {
        if (isGameOver()) {
            System.out.println("Game Over! Ball has reached the target.");
        }
    }
}
