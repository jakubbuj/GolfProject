import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class experiments with the 4th order Runge-Kutta method for solving a system of first-order
 * ordinary differential equations (ODEs). It measures computation time and accuracy for different step sizes.
 */
public class RungeKuttaExperiment {
    /**
     * Main method to run the Runge-Kutta method experiments with different step sizes.
     * 
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        double startTime = 0.0;
        double endTime = 1.0;
        List<Double> initialValues = Arrays.asList(1.0, 0.0); // y(0) = 1, y'(0) = 0
        List<String> rawDerivatives = Arrays.asList("b", "a 6 * b -"); // dy1/dx = y2, dy2/dx = 6*y1 - y2 (RPN)

        double[] stepSizes = {0.1, 0.01, 0.001}; // Step sizes for the experiment

        System.out.println("Step size\tComputation time (s)\tRoot Mean Squared Error");

        for (double stepSize : stepSizes) {
            long startTimeNano = System.nanoTime(); // Start the timer

            Runge_Kutta_4th_ODE solver = new Runge_Kutta_4th_ODE(startTime, endTime, stepSize, initialValues, rawDerivatives);
            double[][] evolution = solver.rungeSolver();

            long endTimeNano = System.nanoTime(); // End the timer
            long computationTime = endTimeNano - startTimeNano; // Calculate computation time
            double computationTimeSeconds = computationTime / 1e9; // Convert to seconds

            double error = calculateRMSE(evolution, endTime, stepSize);

            System.out.println(stepSize + "\t" + computationTimeSeconds + "\t" + error);
        }
    }

    /**
     * Calculates the Root Mean Squared Error (RMSE) between the numerical solution and the analytical solution.
     * 
     * @param evolution 2D array containing the evolution of values through calculations.
     * @param endTime The end time of the simulation.
     * @param stepSize The time step size.
     * @return The RMSE value.
     */
    public static double calculateRMSE(double[][] evolution, double endTime, double stepSize) {
        int steps = (int) (endTime / stepSize);
        double sumSquaredErrors = 0.0;
        for (int i = 0; i <= steps; i++) {
            double time = i * stepSize;
            double analyticalValue = analyticalSolution(time);
            double numericalValue = evolution[i][0];
            double error = numericalValue - analyticalValue;
            sumSquaredErrors += error * error;
        }
        return Math.sqrt(sumSquaredErrors / (steps + 1));
    }

    /**
     * Analytical solution of the differential equation for comparison.
     * 
     * @param x The input value for the analytical solution.
     * @return The value of the analytical solution at x.
     */
    public static double analyticalSolution(double x) {
        double C1 = 3.0 / 5.0;
        double C2 = 2.0 / 5.0;
        return C1 * Math.exp(2 * x) + C2 * Math.exp(-3 * x);
    }
}
