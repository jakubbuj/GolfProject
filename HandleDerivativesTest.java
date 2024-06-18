import java.util.Arrays;
import java.util.List;

public class HandleDerivativesTest {
    public static void main(String[] args) {
        List<String> derivatives = Arrays.asList("b", "a 6 * b -"); // dy1/dx = y2, dy2/dx = 6*y1 - y2 (RPN)
        HandleDerivatives handleDerivatives = new HandleDerivatives(derivatives, 0.0);

        double[] state = {1.0, 0.0}; // y(0) = 1, y'(0) = 0
        double[] result = handleDerivatives.calculate(state);

        System.out.println("Result: " + Arrays.toString(result)); // Expected Result: [0.0, 6.0]
    }
}
