import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class implements the 4th order Runge-Kutta method for solving a system 
 * of first-order ordinary differential equations (ODEs).
 */
public class Runge_Kutta_4th_ODE {

    HandleDerivatives handlederivatives;
    private double startTime = 0;
    private double endTime = 0;
    private double stepSize = 0;
    private List<Double> initialValues = new ArrayList<>();
    private List<String> rawDerivatives;

    /**
     * Constructor to initialize the Runge-Kutta 4th order solver with the specified parameters.
     * 
     * @param startTime      The start time of the simulation.
     * @param endTime        The end time of the simulation.
     * @param stepSize       The time step size.
     * @param initialValues  The initial values of the variables.
     * @param rawDerivatives The derivatives of the variables as strings.
     */
    Runge_Kutta_4th_ODE(double startTime, double endTime, double stepSize, List<Double> initialValues, List<String> rawDerivatives) {
        this.initialValues = initialValues;
        this.endTime = endTime;
        this.startTime = startTime;
        this.stepSize = stepSize;
        this.rawDerivatives = rawDerivatives;
    }

    /**
     * Solves the system of ODEs using the 4th order Runge-Kutta method.
     * 
     * @return A 2D array containing the evolution of values through calculations.
     */
    public double[][] rungeSolver() {
        double time = startTime + stepSize;
        int dimensions = initialValues.size();
        int steps = (int) (endTime / stepSize);

        double[][] evolution = new double[steps + 1][dimensions];
        double[] currentstate = new double[initialValues.size()];
        for (int i = 0; i < initialValues.size(); i++) {
            currentstate[i] = initialValues.get(i);
        }

        double[] temporaryState = new double[initialValues.size()];

        for (int i = 0; i <= steps; i++) {
            evolution[i] = currentstate.clone();

            HandleDerivatives k1 = new HandleDerivatives(rawDerivatives, time);
            double[] k1Values = k1.calculate(currentstate);

            for (int j = 0; j < initialValues.size(); j++) {
                temporaryState[j] = currentstate[j] + (k1Values[j] * (stepSize / 2));
            }

            HandleDerivatives k2 = new HandleDerivatives(rawDerivatives, time + (1 / 2));
            double[] k2Values = k2.calculate(temporaryState);

            for (int j = 0; j < initialValues.size(); j++) {
                temporaryState[j] = currentstate[j] + (k2Values[j] * (stepSize / 2));
            }

            HandleDerivatives k3 = new HandleDerivatives(rawDerivatives, time + (1 / 2));
            double[] k3Values = k3.calculate(temporaryState);

            for (int j = 0; j < initialValues.size(); j++) {
                temporaryState[j] = currentstate[j] + k3Values[j];
            }

            HandleDerivatives k4 = new HandleDerivatives(rawDerivatives, time + stepSize);
            double[] k4Values = k4.calculate(temporaryState);

            double[] averageSlopes = new double[dimensions];
            for (int l = 0; l < dimensions; l++) {
                averageSlopes[l] = (1.0 / 6.0) * (k1Values[l] + 2 * k2Values[l] + 2 * k3Values[l] + k4Values[l]);
            }

            for (int k = 0; k < dimensions; k++) {
                currentstate[k] += stepSize * averageSlopes[k];
            }
            time += stepSize;
        }

        return evolution;
    }

    /**
     * Main method to run the Runge-Kutta 4th order solver.
     * 
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        double startTime = 0.0;
        double endTime = 1.0;
        double stepSize = 0.1;
        List<Double> initialValues = Arrays.asList(1.0, 0.0); // Example initial values
        List<String> rawDerivatives = Arrays.asList("b", "a 6 * b -"); // Example derivatives as strings

        Runge_Kutta_4th_ODE solver = new Runge_Kutta_4th_ODE(startTime, endTime, stepSize, initialValues, rawDerivatives);
        double[][] evolution = solver.rungeSolver();

        // Display results
        for (double[] step : evolution) {
            for (double value : step) {
                System.out.print(value + " ");
            }
            System.out.println();
        }
    }
}
