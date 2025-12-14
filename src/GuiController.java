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
    private Label labelDurationGreen;
    @FXML
    private Button buttonChangePhase;
    @FXML
    private Button buttonRedDuration;
    @FXML
    private Button buttonGreenDuration;
    @FXML
    private TextField textFieldRedDuration;
    @FXML
    private TextField textFieldGreenDuration;


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
        Car car = new Car();
        sim.addCar(car, comboBoxRoutes.getValue());
    }

    @FXML
    public void initialize(){
        comboBoxColors.setItems(FXCollections.observableArrayList("black", "white"));
    }
}
