import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Color;

import java.awt.*;
import java.util.Arrays;

public class GuiController {
    //Tab Create Vehicle
    @FXML
    private ComboBox<String> comboBoxEdges;
    @FXML
    private ComboBox<String> comboBoxColors;
    @FXML
    private ComboBox<String> comboBoxRoutes;
    @FXML
    private TextField textFieldStartSpeed;
    @FXML
    private Button buttonAddToSim;


    //Tab Edit Vehicle
    @FXML
    private ComboBox<String> comboBoxSelectVehicle;
    @FXML
    private ComboBox<String> comboBoxSetColor;
    @FXML
    private ComboBox<String> comboBoxChangeRoute;
    @FXML
    private TextField textFieldChangeSpeed;
    @FXML
    private Button buttonChangeColor;
    @FXML
    private Button buttonChangeRoute;
    @FXML
    private Button buttonChangeSpeed;
    @FXML
    private Label labelVehicleColor;
    @FXML
    private Label labelVehicleSpeed;
    @FXML
    private Label labelVehicleRoute;

    //Tab Traffic Lights
    @FXML
    private ComboBox<String> comboBoxSelectLight;
    @FXML
    private Label labelCurrentLightPhase;
    @FXML
    private Label labelNextLightPhase;
    @FXML
    private Label labelDurationRed;
    @FXML
    private Button buttonChangePhase;
    @FXML
    private Button buttonLightDuration;
    @FXML
    private TextField textFieldLightDuration;
    @FXML
    private Button buttonStartStopSimulation;

    @FXML
    private Canvas canvasMap;

    private double roadW = 20;
    private double gap = 40;
    private double boxHalf = 35;

    //Simulation
    private Simulation sim;

    public void setSimulation(Simulation sim){
        this.sim = sim;
        comboBoxFill();
    }

    public void comboBoxFill(){
        if (sim != null) {
            comboBoxEdges.setItems(FXCollections.observableArrayList());
            comboBoxRoutes.setItems(FXCollections.observableArrayList(sim.getRouteIDs()));
            comboBoxSelectVehicle.setItems(FXCollections.observableArrayList(sim.getCarIDs()));
            comboBoxSelectLight.setItems(FXCollections.observableArrayList(sim.getTrafficLightIDs()));
        }
    }

    public void newCar(){
        sim.createNewCar("0", textFieldStartSpeed.getText(), comboBoxColors.getValue(), comboBoxRoutes.getValue());
    }

    public void currentCar(){
        String curCar = comboBoxSelectVehicle.getValue();
        labelVehicleColor.setText(sim.getCarsColorFromID(curCar));
        labelVehicleSpeed.setText(sim.getCarsSpeedFromID(curCar));
        labelVehicleRoute.setText(sim.getCarsRouteFromID(curCar));
    }

    public void readLight(){
        String curTrafficLight = comboBoxSelectLight.getValue();
        labelCurrentLightPhase.setText(sim.getTrafficLightColorFromID(curTrafficLight));
        labelDurationRed.setText(sim.getTrafficLightCycleLengthFromID(curTrafficLight));
    }

    public void setLightDurationBtn(){
        String curTrafficLight = comboBoxSelectLight.getValue();
        Float dur = Float.valueOf(textFieldLightDuration.getText());
        sim.setTrafficLightCycleLengthFromID(curTrafficLight, String.valueOf(dur));
        labelDurationRed.setText(sim.getTrafficLightCycleLengthFromID(curTrafficLight));
    }

    public void togglePause(){
        sim.togglePause();
    }

    public void printMap(){
        GraphicsContext gc = canvasMap.getGraphicsContext2D();
        //Build Map here
    }

    private void draw() {
        GraphicsContext gc = canvasMap.getGraphicsContext2D();
        double w = canvasMap.getWidth();
        double h = canvasMap.getHeight();

        double cx = w / 2;
        double cy = h / 2;

        drawMapFX(gc, w, h, cx, cy);
    }

    private void drawMapFX(GraphicsContext gc, double w, double h,
                           double cx, double cy) {

        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, w, h);

        Color roadFill = Color.rgb(235, 235, 235);
        Color roadEdge = Color.rgb(150, 150, 150);

        gc.setStroke(roadFill);
        gc.setLineWidth(roadW);

        gc.strokeLine(20, cy - gap, w - 20, cy - gap);
        gc.strokeLine(20, cy + gap, w - 20, cy + gap);
        gc.strokeLine(cx - gap, 20, cx - gap, h - 20);
        gc.strokeLine(cx + gap, 20, cx + gap, h - 20);

        gc.setStroke(roadEdge);
        gc.setLineWidth(2);

        gc.strokeLine(20, cy - gap, w - 20, cy - gap);
        gc.strokeLine(20, cy + gap, w - 20, cy + gap);
        gc.strokeLine(cx - gap, 20, cx - gap, h - 20);
        gc.strokeLine(cx + gap, 20, cx + gap, h - 20);

        gc.setFill(Color.rgb(210, 210, 210, 0.67));
        gc.fillRect(cx - boxHalf, cy - boxHalf, boxHalf * 2, boxHalf * 2);

        gc.setStroke(Color.rgb(120, 120, 120));
        gc.setLineWidth(2);
        gc.strokeRect(cx - boxHalf, cy - boxHalf, boxHalf * 2, boxHalf * 2);
    }

    @FXML
    public void initialize(){
        comboBoxColors.setItems(FXCollections.observableArrayList("black", "white"));
        draw();
    }
}
