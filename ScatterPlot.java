import java.util.Arrays;

import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ScatterPlot {
    
    private double[][] array;
    private double startTime = 0.0;
    private double endTime = 0.0;
    private double stepSize = 0.0;

    private boolean[] seriesVisibility;

    public ScatterPlot(double[][] array, double startTime, double endTime, double stepSize){

        this.array = array;
        this.startTime = startTime;
        this.endTime = endTime;
        this.stepSize = stepSize;

        this.seriesVisibility = new boolean[array[0].length];
        Arrays.fill(seriesVisibility, true); //making all the series visible in the beginning

    }

    public void plot() {
        //Creating axes
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Time");
        yAxis.setLabel("Variable Values");

        //Creating scatter chart
        ScatterChart<Number, Number> scatterChart = new ScatterChart<>(xAxis, yAxis);
        scatterChart.setTitle("Evolution Plot");

        XYChart.Series<Number, Number>[] seriesArray = new XYChart.Series[array[0].length];

        //Adding data to scatter chart
        for (int i = 0; i < array[0].length; i++) {

            XYChart.Series<Number, Number> series = new XYChart.Series<>();
    
            // naming the series
            switch (i) {
                case 0:
                    series.setName("a");
                    break;
                case 1:
                    series.setName("b");
                    break;
                case 2:
                    series.setName("c");
                    break;
                case 3:
                    series.setName("d");
                    break;
                case 4:
                    series.setName("e");
                    break;
                case 5:
                    series.setName("f");
                    break;
                case 6:
                    series.setName("g");
                    break;
                case 7:
                    series.setName("h");
                    break;
                case 8:
                    series.setName("i");
                    break;
                case 9:
                    series.setName("j");
                    break;
                // default:
                //     series.setName("x");
                //     break;
            }
            

            for (int j = 0; j < array.length ; j++) {

                series.getData().add(new XYChart.Data<>(j * stepSize, array[j][i]));

            }

            scatterChart.getData().add(series);
            seriesArray[i] = series;
        }

        // Creating a button for each variable
        Button[] buttons = new Button[array[0].length];
        for (int i = 0; i < array[0].length; i++) {

            final int seriesIndex = i;
            //buttons[i] = new Button("Variable" + i);

            // Creating a button and naming it
            switch (i) {

                case 0:
                    buttons[i] = new Button("a");
                    break;
                case 1:
                    buttons[i] = new Button("b");
                    break;
                case 2:
                    buttons[i] = new Button("c");
                    break;
                case 3:
                    buttons[i] = new Button("d");
                    break;
                case 4:
                    buttons[i] = new Button("e");
                    break;
                case 5:
                    buttons[i] = new Button("f");
                    break;
                case 6:
                    buttons[i] = new Button("g");
                    break;
                case 7:
                    buttons[i] = new Button("h");
                    break;
                case 8:
                    buttons[i] = new Button("i");
                    break;
                case 9:
                    buttons[i] = new Button("j");
                    break;
                
            }

            buttons[i].setOnAction(event -> {
                updateGraph(scatterChart, seriesIndex, seriesArray);
                updateButtonColor(buttons[seriesIndex], seriesVisibility[seriesIndex]);
            });

            updateButtonColor(buttons[i], seriesVisibility[i]); //setting button color for the first time

        }

        Stage stage = new Stage();
        stage.setTitle("Scatter Plot");

        VBox vbox = new VBox();
        HBox buttonBox = new HBox();
        buttonBox.getChildren().addAll(buttons);
        vbox.getChildren().addAll(buttonBox, scatterChart);

        Scene scene = new Scene(vbox, 800, 600);
        stage.setScene(scene);
        stage.show();

    }

    private void updateGraph(ScatterChart<Number, Number> scatterChart , int seriesIndex, XYChart.Series<Number, Number>[] seriesArray) {

        if (seriesVisibility[seriesIndex]) {
            scatterChart.getData().remove(seriesArray[seriesIndex]);
        } else {
            scatterChart.getData().add(seriesArray[seriesIndex]);
        }
        seriesVisibility[seriesIndex] = !seriesVisibility[seriesIndex];
    
    }

    private void updateButtonColor(Button button, boolean seriecIsVisible){

        if (seriecIsVisible){
            button.setStyle("-fx-background-color: green;");
        } else {
            button.setStyle("-fx-background-color: red;");
        }
    }

}


