import org.eclipse.sumo.libtraci.TrafficLight;

public class trafficLight {

	private String currentColor;
	private final String id;
	private float cycleLength;
	private Transform2D transform;


	public trafficLight(String id) {
		this.id = id;
		currentColor ="Red";
	}

	public void setTrafficLight() {
		TrafficLight.setRedYellowGreenState(id, currentColor);
	}

	public void setTrafficLight(String value1) {
		this.currentColor = value1;
		setTrafficLight();
	}
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
