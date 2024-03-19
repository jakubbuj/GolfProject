import java.util.List;


public class TrapezoidalODESolver {
    private double startTime;
    private double endTime;
    private double stepSize;
    private List<Double> initialValues;
    private List<String> rawDerivatives;

    TrapezoidalODESolver(double startTime, double endTime, double stepSize, List<Double> initialValues, List<String> rawDerivatives) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.stepSize = stepSize;
        this.initialValues = initialValues;
        this.rawDerivatives = rawDerivatives;
    }

    public double[][] solve() {
        int dimensions = initialValues.size();
        int steps = (int) ((endTime - startTime) / stepSize);
        double[][] evolution = new double[steps][dimensions];
        double[] currentState = new double[dimensions];

        for (int i = 0; i < dimensions; i++) {
            currentState[i] = initialValues.get(i);
        }

        DerivativeHandler derivativeHandler = new DerivativeHandler(); 

        // Solve using Trapezoidal method
        for (int i = 0; i < steps; i++) {
            evolution[i] = currentState.clone();
            double[] derivativeValues = derivativeHandler.calculate(rawDerivatives, currentState); // Calculates derivatives at current state

            // Predict next state using Euler's method
            double[] predictedState = new double[dimensions];
            for (int k = 0; k < dimensions; k++) {
                predictedState[k] = currentState[k] + stepSize * derivativeValues[k];
            }

            double[] predictedDerivativeValues = derivativeHandler.calculate(rawDerivatives, predictedState); // Calculates derivatives at predicted state

            // Update state using Trapezoidal method
            for (int k = 0; k < dimensions; k++) {
                currentState[k] += 0.5 * stepSize * (derivativeValues[k] + predictedDerivativeValues[k]);
            }
        }

        return evolution;
    }
}
