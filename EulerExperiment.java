import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EulerExperiment {
    public static void main(String[] args) {
        double startTime = 0.0;
        double endTime = 10.0;
        List<Double> initialValues = Arrays.asList(1.0, 0.0); // y(0) = 1, y'(0) = 0
        List<String> rawDerivatives = Arrays.asList("b", "a 6 * b -"); // dy1/dx = y2, dy2/dx = 6*y1 - y2 (RPN)

        double[] stepSizes = {0.1, 0.01, 0.001, 0.0001, 0.00001, 0.000001}; // Mides de pas per experimentar

        for (double stepSize : stepSizes) {
            long startTimeNano = System.nanoTime(); // Inici del temporitzador

            Euler_Method_for_1st_ODE solver = new Euler_Method_for_1st_ODE(startTime, endTime, stepSize, initialValues, rawDerivatives);
            double[][] evolution = solver.solver();

            long endTimeNano = System.nanoTime(); // Fi del temporitzador
            long computationTime = endTimeNano - startTimeNano; // Càlcul del temps de còmput en nanosegons
            double computationTimeSeconds = computationTime / 1e9; // Convertir a segons

            double error = calculateRMSE(evolution, endTime, stepSize);

            System.out.println("Step size: " + stepSize + ", Computation time (s): " + computationTimeSeconds + ", Error: " + error);
        }
    }

    public static double[][] runSimulation(double startTime, double endTime, double stepSize, List<Double> initialValues, List<String> rawDerivatives) {
        Euler_Method_for_1st_ODE solver = new Euler_Method_for_1st_ODE(startTime, endTime, stepSize, initialValues, rawDerivatives);
        return solver.solver();
    }

    public static double calculateRMSE(double[][] evolution, double endTime, double stepSize) {
        int steps = (int) (endTime / stepSize);
        double sumSquaredErrors = 0.0;
        for (int i = 0; i <= steps; i++) {
            double time = i * stepSize;
            double analyticalValue = analyticalSolution(time);
            double numericalValue = evolution[i][0];
            double error = numericalValue - analyticalValue;
            sumSquaredErrors += (error * error);
        }
        return Math.sqrt(sumSquaredErrors / (steps + 1));
    }

    public static double analyticalSolution(double x) {
        double C1 = 3.0 / 5.0;
        double C2 = 2.0 / 5.0;
        return C1 * Math.exp(2 * x) + C2 * Math.exp(-3 * x);
    }
}
