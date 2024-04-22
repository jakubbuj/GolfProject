

public class PhysicsEngine {
    
    double X0, Y0; // initial position of the ball
    double Xt, Yt, Rt; // position of the target and it's radius
    double GRASS_K, GRASS_S; // kinetic and static coefficients on the grass
    double SAND_K, SAND_S; // kinetic and static coefficients on the sand
    String heightFunction; // h(x,y) function of the height profile
    double xMap = 100, yMap = 100; // x and y map sizes
    double maxVelocity = 5;

    final double g = 9.80665;
    final double LIMIT_ZERO = 0.0000001;
    final double h = 0.01; // step size

    double[] stateVector = new double[4];
    double[] systemFunction = new double[4];
    
    public PhysicsEngine(String heightFunction, double X0, double Y0, double Xt, double Yt, double Rt, double GRASS_K, double GRASS_S, double SAND_K, double SAND_S){

        this.heightFunction = heightFunction;
        this.X0 = X0;
        this.Y0 = Y0; 
        this.Xt = Xt;
        this.Yt = Yt;
        this.Rt = Rt; 
        this.GRASS_K = GRASS_K;
        this.GRASS_S = GRASS_S; 
        this.SAND_K = SAND_K;
        this.SAND_S = SAND_S; 
        
    }

    public static void main(String[] args) {
        PhysicsEngine testEngine = new PhysicsEngine("2 * x - y", 50, 50, 99, 99, 1, 0.1, 0.5, 0.3, 0.4);

        testEngine.runSimulation(0.5, 0);
        testEngine.runSimulation(70, 8);
    }

    public void runSimulation(double xInitialVelocity, double yInitialVelocity){

        stateVector[0] = X0;
        stateVector[1] = Y0;
        stateVector[2] = xInitialVelocity;
        stateVector[3] = yInitialVelocity;

        while(true){

            System.out.println("X position: " + stateVector[0] + ", Y position: "  + stateVector[1] + ", X velocity: " + stateVector[2] + ", Y velocity: " + stateVector[3] + ", height: " + GetHeight.getHeight(heightFunction, stateVector[0], stateVector[1]));

            if ((stateVector[0] > (Xt - Rt) && stateVector[0] < (Xt + Rt)) && (stateVector[1] > (Yt - Rt) && stateVector[1] < (Yt + Rt))){

                System.out.println("Ball reached the target!");
                break;

            }

            if(stateVector[0] > xMap || stateVector[0] < 0 || stateVector[1] > yMap || stateVector[1] < 0){

                System.out.println("Ball fell out of the map!");
                break;

            }

            if (GetHeight.getHeight(heightFunction, stateVector[0], stateVector[1]) < 0){

                System.out.println("Ball fell in water!");
                break;

            }

           if (Math.abs(stateVector[2]) < h && Math.abs(stateVector[3]) < h){

                stateVector[2] = 0;
                stateVector[3] = 0;
                while(!isImmobile(stateVector[0], stateVector[1])){
                    updateStateVectorRungeKutta(false);
                }
                System.out.println("Ball stopped!");

                if ( (stateVector[0] > (Xt - Rt) && stateVector[0] < (Xt + Rt)) && (stateVector[1] > (Yt - Rt) && stateVector[1] < (Yt + Rt)) ){
                    System.out.println("Ball reached the target!");
                }
                break;
            }

            updateStateVectorRungeKutta(false);
            
        }

    }

    public void updateStateVectorRungeKutta(boolean isImmobile){

        double x = stateVector[0];
        double y = stateVector[1];

        double[] stateVector1 = new double[4];
        double[] stateVector2 = new double[4];
        double[] stateVector3 = new double[4];
        double[] stateVector4 = new double[4];

        double[] averageVector = new double[4];

        for (int i = 0; i < stateVector.length; i++){

            if (i%2 == 0){
                //updating x fields
                stateVector1 = returnUpdatedVector(isImmobile, stateVector, x, y);
                stateVector2 = returnUpdatedVector(isImmobile, stateVector1, x + 0.5 * h, y + 0.5 * stateVector1[1]);
                stateVector3 = returnUpdatedVector(isImmobile, stateVector2, x + 0.5 * h, y + 0.5 * stateVector2[1]);
                stateVector4 = returnUpdatedVector(isImmobile, stateVector3, x + h, y + stateVector3[1]);

            } else {
                //updating y fields
                stateVector1 = returnUpdatedVector(isImmobile, stateVector, x, y);
                stateVector2 = returnUpdatedVector(isImmobile, stateVector1, x + 0.5 * stateVector1[0], y + 0.5 * h);
                stateVector3 = returnUpdatedVector(isImmobile, stateVector2, x + 0.5 * stateVector2[0], y + 0.5 * h);
                stateVector4 = returnUpdatedVector(isImmobile, stateVector3, x + stateVector3[0], y + h);
            }

            averageVector[i] = (1.0/6.0) * (stateVector1[i] + 2 * stateVector2[i] + 2 * stateVector3[i] + stateVector4[i]);

        }

        systemFunction[0] = stateVector[2];
        systemFunction[1] = stateVector[3];
        systemFunction[2] = averageVector[2];
        systemFunction[3] = averageVector[3];

        for (int i = 0; i < stateVector.length; i++){
            stateVector[i] += h * systemFunction[i];
        }
    }

    public double[] returnUpdatedVector(boolean isImmobile, double[] stateVector, double x, double y){

        double xVelocity = stateVector[2];
        double yVelocity = stateVector[3];

        double kineticCoefficient = GRASS_S;
        /*if (isWithinSandArea(x, y)) {
            kineticCoefficient = SAND_S;
        } else {
            kineticCoefficient = GRASS_S;
        } */

        double slopeX = calculateDerivativeX(x, y);
        double slopeY = calculateDerivativeY(x, y);

        double xFirstTerm = -g * slopeX;
        double yFirstTerm = -g * slopeY;
        double xSecondTerm;
        double ySecondTerm;

        if (isImmobile(x, y)){

            xSecondTerm = -kineticCoefficient * g * (slopeX / Math.sqrt(slopeX * slopeX + slopeY * slopeY));
            ySecondTerm = -kineticCoefficient * g * (slopeY / Math.sqrt(slopeX * slopeX + slopeY * slopeY));

        } else {

            xSecondTerm = -kineticCoefficient * g * (xVelocity/ Math.sqrt(xVelocity * xVelocity + yVelocity * yVelocity));
            ySecondTerm = -kineticCoefficient * g * (yVelocity/ Math.sqrt(xVelocity * xVelocity + yVelocity * yVelocity));

        }
        double xAcceleration = xFirstTerm + xSecondTerm;
        double yAcceleration = yFirstTerm + ySecondTerm;

        systemFunction[0] = stateVector[2];
        systemFunction[1] = stateVector[3];
        systemFunction[2] = xAcceleration;
        systemFunction[3] = yAcceleration;

        return systemFunction;
    }

    public void updateStateVectorEuler(boolean isImmobile){
        
        double x = stateVector[0];
        double y = stateVector[1];
        double xVelocity = stateVector[2];
        double yVelocity = stateVector[3];

        double slopeX = calculateDerivativeX(x, y);
        double slopeY = calculateDerivativeY(x, y);

        double kineticCoefficient = GRASS_S;
        /*if (isWithinSandArea(x, y)) {
            kineticCoefficient = SAND_S;
        } else {
            kineticCoefficient = GRASS_S;
        }  */ 

        double xFirstTerm = -g * slopeX;
        double yFirstTerm = -g * slopeY;
        double xSecondTerm;
        double ySecondTerm;
        if (isImmobile(x, y)){

            xSecondTerm = -kineticCoefficient * g * (slopeX / Math.sqrt(slopeX * slopeX + slopeY * slopeY));
            ySecondTerm = -kineticCoefficient * g * (slopeY / Math.sqrt(slopeX * slopeX + slopeY * slopeY));

        } else {

            xSecondTerm = -kineticCoefficient * g * (xVelocity/ Math.sqrt(xVelocity * xVelocity + yVelocity * yVelocity));
            ySecondTerm = -kineticCoefficient * g * (yVelocity/ Math.sqrt(xVelocity * xVelocity + yVelocity * yVelocity));

        }
        double xAcceleration = xFirstTerm + xSecondTerm;
        double yAcceleration = yFirstTerm + ySecondTerm;

        systemFunction[0] = stateVector[2];
        systemFunction[1] = stateVector[3];
        systemFunction[2] = xAcceleration;
        systemFunction[3] = yAcceleration;
        
        for (int i = 0; i < stateVector.length; i++){
            stateVector[i] += h * systemFunction[i];
        }

    }

    // Checking if the ball is not moving
    public boolean isImmobile(double x, double y) {
        double staticCoefficient = GRASS_S;
        /*if (isWithinSandArea(x, y)) {
         staticCoefficient = SAND_S;
        } else {
         staticCoefficient = GRASS_S;
        }   
        */

        // Calculating partial derivatives of the terrain at (X0, Y0)
        double slopeX = calculateDerivativeX(x, y);
        double slopeY = calculateDerivativeY(x, y);

        // Return true if the static friction coefficient is greater than the slope magnitude
        return staticCoefficient > Math.sqrt(slopeX * slopeX + slopeY * slopeY);
    }

    // Helper method to check if a position is within the sand area
    //private boolean isWithinSandArea(double x, double y) {
        
    //}

    // Calculating the partial derivative with respect to x
    private double calculateDerivativeX(double x, double y) {
        return (Math.abs(GetHeight.getHeight(heightFunction, x - LIMIT_ZERO, y) - GetHeight.getHeight(heightFunction, x + LIMIT_ZERO, y))) / (2 * LIMIT_ZERO);
    }

    // Calculating the partial derivative with respect to Y
    private double calculateDerivativeY(double x, double y) {
        return (Math.abs(GetHeight.getHeight(heightFunction, x, y - LIMIT_ZERO) - GetHeight.getHeight(heightFunction, x, y + LIMIT_ZERO))) / (2 * LIMIT_ZERO);
    }

}