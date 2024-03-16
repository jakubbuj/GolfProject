import java.util.ArrayList;
import java.util.List;

public class Euler_Method_for_1st_ODE {

    HandleDerivatives handlederivatives;
    GUI gui;
    private double startTime = 0;
    private double endTime = 0;
    private double stepSize = 0;
    private List<Double> initialValues = new ArrayList<>();
    private List<String> rawDerivatives;


    Euler_Method_for_1st_ODE(double startTime,double endTime,double stepSize,List<Double> initialValues,List<String> rawDerivatives){
        this.initialValues = initialValues;
        this.endTime = endTime;
        this.startTime = startTime;
        this.stepSize = stepSize;
        this.rawDerivatives = rawDerivatives;
    }

    // returrn 2d array containg evolution of values threw calcultaion 
    public double[][] solver(){
        //number of steps to take
        double time = startTime;
        int dimentions = initialValues.size();
        int steps =(int) (endTime/stepSize);

        //contains every numbers for all derivatives in each step
        double[][] evolution = new double[steps][dimentions];

        //holds all values of the variables in curent time
        double[] currentstate = new double[initialValues.size()];
        for(int i=0;i<initialValues.size();i++){
            currentstate[i]=initialValues.get(i);
        }
       
        for(int i=0;i<steps;i++){ //iterate over each step
            evolution[i] = currentstate.clone();

           handlederivatives = new HandleDerivatives(rawDerivatives); // pass derivatives 
            double[] derivativeValues = handlederivatives.calculate(currentstate); //fill aray with derivatives solved for each dimension

            for(int k=0;k<dimentions;k++){ //iterate over each dimension
                currentstate[k] += stepSize* derivativeValues[k];// update variables 
            }
            time+=stepSize; // keep track of time if needed
        }
        
        return evolution;



    }

   
    
}
