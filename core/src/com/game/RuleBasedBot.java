package com.game;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
public class RuleBasedBot {

    private static Vector3 targetPosition;
    private Vector3 ballPosition;
    private float targetRadius;
    private PhysicsEngine physicsEngine;
    private static GolfBall RBball;
    private static GameRules gameRules;

    //Vector3 initialVelocity = new Vector3 (10, 0, 10);


    public RuleBasedBot(GolfBall RBball, Vector3 targetPosition, float targetRadius, PhysicsEngine physicsEngine, GameRules gameRules) {
        this.RBball = RBball;
        this.targetPosition = targetPosition;
        this.targetRadius = targetRadius;
        this.physicsEngine = physicsEngine;
        if (gameRules == null) {
            throw new IllegalArgumentException("gameRules cannot be null");
        }
        this.gameRules = gameRules;
    }

    public Vector3 calculateNewVelocity(){
        ballPosition = RBball.getPosition();
        // float xDirection; 
        // float zDirection;
        // // Determining in which direction the ball has to move to reach the target (x coordinates)
        // if (targetPosition.x < ballPosition.x){
        //     xDirection = -1;
        // } else if (targetPosition.x > ballPosition.x){
        //     xDirection = 1;
        // } else {
        //     xDirection = 0;
        // }
        // // Determining in which direction the ball has to move to reach the target (z coordinates)
        // if (targetPosition.z < ballPosition.z){
        //     xDirection = -1;
        // } else if (targetPosition.z > ballPosition.z){
        //     xDirection = 1;
        // } else {
        //     xDirection = 0;
        // }

        Vector2 vectorBT = new Vector2(ballPosition.x - targetPosition.x, ballPosition.z - targetPosition.z); //vector from ball position to target position
        double magnitude = Math.sqrt(Math.pow(vectorBT.x, 2) + Math.pow(vectorBT.y, 2));
        //Normalizing vectorBT to get direction vector of the ball
        Vector2 direction = new Vector2((float)(-vectorBT.x / magnitude), (float)(-vectorBT.y / magnitude));

        double force;
        if (magnitude < 3 && magnitude > 1) force = 3.5;
        else if (magnitude <= 1) force = 0.75;
        else force = magnitude;
        Vector3 newVelocity = new Vector3( (float)(direction.x * force), (float)(0.0), (float)(direction.y * force));
        return newVelocity;
    }

    public void update() {

        //Vector3 currentVelocity = new Vector3(initialVelocity);
        
        Vector3 currentPosition = RBball.getPosition();
        // Get the current position and velocity of the ball
        currentPosition = RBball.getPosition();
        Vector3 currentVelocity = new Vector3 (RBball.getVelocity());
        // Set the current state in the physics engine
        physicsEngine.setState(currentPosition.x, currentPosition.z, currentVelocity.x, currentVelocity.z);

        // Run the physics simulation for one timestep
        double[] newState = physicsEngine.runSingleStep(currentPosition, currentVelocity);

        // Update the ball's position based on the physics engine output
        RBball.setPosition(new Vector3((float) newState[0], RBball.getPosition().y, (float) newState[1]));

        // Update the ball's velocity based on the physics engine output
        RBball.setVelocity(new Vector3((float) newState[2], 0, (float) newState[3]));

        // Adjust the ball's vertical position based on the terrain height
        RBball.getPosition().y = (float) (physicsEngine.terrainHeight + 0.2f); // Keeping the ball slightly above the terrain

    }

    private boolean reachedTarget(){
        boolean xPos = RBball.getPosition().x <= targetPosition.x + targetRadius && RBball.getPosition().x >= targetPosition.x - targetRadius;
        boolean yPos = RBball.getPosition().y <= targetPosition.y + targetRadius && RBball.getPosition().y >= targetPosition.y - targetRadius;
        return xPos && yPos;
    }

    public static void main(String[] args) {
        PhysicsEngine physicsEngine = new PhysicsEngine(" 0.4 * ( 0.9 - e ^ ( -1 * ( x ^ 2 + y ^ 2 ) / 8 ) )", 3, 0, 4, 1, 1, 0.6, 0.6, 0.3, 0.4, 0.0, 0.0);
        RBball = new GolfBall(new Vector3(10, 20, 12), Color.GOLD);
        Vector3 targetposition = new Vector3(4.0f,0.0f,1.0f);
        RuleBasedBot ruleBasedBot = new RuleBasedBot(RBball,targetposition, 1, physicsEngine, gameRules);
        System.out.println("Initial position: " + RuleBasedBot.RBball.getPosition());
        ruleBasedBot.update();
        System.out.println("New position after update: " + RuleBasedBot.RBball.getPosition());
    }
}
