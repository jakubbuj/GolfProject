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
        PhysicsEngine testEngine = new PhysicsEngine("2 * x - y", 5, 2, 4, 1, 0.15, 0.1, 0.2, 0.3, 0.4,0.5,0.0);

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

            updateStateVectorRungeKutta(false);

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

        double x = stateVector[0]; // X position on the plane
        double z = stateVector[1]; // Z position on the plane
    
        double xVelocity = stateVector[2]; // X velocity
        double zVelocity = stateVector[3]; // Z velocity

        // No vertical movement in Y since it's only for height, not affected by this physics simulation
        // The slope calculation methods need to work with the ground plane (x, z), not vertical (y)
        double slopeX = calculateDerivativeX(x, z); // Should calculate the derivative on the plane
        double slopeZ = calculateDerivativeZ(x, z); // Should calculate the derivative on the plane

        // Friction coefficients
        double kineticCoefficient = GRASS_K; // Kinetic coefficient for grass
        // Use sand kinetic coefficient if within sand area, assuming a method isWithinSandArea(x, z)
        // if (isWithinSandArea(x, z)) {
        //     kineticCoefficient = SAND_K;
        // }

        // Calculate forces based on the slopes and friction coefficients on the plane
        double forceX = -kineticCoefficient * g * slopeX;
        double forceZ = -kineticCoefficient * g * slopeZ;
        double xSecondTerm;
        double ySecondTerm;
        if (isImmobile(x, z)) {

            xSecondTerm = -kineticCoefficient * g * (slopeX / Math.sqrt(slopeX * slopeX + forceZ * forceZ));
            ySecondTerm = -kineticCoefficient * g * (forceZ / Math.sqrt(slopeX * slopeX + forceZ * forceZ));

        } else {

            xSecondTerm = -kineticCoefficient * g
                    * (xVelocity / Math.sqrt(xVelocity * xVelocity + zVelocity * zVelocity));
            ySecondTerm = -kineticCoefficient * g
                    * (zVelocity / Math.sqrt(xVelocity * xVelocity + zVelocity * zVelocity));

        }
        double xAcceleration = forceX + xSecondTerm;
        double yAcceleration = forceZ + ySecondTerm;

        systemFunction[0] = stateVector[2];
        systemFunction[1] = stateVector[3];
        systemFunction[2] = xAcceleration;
        systemFunction[3] = yAcceleration;

        for (int i = 0; i < stateVector.length; i++) {
            stateVector[i] += h * systemFunction[i];
        }

    }

    // Checking if the ball is not moving
    public boolean isImmobile(double x, double y) {
        double staticCoefficient = GRASS_S;
        /*
         * if (isWithinSandArea(x, y)) {
         * staticCoefficient = SAND_S;
         * } else {
         * staticCoefficient = GRASS_S;
         * }
         */

        // Calculating partial derivatives of the terrain at (X0, Y0)
        double slopeX = calculateDerivativeX(x, y);
        double forceZ = calculateDerivativeZ(x, y);

        // Return true if the static friction coefficient is greater than the slope
        // magnitude
        return staticCoefficient > Math.sqrt(slopeX * slopeX + forceZ * forceZ);
    }

    // Helper method to check if a position is within the sand area
    // private boolean isWithinSandArea(double x, double y) {

    // }

    // Calculating the partial derivative with respect to x
    private double calculateDerivativeX(double x, double y) {
        return (Math.abs(GetHeight.getHeight(heightFunction, x - LIMIT_ZERO, y)
                - GetHeight.getHeight(heightFunction, x + LIMIT_ZERO, y))) / (2 * LIMIT_ZERO);
    }

    // Calculating the partial derivative with respect to Y
    private double calculateDerivativeZ(double x, double z) {
        return (Math.abs(GetHeight.getHeight(heightFunction, x, z - LIMIT_ZERO)
                - GetHeight.getHeight(heightFunction, x, z + LIMIT_ZERO))) / (2 * LIMIT_ZERO);
    }

    // In PhysicsEngine.java

    public void runSingleStep(double dt, Vector3 ballPosition, Vector3 ballVelocity) {
        //filling statevectors with values
        stateVector[0] = ballPosition.x;
        stateVector[1] = ballPosition.z;     
        stateVector[2] = ballVelocity.x;
        stateVector[3] = ballVelocity.z;

        //updating state vectors
        updateStateVectorRungeKutta(false);

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