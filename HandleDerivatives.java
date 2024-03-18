import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import javafx.scene.control.Alert;

// regarding reverse polish notation as input beofre and after operator use spaces if u want to have negative number use "-6" with only space infront
// use sqrt as normal operator with spaces after and infront the same with ( and )
public class HandleDerivatives {
    private List<String> derivativesString;


    HandleDerivatives(List<String> derivatives){
        this.derivativesString=derivatives;

    }

    //recives a state so array contaning values for all dimentions(derivatives) for every variable
    public double[] calculate(double[] state) {
        double[] derivativesArray = new double[state.length];
        for (int i = 0; i < state.length; i++) {
            String derString = derivativesString.get(i);
            String replacedDerString = replaceVariables(derString, state);
//  System.out.println("replaced string : "+replacedDerString);
            List<String> rpn = str_to_RPN(replacedDerString);
 System.out.println("rpn : "+rpn);
            derivativesArray[i] = rpn_to_double(rpn);
System.out.println(derivativesArray[i]);
        }
 //System.out.println();
        return derivativesArray;
    }

    // replace variables in derivative expression with values assigned to them in map
    public String replaceVariables(String derivative, double[] state) {
        String[] vari = {"a","b","c","d","e","f","g","h","i","j"};
        for(int i=0;i<state.length;i++){
            derivative = derivative.replaceAll(vari[i], String.valueOf(state[i]));
        }
        return derivative;
    }

    public void errorHandler(String derivative, int dimensions) {
        if (derivative.contains("+") && (!derivative.contains("+ ") || !derivative.contains(" +"))) {
            showAlert("Please make sure to use spaces before and after the '+' operator in the derivative expression.");
        }
        if (derivative.contains("^") && (!derivative.contains("^ ") || !derivative.contains(" ^"))) {
            showAlert("Please make sure to use spaces before and after the '^' operator in the derivative expression.");
        }
        if (derivative.contains("*") && (!derivative.contains("* ") || !derivative.contains(" *"))) {
            showAlert("Please make sure to use spaces before and after the '*' operator in the derivative expression.");
        }
        if (derivative.contains("/") && (!derivative.contains("/ ") || !derivative.contains(" /"))) {
            showAlert("Please make sure to use spaces before and after the '/' operator in the derivative expression.");
        }
        if (derivative.contains("(") && (!derivative.contains("( ") )) {
            showAlert("Please make sure to use spaces after the '(' symbol in the derivative expression.");
        }
        if (derivative.contains(")") && (!derivative.contains(" )") )) {
            showAlert("Please make sure to use spaces before the ')' symbol in the derivative expression.");
        }
        if (derivative.contains("sin") && (!derivative.contains("sin (") )) {
            showAlert("Please make sure to use spaces after the 'sin' symbol in the derivative expression.");
        }
        if (derivative.contains("cos") && (!derivative.contains("cos (") )) {
            showAlert("Please make sure to use spaces after the ')' symbol in the derivative expression.");
        }
        if (derivative.contains("sqrt") && (!derivative.contains("sqrt (") )) {
            showAlert("Please make sure to use spaces after the 'sqrt' symbol in the derivative expression.");
        }
        if (derivative.contains("log") && (!derivative.contains("log (") )) {
            showAlert("Please make sure to use spaces after the 'log' symbol in the derivative expression.");
        }
        if (derivative.contains("ln") && (!derivative.contains("ln (") )) {
            showAlert("Please make sure to use spaces after the 'ln' symbol in the derivative expression.");
        }
        if (derivative.contains("()")||derivative.contains("( )")){
            showAlert("Please make sure that there are values between the brackets.");
        }

        // Check if every opening '(' has a corresponding closing ')'
        Stack<Character> stack = new Stack<>();
        for (char c : derivative.toCharArray()) {
            if (c == '(') {
                stack.push(c);
            } else if (c == ')') {
                if (stack.isEmpty() || stack.pop() != '(') {
                    showAlert("Please ensure that every opening '(' has a corresponding closing ')' symbol in the derivative expression.");
                    return;
                }
            }
        }
        if (!stack.isEmpty()) {
            showAlert("Please ensure that every opening '(' has a corresponding closing ')' symbol in the derivative expression.");
            return;
        }
        
        // Check if alphabetic characters from (dimensions + 1) to z are used outside of sin, cos, sqrt, log, ln
        if (derivative.matches(".*\\b(sin|cos|sqrt|log|ln)\\b.*")) {
            // Exclude valid functions and then check for remaining alphabetic characters
            String remaining = derivative.replaceAll("(sin|cos|sqrt|log|ln)", "");
            if (dimensions == 2 && remaining.matches(".*[c-zC-Z].*")) {
                showAlert("Please ensure proper use of alphabetic characters (c-z) in the derivative expression.");
            } else if (dimensions != 2 && remaining.matches(".*[" + (char)(dimensions + 'a' - 1) + "-zK-Z].*")) {
                showAlert("Please ensure proper use of alphabetic characters (" + (char)(dimensions + 'a' ) + "-z) in the derivative expression.");
            }
        } else if (dimensions == 2 && derivative.matches(".*[c-zC-Z].*")) {
            showAlert("Please ensure proper use of alphabetic characters (c-z) in the derivative expression.");
        } else if (dimensions != 2 && derivative.matches(".*[" + (char)(dimensions + 'a' - 1) + "-zK-Z].*")) {
            showAlert("Please ensure proper use of alphabetic characters (" + (char)(dimensions + 'a' ) + "-z) in the derivative expression.");
        }
    }

    //polish reversed notation
    public List<String> str_to_RPN(String derivative) {
        derivative.trim();
        errorHandler(derivative, derivativesString.size());
        String[] tokens = derivative.split("\\s+");// create tokens
        List<String> rpn = new LinkedList<>();//contain rpn notation
        Stack<String> operators = new Stack<>();//contaion operators temporarly

        for(String token : tokens){ // iterate over all tokens
            token.trim();

            if(isDouble(token)){ //if token is a double add to list
                rpn.add(token);
            }

            else if (token.equals("(")) { // if token is ( add to stack
                operators.push(token);
            }
            else if(token.equals(")")){// if token is )
                while (!operators.isEmpty() && !operators.peek().equals("(")) { //   add all operators to rpn untill token ( is found or empty stack
                    // if operator is ( we get remove it from operators stack and brake while loop cuz we wmptied ()

                    rpn.add(operators.pop());; // add operator to rpn
                }
                if (!operators.isEmpty()) { // if operator is ( we remove it from operators stack cuz we emptied operators in ()
                    operators.pop(); 
                }

            }else{ // if token is a operator so + - / * ^ or sqrt 

                while (!operators.isEmpty() && priority(operators.peek()) >= priority(token)) {  // if operators stack is not empty and if a token is higher or equal prority as top operator on stack we remove the top operator and add it to rpn list
                    rpn.add(operators.pop());
                }
                operators.push(token); // add tokes to operators stack if top operator is of lower prority then token cuz we didnt went threw if statement
            }

        }
        while(!operators.isEmpty()){
            rpn.add(operators.pop()); // add every operator that is left to rpn list 
        }
        return rpn;
    }


    private int priority(String operator) {
        switch (operator) {
            case "sqrt":
            case "log":
            case "ln":
            case "sin":
            case "cos":
                return 4;
            case "^":
                return 3;
            case "*":
            case "/":
                return 2;
            case "+":
            case "-":
                return 1;
            default:
                return -1;
        }
    }

    private static boolean isDouble(String text) {
        try {
            Double.parseDouble(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    //rpn solver
    public  double rpn_to_double(List<String> rpn){
        Stack<Double> stack = new Stack<>();

        for(String token : rpn){
   // debug    // System.out.println(rpn); // debug
            switch (token) {
                case "sin":
                   if (stack.isEmpty()) throw new IllegalArgumentException("Insufficient operands for sin");
                    stack.push(Math.sin(Math.toRadians(stack.pop()))); //  the angle is in degrees and converts to radians
                    break;
                case "cos":
                    if (stack.isEmpty()) throw new IllegalArgumentException("Insufficient operands for cos");
                    stack.push(Math.cos(Math.toRadians(stack.pop()))); //  the angle is in degrees and converts to radians
                    break;
                case "ln":
                    if (stack.isEmpty()) throw new IllegalArgumentException("Insufficient operands for log");
                      double value = stack.pop();
                      if(value <= 0) throw new IllegalArgumentException("Logarithm of non-positive number");
                      stack.push(Math.log(value)); // Natural logarithm base 
                      break;
                case "log":
                       if (stack.isEmpty()) throw new IllegalArgumentException("Insufficient operands for log");
                      double v = stack.pop();
                      if(v <= 0) throw new IllegalArgumentException("Logarithm of non-positive number");
                      stack.push(Math.log10(v)); //  logarithm base 10
                    break;
                case "sqrt":
                    if (rpn.size() < 2) throw new IllegalArgumentException("Insufficient operands for sqrt");
                    stack.push(Math.sqrt(stack.pop()));
                    break;
                case "^":
                    if (rpn.size() < 2) throw new IllegalArgumentException("Insufficient operands for ^");
                    double expo = stack.pop();
                    stack.push(Math.pow(stack.pop(),expo));
                    break;
                case "*":
                    if (rpn.size() < 2) throw new IllegalArgumentException("Insufficient operands for *");
                    stack.push(stack.pop()*stack.pop());
                    break;
                case "/":
                    if (rpn.size() < 2) throw new IllegalArgumentException("Insufficient operands for /");
                    double devider = stack.pop();
                    stack.push((double)stack.pop()/(double)devider);
                    break;
                case "+":
                    if (rpn.size() < 2) throw new IllegalArgumentException("Insufficient operands for +");
                    stack.push(stack.pop()+stack.pop());
                    break;
                case "-":
                    if (rpn.size() < 2) throw new IllegalArgumentException("Insufficient operands for -");
                    double a = stack.pop();
                    double b = stack.pop();
                    stack.push(b - a);
                    break;
                default:
                    stack.push(Double.parseDouble(token)); // from str to double
                    break;
            }
        }
        if (stack.size() != 1) {
            throw new IllegalArgumentException("Invalid RPN expression");
        }
        return stack.pop();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
    // TEST
    // public static void main (String[] args){
    //     String test = "-5 * x * ( sqrt ( 24 * y + 1 * z ) )";
    //     System.err.println(test);

    //     double[] state = {3,1,1};
    //     test = test = replaceVariables(test, state);
    //     System.out.println(test);

    //     List<String> a= str_to_RPN(test);
    //     System.out.println(a);

    //     System.out.println(rpn_to_double(a));
    // }
