import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;

public class GUI extends Application {
    private TextField dimensionsField = new TextField();
    private TextField startTimeField = new TextField();
    private TextField endTimeField = new TextField();
    private TextField stepSizeField = new TextField();
    private double startTime = 0.0;
    private double endTime = 0.0;
    private double stepSize = 0.0;

    private List<TextField> derivativesFields = new ArrayList<>();
    private List<TextField> initialValuesFields = new ArrayList<>();
    public List<String> derivatives = new ArrayList<>();
    public List<Double> initialValues = new ArrayList<>();

    private int dimensions = 2; // Default dimensions, you can change as needed

    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(5);

        startTimeField.setPromptText("Enter start time");
        endTimeField.setPromptText("Enter end time");
        stepSizeField.setPromptText("Enter step size");

        

        root.getChildren().addAll(startTimeField, endTimeField, stepSizeField);

        //create derivatives fields
        for (int i = 0; i < dimensions; i++) {
            TextField derivativeField = new TextField();
            derivativeField.setPromptText("Enter derivative for dimension " + (i + 1));
            derivativesFields.add(derivativeField); // add to list 
            root.getChildren().add(derivativeField);
        }

        //create fields for variables
        for (int i = 0; i < dimensions; i++) {
            TextField initialValueField = new TextField();
            initialValueField.setPromptText("Enter initial value for dimension " + (i + 1));
            initialValuesFields.add(initialValueField); // add to list 
            root.getChildren().add(initialValueField);
        }

        Button submitButton = new Button("Submit");
        submitButton.setOnAction(event -> handleSubmitButton());
        root.getChildren().add(submitButton);

        Button generate = new Button("gen");
        generate.setOnAction(event -> aa());
        root.getChildren().add(generate);

        Scene scene = new Scene(root, 500, 400);

        primaryStage.setTitle("ODE Input");
        primaryStage.setScene(scene);
        primaryStage.show();
        
    }

    //submit button and adding elements to string and double lists
    public void handleSubmitButton(){
        derivatives.clear();
        initialValues.clear();

        if(isDouble(startTimeField.getText())){
            startTime = Double.parseDouble(startTimeField.getText());
        }else{
            showAlert("Please enter valid numbers (double) for startTime .");
        }
        if(isDouble(endTimeField.getText())){
            endTime = Double.parseDouble(endTimeField.getText());
        }else{
            showAlert("Please enter valid numbers (double) for endTime .");
        }
        if(isDouble(stepSizeField.getText())){
            stepSize = Double.parseDouble(stepSizeField.getText());
        }else{
            showAlert("Please enter valid numbers (double) for stepSize .");
        }

        for (TextField field : derivativesFields) { // derivatives 
            derivatives.add(field.getText()); // Store the derivative expressions
        }
        int index=1;
        for (TextField field : initialValuesFields) {  //innitial values 
            if(isDouble(field.getText())){
                initialValues.add(Double.parseDouble(field.getText()));
                index++;
            }
            else{
                showAlert("Please enter valid number (double) for initial value.  "+index);
                index++;
            }
        }


    }

    public void aa(){
        
        System.out.println(" derivative input : "+ derivatives);
        System.out.println(" initioal values : "+initialValues);

        Euler_Method_for_1st_ODE Euler = new Euler_Method_for_1st_ODE(startTime, endTime, stepSize, initialValues, derivatives);

        double[][] evolution = Euler.solver();

        for(int i=0;i<evolution[0].length;i++){
            for(int j=0;j<evolution.length;j++){
                System.out.print(evolution[j][i]+" ");
            }
            System.out.println();
        }

    }

    private boolean isDouble(String text) {
        try {
            Double.parseDouble(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

        private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.showAndWait();
    }
    

    public static void run(String[] args){
        launch(args); // This will launch the JavaFX application
    }

}
