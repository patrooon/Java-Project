import org.eclipse.sumo.libtraci.TrafficLight;

import java.util.ArrayList;

public class trafficLight {
	// Stores the current color of the traffic light
	private String currentColor;//initially GrG
	private ArrayList<Vector2D> stopLinePositions;
	// Unique id of the traffic light
	private final String id;
	// Length of one light cycle
	private float cycleLength;
	// Position of the traffic light
	private Transform2D transform;

	// Creates a traffic light with id
	public trafficLight(String id) {
		this.id = id;
		currentColor="";
	}
    public ArrayList<Vector2D> getStopLinePositions(){
        return stopLinePositions;
    }
	public void setStopLinePositions(ArrayList<Vector2D> stopLinePositions) {
		this.stopLinePositions = stopLinePositions;
		for (Vector2D v:stopLinePositions){
			System.out.println(v);
		}
	}

	// Sends the current color to SUMO
	public void setTrafficLight() {
		TrafficLight.setRedYellowGreenState(id, currentColor);
	}
	// Sets the cycle length of the traffic light
	public void setCycleLengthRed(float cycleLength) {
		if (cycleLength<=0){
			System.out.println("the traffic light's cycle length needs to be a positive number");
			return;
		}
		this.cycleLength = cycleLength;
		TrafficLight.setPhaseDuration(id,cycleLength);
	}

	public float getCycleLength(){
		return cycleLength;
	}
	public String getID(){
		return id;
	}
	// Sets a new traffic light color
	public void setTrafficLight(String value1) {
		this.currentColor = value1;
		setTrafficLight();
	}
	// Updates current color from SUMO each tick
    public void update(){
        currentColor=TrafficLight.getRedYellowGreenState(id);
    }

	public String getTrafficLight() {
		return TrafficLight.getRedYellowGreenState(id);
	}
	@Override
	public String toString() {
		return "Traffic light with id: "+id+" and light: "+ currentColor;
	}
	public Transform2D getTransform(){
		return transform;
	}
}
