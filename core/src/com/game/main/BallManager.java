package com.game.main;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.game.golfball.*;
import com.game.terrain.GameRules;

public class BallManager {
    private GolfBall ball;
    private GolfBall AIball;
    private GolfBall RBball;
    private GolfBallMovement ballMovement;
    private GolfAI golfAI;
    private RuleBasedBot ruleBasedBot;

    public BallManager(PhysicsEngine physicsEngine, GameRules gameRules, GameRules gameRulesAI, GameRules gameRulesRB, Vector3 initialPosition, Vector3 targetPosition, float targetRadius) {
        ball = new GolfBall(initialPosition, Color.valueOf("2e3d49"));
        AIball = new GolfBall(initialPosition, Color.valueOf("007d8d"));
        RBball = new GolfBall(initialPosition, Color.valueOf("880808"));
        
        ballMovement = new GolfBallMovement(ball, physicsEngine, gameRules);
        golfAI = new GolfAI(AIball, targetPosition, physicsEngine, gameRulesAI);
        ruleBasedBot = new RuleBasedBot(RBball, targetPosition, targetRadius, physicsEngine, gameRulesRB);
    }

    public GolfBall getBall() {
        return ball;
    }

    public GolfBall getAIball() {
        return AIball;
    }

    public GolfBall getRBball() {
        return RBball;
    }

    public GolfBallMovement getBallMovement() {
        return ballMovement;
    }

    public GolfAI getGolfAI() {
        return golfAI;
    }

    public RuleBasedBot getRuleBasedBot() {
        return ruleBasedBot;
    }
}
