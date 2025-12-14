import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

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
        sim.setTrafficLightCycleLengthFromID(curTrafficLight, dur);
        labelDurationRed.setText(sim.getTrafficLightCycleLengthFromID(curTrafficLight));
    }

    public void togglePause(){
        sim.togglePause();
    }

    @FXML
    public void initialize(){
        comboBoxColors.setItems(FXCollections.observableArrayList("black", "white"));

    }
}