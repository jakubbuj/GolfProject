import java.util.ArrayList;
import java.util.List;

public class Midpoint_Method_for_2nd_ODE {

    HandleDerivatives handlederivatives;
    GUI gui;
    private double startTime = 0;
    private double endTime = 0;
    private double stepSize = 0;
    private List<Double> initialValues = new ArrayList<>();
    private List<String> rawDerivatives;

    Midpoint_Method_for_2nd_ODE(double startTime, double endTime, double stepSize, List<Double> initialValues,
                                List<String> rawDerivatives) {
        this.initialValues = initialValues;
        this.endTime = endTime;
        this.startTime = startTime;
        this.stepSize = stepSize;
        this.rawDerivatives = rawDerivatives;
    }

    // return 2d array containing evolution of values through calculation
    public double[][] solver() {
        double time = startTime + stepSize;
        int dimensions = initialValues.size();
        int steps = (int) ((endTime - startTime) / stepSize);

        // contains every number for all derivatives in each step
        double[][] evolution = new double[steps + 1][dimensions];

        // holds all values of the variables at current time
        double[] currentstate = new double[initialValues.size()];
        for (int i = 0; i < initialValues.size(); i++) {
            currentstate[i] = initialValues.get(i);
        }

        for (int i = 0; i <= steps; i++) { // iterate over each step
            evolution[i] = currentstate.clone();

            // Calculate derivatives at the midpoint
            double[] k1 = new double[dimensions];
            double[] k2 = new double[dimensions];
            double[] midpointState = new double[dimensions];

            handlederivatives = new HandleDerivatives(rawDerivatives, time - 0.5 * stepSize); 
            double[] derivativeValues = handlederivatives.calculate(currentstate); 

            for (int k = 0; k < dimensions; k++) { // iterate over each dimension to calculate k1
                k1[k] = stepSize * derivativeValues[k];
                midpointState[k] = currentstate[k] + 0.5 * k1[k]; // calculate midpoint state
            }

            handlederivatives = new HandleDerivatives(rawDerivatives, time); 
            derivativeValues = handlederivatives.calculate(midpointState); 

            for (int k = 0; k < dimensions; k++) { // iterate over each dimension to calculate k2
                k2[k] = stepSize * derivativeValues[k];
            }

            for (int k = 0; k < dimensions; k++) { // iterate over each dimension to update variables
                currentstate[k] += k2[k]; // update variables
            }
            time += stepSize; 
        }

        return evolution;

    }

}
