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
        System.out.println("asdasd");

    }

    //set desired initailvelocities and position of the ball
    public void setState(double x0, double y0, double vx0, double vy0) {
        this.X0 = x0;
        this.Y0 = y0;
        this.xInitialVelocity = vx0;
        this.zInitialVelocity = vy0;
    }

    public double[] runSimulation(double xInitialVelocity, double yInitialVelocity) {
        // Initialize the state vector for position and velocity
        stateVector[0] = X0;
        stateVector[1] = Y0;
        stateVector[2] = xInitialVelocity;
        stateVector[3] = yInitialVelocity;

        // Simulation loop: run until both velocities are close to zero
        while (Math.sqrt(stateVector[2] * stateVector[2] + stateVector[3] * stateVector[3]) >= LIMIT_ZERO) {
            updateStateVectorRungeKutta(false);
        }

        // Optionally print final stopping position of the ball
        System.out.println("Final position: X = " + stateVector[0] + ", Y = " + stateVector[1]);

        return stateVector;
    }

    public void updateStateVectorRungeKutta(boolean isImmobile) {
        double x = stateVector[0];
        double z = stateVector[1];

        double kineticCoefficient = isWithinSandArea(x, z) ? SAND_K : GRASS_K;
        // Calculate the next state vector using the Runge-Kutta method

        double [] stateVector1 = new double[4];
        double [] stateVector2 = new double[4];
        double [] stateVector3 = new double[4];
        double [] stateVector4 = new double[4];

        double [] averageVector = new double[4];

        for (int i = 0; i < stateVector.length; i++){
            if (i%2 == 0){
            // Update x fields of the state vector : stateVector[0] and stateVector[2]
                stateVector1 = returnUpdatedVector(isImmobile, x, z, kineticCoefficient);
                stateVector2 = returnUpdatedVector(isImmobile, x + 0.5 * stateVector1[0], z, kineticCoefficient);
                stateVector3 = returnUpdatedVector(isImmobile, x + 0.5 * stateVector2[0], z, kineticCoefficient);
                stateVector4 = returnUpdatedVector(isImmobile, x + stateVector3[0], z, kineticCoefficient);
            } else {
            // Update y fields of the state vector : stateVector[1] and stateVector[3]
                stateVector1 = returnUpdatedVector(isImmobile, x, z, kineticCoefficient);
                stateVector2 = returnUpdatedVector(isImmobile, x, z + 0.5 * stateVector1[1], kineticCoefficient);
                stateVector3 = returnUpdatedVector(isImmobile, x, z + 0.5 * stateVector2[1], kineticCoefficient);
                stateVector4 = returnUpdatedVector(isImmobile, x, z + stateVector3[1], kineticCoefficient);
            }
            averageVector[i] = (1.0/6.0) * (stateVector1[i] + 2 * stateVector2[i] + 2 * stateVector3[i] + stateVector4[i]);
        }
        
        stateVector[0] += h * stateVector[2];
        stateVector[1] += h * stateVector[3];
        stateVector[2] += h * averageVector[2];
        stateVector[3] += h * averageVector[3];

        // Check if the velocity is low enough to consider the ball stopped
        if (Math.sqrt(stateVector[2] * stateVector[2] + stateVector[3] * stateVector[3]) < LIMIT_ZERO) {
            stateVector[2] = 0;
            stateVector[3] = 0;
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
    
       double normVelocity = Math.sqrt(xVelocity * xVelocity + zVelocity * zVelocity);
        // Determine friction components based on motion state
        if (normVelocity <= LIMIT_ZERO) {
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
        double frictionForceX = normVelocity > LIMIT_ZERO ? kineticCoefficient * g * (xVelocity / normVelocity) : kineticCoefficient * g * (slopeX/ Math.sqrt(slopeX * slopeX + slopeZ * slopeZ));
        double frictionForceZ = normVelocity > LIMIT_ZERO ? kineticCoefficient * g * (zVelocity / normVelocity) : kineticCoefficient * g * (slopeZ/ Math.sqrt(slopeX * slopeX + slopeZ * slopeZ));
    
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
    // public boolean isImmobile(double x, double z) {
    //     double slopeX = calculateDerivativeX(x, z);
    //     double slopeZ = calculateDerivativeZ(x, z);
    //     double normSlope = Math.sqrt(slopeX * slopeX + slopeZ * slopeZ);
    //     double staticCoefficient = isWithinSandArea(x, z) ? SAND_S : GRASS_S;
    //     return staticCoefficient * g > normSlope;
    // }

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
    


    public double[] runSingleStep(Vector3 ballPosition, Vector3 ballVelocity) {
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

        return stateVector;
    }
    

    public double[] getStateVector() {
        return stateVector;
    }
    
}