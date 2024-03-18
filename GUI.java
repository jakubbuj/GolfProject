import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
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
    private double[][] evolution;
    private int dimensions = 2; 

    private List<TextField> derivativesFields = new ArrayList<>();
    private List<TextField> initialValuesFields = new ArrayList<>();
    public List<String> derivatives = new ArrayList<>();
    public List<Double> initialValues = new ArrayList<>();

    private String style = "-fx-font-size: 18px;";

//Starting window where user defines number of dimensions
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Main Window");

        Label dimensionLabel = new Label("Specify the number of dimensions (1-10):");
        dimensionLabel.setStyle(style); 

        TextField dimensionField = new TextField();
        dimensionField.setPromptText("Specify the number of dimensions");
        dimensionField.setStyle(style); 

        Button dimensionButton = new Button("Select");
        dimensionButton.setStyle(style);
        dimensionButton.setMaxWidth(Double.MAX_VALUE); 

        dimensionButton.setOnAction(e -> {
            String input = dimensionField.getText();
            try {
                int inputNumber = Integer.parseInt(input);
                if (inputNumber >= 1) {
                    if (inputNumber >= 10){
                        dimensions = 10;
                    } else {
                        dimensions = inputNumber;
                    }
                    displayOptions();
                    primaryStage.close();
                } 
            } catch (NumberFormatException ex) {
                showAlert("Please enter a valid integer for the dimensions.");
            }
        });

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(50));
        layout.getChildren().addAll(dimensionLabel, dimensionField, dimensionButton);

        primaryStage.setScene(new Scene(layout, 600, 400));
        primaryStage.show();
    }

    //Second window where user selects solver
    private void displayOptions() {

        Stage optionsStage = new Stage();
        optionsStage.initModality(Modality.APPLICATION_MODAL);
        optionsStage.setTitle("Options");
    
        Button option1Button = new Button("Euler solver (first order ODEs)");
        option1Button.setStyle("-fx-font-size: 16px; -fx-pref-height: 40px;"); 
        option1Button.setOnAction(e -> {
            displayEulerSolver("Option 1 selected");
            optionsStage.close(); // Close the options window
        });
        option1Button.setMaxWidth(Double.MAX_VALUE); 
    
        Button option2Button = new Button("Another solver (second order ODEs)");
        option2Button.setStyle("-fx-font-size: 16px; -fx-pref-height: 40px;"); 
        option2Button.setMaxWidth(Double.MAX_VALUE); 
        //option1Button.setOnAction(e -> {
        //displayEulerSolver("Option 2 selected");
        //optionsStage.close(); // Close the options window
        //});
    
        Label titleLabel = new Label("Choose solver");
        titleLabel.setStyle("-fx-font-size: 18px;");
    
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(titleLabel, option1Button, option2Button);
    
        optionsStage.setScene(new Scene(layout, 400, 250)); // Adjust window size
        optionsStage.show();

    }

    //Third window where user selects parameters for the Euler solver
    private void displayEulerSolver(String message) {

        VBox root = new VBox(5);
        root.setPadding(new Insets(10));

        Label startTimeLabel = new Label("Start time:");
        startTimeField.setPromptText("Enter start time");
        startTimeLabel.setStyle(style);
        startTimeField.setStyle(style);

        Label endTimeLabel = new Label("End time:");
        endTimeField.setPromptText("Enter end time");
        endTimeLabel.setStyle(style);
        endTimeField.setStyle(style);

        Label stepSizeLabel = new Label("Size of the steps:");
        stepSizeField.setPromptText("Enter size of the steps");
        stepSizeLabel.setStyle(style);
        stepSizeField.setStyle(style);

        Label derivativesLabel = new Label("Derivatives and initial values:");
        derivativesLabel.setStyle(style);

        root.getChildren().addAll(startTimeLabel, startTimeField, endTimeLabel, endTimeField, stepSizeLabel, stepSizeField, derivativesLabel);

    //create derivatives fields
    for (int i = 0; i < dimensions; i++) {
        TextField derivativeField = new TextField();
        derivativeField.setPromptText("Enter a derivative for dimension " + (i + 1));
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
        submitButton.setStyle(style);
        root.getChildren().add(submitButton);

        Button generateButton = new Button("Generate");
        generateButton.setOnAction(event -> aa());
        generateButton.setStyle(style);
        root.getChildren().add(generateButton);

        Button showPlotButton = new Button("Show Evolution");
        showPlotButton.setOnAction(event -> handleShowPlotButton());
        showPlotButton.setStyle(style);
        root.getChildren().add(showPlotButton);

        // Wrap the VBox with a ScrollPane
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(root);
        scrollPane.setFitToWidth(true);

        Stage eulerStage = new Stage();
        eulerStage.setTitle("Eulers ODE Solver");
        eulerStage.setScene(new Scene(scrollPane, 600, 530));
        eulerStage.show();
    }


    //submit button and adding elements to string and double lists
    public void handleSubmitButton(){
        derivatives.clear();
        initialValues.clear();
        List<Integer> invalidIndices = new ArrayList<>();

        if(isDouble(startTimeField.getText())){
            startTime = Double.parseDouble(startTimeField.getText());
        }else{
            showAlert("Please enter a valid number (double) for the startTime.");
        }
        if(isDouble(endTimeField.getText())){
            endTime = Double.parseDouble(endTimeField.getText());
        }else{
            showAlert("Please enter a valid number (double) for the endTime.");
        }
        if (startTime > endTime){
            showAlert("Please make sure that the startTime is smaller then the endtime");
        }
        if(isDouble(stepSizeField.getText())){
            stepSize = Double.parseDouble(stepSizeField.getText());
        }else{
            showAlert("Please enter a valid number (double) for the stepSize.");
        }
        if (stepSize<startTime || stepSize>endTime){
            showAlert("Please make sure that the stepsize is between the starttime and endtime");
        }

        for (TextField field : derivativesFields) { // derivatives 
            derivatives.add(field.getText()); // Store the derivative expressions
        }
    
        int index = 1;
        for (TextField field : initialValuesFields) {  // initial values 
            if(isDouble(field.getText())){
                initialValues.add(Double.parseDouble(field.getText()));          
            } else {
                invalidIndices.add(index); // Add index of invalid value
            }
            index++;
        }
    
        if (!invalidIndices.isEmpty()) { // Check if there are any invalid values
            StringBuilder errorMessage = new StringBuilder("Invalid number(s) (double) for the initial values at index: ");
            for (int i = 0; i < invalidIndices.size(); i++) {
                errorMessage.append(invalidIndices.get(i));
                if (i < invalidIndices.size() - 1) {
                    errorMessage.append(", "); 
                }
            }
            showAlert(errorMessage.toString());
        }

    }

    public void aa(){
        
        System.out.println("derivative input : "+ derivatives);
        System.out.println("initioal values : "+ initialValues);

        Euler_Method_for_1st_ODE Euler = new Euler_Method_for_1st_ODE(startTime, endTime, stepSize, initialValues, derivatives);
        double[][] evolution = Euler.solver();

        for(int i=0;i<evolution[0].length;i++){
            for(int j=0;j<evolution.length;j++){
                System.out.print(evolution[j][i]+" ");
            }
            System.out.println();
        }

    }


    public void handleShowPlotButton() {
       
        // Generate scatter plot data
        Euler_Method_for_1st_ODE euler = new Euler_Method_for_1st_ODE(startTime, endTime, stepSize, initialValues, derivatives);
        double[][] evolution = euler.solver();
    
        // Instantiate ScatterPlotterFX with the generated data
        ScatterPlot scatterPlotter = new ScatterPlot(evolution, startTime, endTime, stepSize);
    
        // Display the scatter plot in the new window
        scatterPlotter.plot();

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
