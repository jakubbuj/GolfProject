import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class GetHeight {

    /**
     * Calculates the height for a given mathematical expression, x, and y coordinates.
     *
     * @param heightFunction The mathematical expression representing the height.
     * @param x             The x coordinate.
     * @param y             The y coordinate.
     * @return The height calculated for the given coordinates and expression.
     */
    public static double getHeight(String heightFunction, double x, double y) {
        String replacedHeightFunction = replaceVariables(heightFunction, x, y);
        List<String> rpn = strToRPN(replacedHeightFunction);
        return rpnToDouble(rpn);
    }

    /**
     * Replaces variables (x and y) and constants (pi, e, g) in the height function with their corresponding values.
     *
     * @param heightFunction The mathematical expression representing the height.
     * @param x              The x coordinate.
     * @param y              The y coordinate.
     * @return The height function with variables and constants replaced by their values.
     */
    public static String replaceVariables(String heightFunction, double x, double y) {
        heightFunction = heightFunction.replaceAll("x", String.valueOf(x));
        heightFunction = heightFunction.replaceAll("y", String.valueOf(y));
        heightFunction = heightFunction.replaceAll("pi", String.valueOf(Math.PI));
        heightFunction = heightFunction.replaceAll("e", String.valueOf(Math.E));
        heightFunction = heightFunction.replaceAll("g", String.valueOf(9.81));
        return heightFunction;
    }

    /**
     * Converts a mathematical expression to Reverse Polish Notation (RPN).
     *
     * @param heightFunction The mathematical expression.
     * @return The expression in RPN.
     */
    public static List<String> strToRPN(String heightFunction) {
        heightFunction = heightFunction.trim();
        String[] tokens = heightFunction.split("\\s+");
        List<String> rpn = new LinkedList<>();
        Stack<String> operators = new Stack<>();

        for (String token : tokens) {
            token = token.trim();

            if (isDouble(token)) {
                rpn.add(token);
            } else if (token.equals("(")) {
                operators.push(token);
            } else if (token.equals(")")) {
                while (!operators.isEmpty() && !operators.peek().equals("(")) {
                    rpn.add(operators.pop());
                }
                if (!operators.isEmpty()) {
                    operators.pop();
                }
            } else {
                while (!operators.isEmpty() && priority(operators.peek()) >= priority(token)) {
                    rpn.add(operators.pop());
                }
                operators.push(token);
            }
        }
        while (!operators.isEmpty()) {
            rpn.add(operators.pop());
        }
        return rpn;
    }

    /**
     * Determines the priority of an operator.
     *
     * @param operator The operator.
     * @return The priority value.
     */
    private static int priority(String operator) {
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

    /**
     * Checks if a string represents a double value.
     *
     * @param text The string to check.
     * @return True if the string represents a double value, false otherwise.
     */
    private static boolean isDouble(String text) {
        try {
            Double.parseDouble(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Evaluates a mathematical expression in RPN format.
     *
     * @param rpn The expression in RPN format.
     * @return The result of the evaluation.
     */
    public static double rpnToDouble(List<String> rpn) {
        Stack<Double> stack = new Stack<>();

        for (String token : rpn) {
            switch (token) {
                case "sin":
                    if (stack.isEmpty()) throw new IllegalArgumentException("Insufficient operands for sin");
                    stack.push(Math.sin(stack.pop()));
                    break;
                case "cos":
                    if (stack.isEmpty()) throw new IllegalArgumentException("Insufficient operands for cos");
                    stack.push(Math.cos(stack.pop()));
                    break;
                case "ln":
                    if (stack.isEmpty()) throw new IllegalArgumentException("Insufficient operands for log");
                    double value = stack.pop();
                    if (value <= 0) throw new IllegalArgumentException("Logarithm of non-positive number");
                    stack.push(Math.log(value));
                    break;
                case "log":
                    if (stack.isEmpty()) throw new IllegalArgumentException("Insufficient operands for log");
                    double v = stack.pop();
                    if (v <= 0) throw new IllegalArgumentException("Logarithm of non-positive number");
                    stack.push(Math.log10(v));
                    break;
                case "sqrt":
                    if (rpn.size() < 2) throw new IllegalArgumentException("Insufficient operands for sqrt");
                    stack.push(Math.sqrt(stack.pop()));
                    break;
                case "^":
                    if (rpn.size() < 2) throw new IllegalArgumentException("Insufficient operands for ^");
                    double expo = stack.pop();
                    stack.push(Math.pow(stack.pop(), expo));
                    break;
                case "*":
                    if (rpn.size() < 2) throw new IllegalArgumentException("Insufficient operands for *");
                    stack.push(stack.pop() * stack.pop());
                    break;
                case "/":
                    if (rpn.size() < 2) throw new IllegalArgumentException("Insufficient operands for /");
                    double devider = stack.pop();
                    stack.push((double) stack.pop() / (double) devider);
                    break;
                case "+":
                    if (rpn.size() < 2) throw new IllegalArgumentException("Insufficient operands for +");
                    stack.push(stack.pop() + stack.pop());
                    break;
                case "-":
                    if (rpn.size() < 2) throw new IllegalArgumentException("Insufficient operands for -");
                    double a = stack.pop();
                    double b = stack.pop();
                    stack.push(b - a);
                    break;
                default:
                    stack.push(Double.parseDouble(token));
                    break;
            }
        }
        if (stack.size() != 1) {
            throw new IllegalArgumentException("Invalid RPN expression");
        }
        return stack.pop();
    }

    public static void main(String[] args) {
        double x = 0.49;
        double y = 1.5666;
        String function = "sin ( e ^ y ) - sqrt ( x + y - 2 )";

        System.out.println(getHeight(function, x, y));
    }
}
