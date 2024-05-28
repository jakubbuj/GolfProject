package com.game;
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

        Vector2 vectorBT = new Vector2(ballPosition.x - targetPosition.x, ballPosition.z - targetPosition.z); //vector from ball position to target position
        double magnitude = Math.sqrt(Math.pow(vectorBT.x, 2) + Math.pow(vectorBT.y, 2));
        //Normalizing vectorBT to get direction vector of the ball
        Vector2 direction = new Vector2((float)(-vectorBT.x / magnitude), (float)(-vectorBT.y / magnitude));

        double slopeX = PhysicsEngine.calculateDerivativeX(ballPosition.x, ballPosition.z);
        double slopeZ = PhysicsEngine.calculateDerivativeZ(ballPosition.x, ballPosition.z);
        double slopeForceX = 1;
        double slopeForceZ = 1;
        if (slopeX > 0.2){ //We apply additional force only when the slope is big enough. 
            slopeForceX = slopeX * 10;
         } //else if (slopeX < 0.1){
        //     slopeForceX = 0.7;
        // }
        if (slopeZ > 0.2){
            slopeForceZ = slopeZ * 10;
        } //else if (slopeZ < 0.1){
        //     slopeForceZ = 0.8;
        // }

        double force;
        if (magnitude < 7 && magnitude > 1) force = magnitude * 1.5; //Close to the target, the magnitude will be small, so we need stronger force in comparison to the magnitude to move the ball significantly.
        else if (magnitude <= 1) force = magnitude + 1; //Very close to the target, we need force to be stronger with respect to the magnitude, but not too strong to not overshoot the target.
        else force = 0.75 * magnitude; 
        Vector3 newVelocity = new Vector3( (float)(direction.x * force * slopeForceX), (float)(0.0), (float)(direction.y * force * slopeForceZ));
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
        RBball.getPosition().y = (float) (physicsEngine.terrainHeight); // Keeping the ball slightly above the terrain

    }

    private boolean reachedTarget(){
        boolean xPos = RBball.getPosition().x <= targetPosition.x + targetRadius && RBball.getPosition().x >= targetPosition.x - targetRadius;
        boolean yPos = RBball.getPosition().y <= targetPosition.y + targetRadius && RBball.getPosition().y >= targetPosition.y - targetRadius;
        return xPos && yPos;
    }

}
