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
import javafx.scene.image.Image;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Color;

import javax.swing.*;
//import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.util.Arrays;

public class GuiController {
	private Image carImage;
	private Image trafficLightGrey;
	private Image trafficLightGreen;
	private Image trafficLightRed;
	private Image trafficLightYellow;
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
	final double TEXTURERADIUS=8;

    //Simulation
    private Simulation sim;
	private boolean isConfigStarted=false;
	public void loadConfig(){
		isConfigStarted=true;
	}
    public void setSimulation(Simulation sim){
		System.out.println("sim started");
        this.sim = sim;
        comboBoxFill();
    }
	public void loadImagesFromDisk(){
		carImage=new Image(new File("textures/car_icon.png").toURI().toString());//.getImage();
		trafficLightYellow=new Image(new File("textures/yellow_light.png").toURI().toString());
		trafficLightGreen=new Image(new File("textures/green_light.png").toURI().toString());
		trafficLightRed=new Image(new File("textures/red_light.png").toURI().toString());
		trafficLightGrey=new Image(new File("textures/grey_light.png").toURI().toString());
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

    public void draw() {
        GraphicsContext gc = canvasMap.getGraphicsContext2D();
        double w = canvasMap.getWidth();
        double h = canvasMap.getHeight();

        double halfwidth = w / 2;
        double halfheight = h / 2;

        drawMapFX(gc, w, h, halfwidth, halfheight);
    }

    private void drawMapFX(GraphicsContext gc, double w, double h,
                           double halfwidth, double halfheight) {
		gc.setFill(Color.WHITE);
		gc.fillRect(0, 0, w, h);
		if (sim==null){
			System.out.println("is null");
			//gc.drawImage(carImage,300,200);
			//System.out.println("is null");
			return;
		}
		for (Lane lane:sim.getLanes()){
			for(int i=0;i<lane.getLine().size()-1;i++){
				gc.strokeLine(lane.getLine().get(i).x+halfwidth,-lane.getLine().get(i).y+halfheight,lane.getLine().get(i+1).x+halfwidth,-lane.getLine().get(i+1).y+halfheight);
			}
		}
		System.out.println("not null");

		//return;
		// Static textures
        /*
        for (trafficLight tl:sim.getTrafficLights()){
            if (tl.getTrafficLight().equals("")){
                for(int i=0;i<tl.getStopLinePositions().size();i++){
                    gc.drawImage(trafficLightGrey,tl.getStopLinePositions().get(i).x-TEXTURERADIUS+halfwidth,-tl.getStopLinePositions().get(i).x-TEXTURERADIUS+halfheight);
                }
            }
            else{
                for (int i=0;i<tl.getTrafficLight().length();i++){
                    if (tl.getTrafficLight().charAt(i)=='G'){
                        gc.drawImage(trafficLightGreen,tl.getStopLinePositions().get(i).x-TEXTURERADIUS+halfwidth,-tl.getStopLinePositions().get(i).x-TEXTURERADIUS+halfheight);
                    }
                    if (tl.getTrafficLight().charAt(i)=='y'){
                        gc.drawImage(trafficLightYellow,tl.getStopLinePositions().get(i).x-TEXTURERADIUS+halfwidth,-tl.getStopLinePositions().get(i).x-TEXTURERADIUS+halfheight);
                    }
                    if (tl.getTrafficLight().charAt(i)=='r'){
                        gc.drawImage(trafficLightRed,tl.getStopLinePositions().get(i).x-TEXTURERADIUS+halfwidth,-tl.getStopLinePositions().get(i).x-TEXTURERADIUS+halfheight);
                    }
                }
            }
        }
        */
		for (Car car : sim.getCars()) {
			gc.drawImage(carImage,car.getPosition().x-TEXTURERADIUS+halfwidth,-car.getPosition().y-TEXTURERADIUS+halfheight);
		}
		/*
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, w, h);

        Color roadFill = Color.rgb(235, 235, 235);
        Color roadEdge = Color.rgb(150, 150, 150);

        gc.setStroke(roadFill);
        gc.setLineWidth(roadW);

        gc.strokeLine(20, halfheight - gap, w - 20, halfheight - gap);
        gc.setStroke(roadEdge);
        gc.setLineWidth(2);
        */

    }

    @FXML
    public void initialize(){
        comboBoxColors.setItems(FXCollections.observableArrayList("black", "white"));
        draw();
    }
}
