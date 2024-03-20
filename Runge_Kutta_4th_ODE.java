import java.util.ArrayList;
import java.util.List;

public class Runge_Kutta_4th_ODE {
    
    HandleDerivativesOlaf handlederivatives;
    GUIOG gui;
    private double startTime = 0;
    private double endTime = 0;
    private double stepSize = 0;
    private List<Double> initialValues = new ArrayList<>();
    private List<String> rawDerivatives;

    Runge_Kutta_4th_ODE (double startTime, double endTime, double stepSize, List<Double> initialValues, List<String> rawDerivatives) {
        this.initialValues = initialValues;
        this.endTime = endTime;
        this.startTime = startTime;
        this.stepSize = stepSize;
        this.rawDerivatives = rawDerivatives;
    }

    public double[][] rungeSolver() {
        // number of steps to take
        double time = startTime+stepSize;
        int dimentions = initialValues.size();
        int steps = (int) (endTime / stepSize);

        // contains every numbers for all derivatives in each step
        double[][] evolution = new double[steps + 1][dimentions];

        // holds all values of the variables in curent time
        double[] currentstate = new double[initialValues.size()];
        for (int i = 0; i < initialValues.size(); i++) {
            currentstate[i] = initialValues.get(i);
        }

        double[] temporaryState = new double[initialValues.size()];

        for (int i = 0; i <= steps; i++) { // iterate over each step
            evolution[i] = currentstate.clone();

            HandleDerivativesOlaf k1 = new HandleDerivativesOlaf(rawDerivatives, time);
            double[] k1Values = k1.calculate(currentstate);

            for (int j = 0; j < initialValues.size(); j++) {
                temporaryState[j] = currentstate[j] + (k1Values[j] * (stepSize/2));
            }

            HandleDerivativesOlaf k2 = new HandleDerivativesOlaf(rawDerivatives, time + (stepSize/2));
            double[] k2Values = k2.calculate(temporaryState);

            for (int j = 0; j < initialValues.size(); j++) {
                temporaryState[j] = currentstate[j] + (k2Values[j] * (stepSize/2));
            }

            HandleDerivativesOlaf k3 = new HandleDerivativesOlaf(rawDerivatives, time + (stepSize/2));
            double[] k3Values = k3.calculate(temporaryState);

            for (int j = 0; j < initialValues.size(); j++) {
                temporaryState[j] = currentstate[j] + (k3Values[j] * (stepSize/2));
            }

            HandleDerivativesOlaf k4 = new HandleDerivativesOlaf(rawDerivatives, time + stepSize);
            double[] k4Values = k4.calculate(temporaryState);

            double[] averageSlopes = new double[dimentions];
            for (int l = 0; l < dimentions; l++) { // iterate over each dimension
                averageSlopes[l] = (1.0/6.0) * (k1Values[l] + 2 * k2Values[l] + 2 * k3Values[l] + k4Values[l]);// update average Slopes
            }

            // for (int k = 0; k < dimentions; k++) { // iterate over each dimension
            //     temporaryState[k] = currentstate[k] + stepSize * averageSlopes[k];// update temporary state
            // }

            for (int k = 0; k < dimentions; k++) { // iterate over each dimension
                //currentstate[k] += temporaryState[k];;// update variables
                currentstate[k] += stepSize * averageSlopes[k];
            }
            time += stepSize; // keep track of time if needed
        }

        return evolution;

    }

    public static void main(String[] args) {
        
    //     List<Double> iv = new ArrayList<>();
    //     iv.add(1.0);
    //     iv.add(3.0);
    //     iv.add(2.0);

    //     List<String> rd = new ArrayList<>();
    //     rd.add("a + 3 * b");
    //     rd.add("a ^ 2");
    //     rd.add("c - 1");

    //     Runge_Kutta_4th_ODE test = new Runge_Kutta_4th_ODE(0, 1, 0.1, iv, rd);

    //     double[][] testResults = test.rungeSolver();


    //     for(int i=0;i<testResults[0].length;i++){
    //         for(int j=0;j<testResults.length;j++){
    //             System.out.print(testResults[j][i]+" ");
    //         }
    //         System.out.println();
    //     }
    


    }
}
