import org.eclipse.sumo.libtraci.TrafficLight;

public class trafficLight {

	private String current_color;
	private String id;


	public trafficLight(String id) {
		this.id = id;
		current_color="Red";
	}

	public void setTrafficLight() {
		TrafficLight.setRedYellowGreenState(id, current_color);
	}

	public void setTrafficLight(String value1) {
		this.current_color = value1;
		setTrafficLight();
	}

	public String getTrafficLight() {
		return TrafficLight.getRedYellowGreenState(id);
	}

	@Override
	public String toString() {
		return "Traffic light with id: "+id+" and light: "+current_color;
	}
}
