import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class implements the Euler method for solving a system of first-order 
 * ordinary differential equations (ODEs).
 */
public class Euler_Method_for_1st_ODE {

    HandleDerivatives handlederivatives;
    private double startTime = 0;
    private double endTime = 0;
    private double stepSize = 0;
    private List<Double> initialValues = new ArrayList<>();
    private List<String> rawDerivatives;

    /**
     * Constructor to initialize the Euler method solver with the specified parameters.
     * 
     * @param startTime      The start time of the simulation.
     * @param endTime        The end time of the simulation.
     * @param stepSize       The time step size.
     * @param initialValues  The initial values of the variables.
     * @param rawDerivatives The derivatives of the variables as strings.
     */
    Euler_Method_for_1st_ODE(double startTime, double endTime, double stepSize, List<Double> initialValues,
                             List<String> rawDerivatives) {
        this.initialValues = initialValues;
        this.endTime = endTime;
        this.startTime = startTime;
        this.stepSize = stepSize;
        this.rawDerivatives = rawDerivatives;
    }

    /**
     * Solves the system of ODEs using the Euler method.
     * 
     * @return A 2D array containing the evolution of values through calculations.
     */
    public double[][] solver() {
        double time = startTime + stepSize;
        int dimensions = initialValues.size();
        int steps = (int) (endTime / stepSize);

        double[][] evolution = new double[steps + 1][dimensions];
        double[] currentstate = new double[initialValues.size()];
        for (int i = 0; i < initialValues.size(); i++) {
            currentstate[i] = initialValues.get(i);
        }

        for (int i = 0; i <= steps; i++) {
            evolution[i] = currentstate.clone();

            handlederivatives = new HandleDerivatives(rawDerivatives, time);
            double[] derivativeValues = handlederivatives.calculate(currentstate);

            for (int k = 0; k < dimensions; k++) {
                currentstate[k] += stepSize * derivativeValues[k];
            }
            time += stepSize;
        }

        return evolution;
    }

    /**
     * Main method to run the Euler method solver.
     * 
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        double startTime = 0.0;
        double endTime = 10.0;
        double stepSize = 0.1;
        List<Double> initialValues = Arrays.asList(1.0, 0.0); // Example initial values
        List<String> rawDerivatives = Arrays.asList("b", "a 6 * b -"); // Example derivatives as strings

        Euler_Method_for_1st_ODE solver = new Euler_Method_for_1st_ODE(startTime, endTime, stepSize, initialValues,
                rawDerivatives);
        double[][] evolution = solver.solver();

        // Display results
        for (double[] step : evolution) {
            for (double value : step) {
                System.out.print(value + " ");
            }
            System.out.println();
        }
    }
}
