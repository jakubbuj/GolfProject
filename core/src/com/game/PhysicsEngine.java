package com.game;

import com.badlogic.gdx.math.Vector3;

public class PhysicsEngine {

    double X0, Y0; // initial position of the ball
    double Xt, Yt, Rt; // position of the target and it's radius
    double GRASS_K, GRASS_S; // kinetic and static coefficients on the grass
    double SAND_K, SAND_S; // kinetic and static coefficients on the sand
    public static String heightFunction; // h(x,y) function of the height profile
    double xMapStart = 0, xMapEnd = 50, yMapStart = 0, yMapEnd = 50; // x and y map limits
    double maxVelocity = 5;
    double xInitialVelocity=0,  zInitialVelocity=0;
    double terrainHeight=0;

    final double g = 9.80665;
    final double LIMIT_ZERO = 0.0000001;
    final double h = 0.01; // step size

    double[] stateVector = new double[4];
    double[] systemFunction = new double[4];

    public PhysicsEngine(String heightFunction, double X0, double Y0, double Xt, double Yt, double Rt, double GRASS_K,
            double GRASS_S, double SAND_K, double SAND_S, double xInitialVelocity, double zInitialVelocity) {

        PhysicsEngine.heightFunction = heightFunction;
        this.X0 = X0;
        this.Y0 = Y0;
        this.Xt = Xt;
        this.Yt = Yt;
        this.Rt = Rt;
        this.GRASS_K = GRASS_K;
        this.GRASS_S = GRASS_S;
        this.SAND_K = SAND_K;
        this.SAND_S = SAND_S;
        this.xInitialVelocity=xInitialVelocity;
        this.zInitialVelocity=zInitialVelocity;

    }

    public static void main(String[] args) {
        PhysicsEngine testEngine = new PhysicsEngine(" sqrt ( ( sin x + cos y ) ^ 2 )", 5, 2, 4, 1, 0.15, 1, 0.5, 0.3, 0.4,0.5,0.0);

        testEngine.runSimulation(0.5, 0);
       // testEngine.runSimulation(10, 10);

    }


    public void runSimulation(double xInitialVelocity, double yInitialVelocity) {

        stateVector[0] = X0;
        stateVector[1] = Y0;
        stateVector[2] = xInitialVelocity;
        stateVector[3] = yInitialVelocity;

        while (true) {

            System.out.println("X position: " + stateVector[0] + ", Y position: " + stateVector[1] + ", X velocity: "
                    + stateVector[2] + ", Y velocity: " + stateVector[3] + ", height: "
                     + GetHeight.getHeight(heightFunction, stateVector[0], stateVector[1]));

            // if ((stateVector[0] > (Xt - Rt) && stateVector[0] < (Xt + Rt))
            //         && (stateVector[1] > (Yt - Rt) && stateVector[1] < (Yt + Rt))) {

            //     System.out.println("Ball reached the target!");
            //     break;

            // }

            // if (stateVector[0] > xMapEnd || stateVector[0] < xMapStart || stateVector[1] > yMapEnd
            //         || stateVector[1] < yMapStart) {

            //     System.out.println("Ball fell out of the map!");
            //     break;

            // }

            // if (GetHeight.getHeight(heightFunction, stateVector[0], stateVector[1]) < 0) {

            //     System.out.println("Ball fell in water!");
            //     break;

            // }

            // if (Math.abs(stateVector[2]) < h && Math.abs(stateVector[3]) < h) {

            //     stateVector[2] = 0;
            //     stateVector[3] = 0;
            //     while (!isImmobile(stateVector[0], stateVector[1])) {
            //         updateStateVectorRungeKutta(false);
            //     }
            //     System.out.println("Ball stopped!");

            //     if ((stateVector[0] > (Xt - Rt) && stateVector[0] < (Xt + Rt))
            //             && (stateVector[1] > (Yt - Rt) && stateVector[1] < (Yt + Rt))) {
            //         System.out.println("Ball reached the target!");
            //     }
            //     break;
            // }

            updateStateVectorEuler(false);
        }

    }

    public void updateStateVectorRungeKutta(boolean isImmobile) {
        double x = stateVector[0];
        double z = stateVector[1];

        double kineticCoefficient = GRASS_K; // Assuming the object is on grass
        // Calculate the next state vector using the Runge-Kutta method
        double[] stateVector1 = returnUpdatedVector(isImmobile, x, z, kineticCoefficient);
        double[] stateVector2 = returnUpdatedVector(isImmobile, x + h / 2, z + stateVector1[1] / 2, kineticCoefficient);
        double[] stateVector3 = returnUpdatedVector(isImmobile, x + h / 2, z + stateVector2[1] / 2, kineticCoefficient);
        double[] stateVector4 = returnUpdatedVector(isImmobile, x + h, z + stateVector3[1], kineticCoefficient);
        
        double[] averageVector = new double[4];
        for (int i = 0; i < stateVector.length; i++) {
            averageVector[i] = (1.0 / 6.0) * (stateVector1[i] + 2 * stateVector2[i] + 2 * stateVector3[i] + stateVector4[i]);
            stateVector[i] += h * averageVector[i];
        }
    
    }
    
    public double[] returnUpdatedVector(boolean isImmobile, double x, double z, double kineticCoefficient) {
        double xVelocity = stateVector[2];
        double zVelocity = stateVector[3];
    
        double slopeX = calculateDerivativeX(x, z);
        double slopeZ = calculateDerivativeZ(x, z);
    
        double xFirstTerm = -g * slopeX;
        double zFirstTerm = -g * slopeZ;
    
        double xSecondTerm;
        double zSecondTerm;
    
        // Determine friction components based on motion state
        if (isImmobile(x, z)) {
            // Friction acts against the slope
            xSecondTerm = -kineticCoefficient * g * (slopeX / Math.sqrt(slopeX * slopeX + slopeZ * slopeZ));
            zSecondTerm = -kineticCoefficient * g * (slopeZ / Math.sqrt(slopeX * slopeX + slopeZ * slopeZ));
        } else {
            // Friction acts against velocity
            xSecondTerm = -kineticCoefficient * g * (xVelocity / Math.sqrt(xVelocity * xVelocity + zVelocity * zVelocity));
            zSecondTerm = -kineticCoefficient * g * (zVelocity / Math.sqrt(xVelocity * xVelocity + zVelocity * zVelocity));
        }
    
        // Calculate total acceleration
        double xAcceleration = xFirstTerm + xSecondTerm;
        double zAcceleration = zFirstTerm + zSecondTerm;
    
        // Update system function array
        systemFunction[0] = stateVector[2]; // xVelocity
        systemFunction[1] = stateVector[3]; // zVelocity
        systemFunction[2] = xAcceleration;
        systemFunction[3] = zAcceleration;
    
        return systemFunction;
    }
    

    public void updateStateVectorEuler(boolean isImmobile) {
        double x = stateVector[0];
        double z = stateVector[1];
    
        double xVelocity = stateVector[2];
        double zVelocity = stateVector[3];
    
        double normVelocity = Math.sqrt(xVelocity * xVelocity + zVelocity * zVelocity);
    
        // Calculate the slope of the height field at the current position
        double slopeX = calculateDerivativeX(x, z);
        double slopeZ = calculateDerivativeZ(x, z);
    
        // Determine the coefficient of kinetic friction based on the terrain
        double kineticCoefficient = isWithinSandArea(x, z) ? SAND_K : GRASS_K;
    
        // Calculate the force of gravity acting along the slope
        double gravityForceX = -g * slopeX;
        double gravityForceZ = -g * slopeZ;
    
        // Calculate friction force magnitude; it should oppose the velocity vector
        double frictionForceX = normVelocity > LIMIT_ZERO ? kineticCoefficient * g * (xVelocity / normVelocity) : 0;
        double frictionForceZ = normVelocity > LIMIT_ZERO ? kineticCoefficient * g * (zVelocity / normVelocity) : 0;
    
        // Update accelerations by combining gravitational and frictional forces
        double xAcceleration = gravityForceX - frictionForceX;
        double zAcceleration = gravityForceZ - frictionForceZ;
    
        // Apply Euler's method to update velocities and positions
        stateVector[2] += xAcceleration * h;  // Update x velocity
        stateVector[3] += zAcceleration * h;  // Update z velocity
    
        // Update positions based on new velocities
        stateVector[0] += stateVector[2] * h;
        stateVector[1] += stateVector[3] * h;
    
        // Check if the velocity is low enough to consider the ball stopped
        if (Math.sqrt(stateVector[2] * stateVector[2] + stateVector[3] * stateVector[3]) < LIMIT_ZERO) {
            stateVector[2] = 0;
            stateVector[3] = 0;
        }
    }
    
    
    private boolean isWithinSandArea(double x, double z) {
        // Assume a method that checks if coordinates are in a sand area
        return false; // Placeholder: Implement the actual area check based on your game map
    }
    
    // Check if the ball is immobile based on the static friction exceeding the force due to slope
    public boolean isImmobile(double x, double z) {
        double slopeX = calculateDerivativeX(x, z);
        double slopeZ = calculateDerivativeZ(x, z);
        double normSlope = Math.sqrt(slopeX * slopeX + slopeZ * slopeZ);
        double staticCoefficient = isWithinSandArea(x, z) ? SAND_S : GRASS_S;
        return staticCoefficient * g > normSlope;
    }

    private double calculateDerivativeX(double x, double y) {
        double forwardHeight = GetHeight.getHeight(heightFunction, x + LIMIT_ZERO, y);
        double backwardHeight = GetHeight.getHeight(heightFunction, x - LIMIT_ZERO, y);
        return (forwardHeight - backwardHeight) / (2 * LIMIT_ZERO);
    }
    
    private double calculateDerivativeZ(double x, double z) {
        double forwardHeight = GetHeight.getHeight(heightFunction, x, z + LIMIT_ZERO);
        double backwardHeight = GetHeight.getHeight(heightFunction, x, z - LIMIT_ZERO);
        return (forwardHeight - backwardHeight) / (2 * LIMIT_ZERO);
    }
    

    // In PhysicsEngine.java

    public void runSingleStep(double dt, Vector3 ballPosition, Vector3 ballVelocity) {
        //filling statevectors with values
        stateVector[0] = ballPosition.x;
        stateVector[1] = ballPosition.z;     
        stateVector[2] = ballVelocity.x;
        stateVector[3] = ballVelocity.z;

        //updating state vectors
        updateStateVectorEuler(false);

        // checking if any of the states in nan
        boolean hasNaN = false;
        for (double value : stateVector) {
            if (Double.isNaN(value)) {
                hasNaN = true;
                break;
            }
        }
    
        // If any value is NaN, revert to the previous valid state
        if (hasNaN) {
            stateVector[0] = ballPosition.x;
            stateVector[1] = ballPosition.z;
            stateVector[2] = ballVelocity.x;
            stateVector[3] = ballVelocity.z;
        }

        // Update height to keep the ball on the terrain
        terrainHeight = (float) GetHeight.getHeight(heightFunction, ballPosition.x, ballPosition.z);
    }

    public double[] getStateVector() {
        return stateVector;
    }
    
}