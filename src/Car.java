import org.eclipse.sumo.libtraci.Vehicle;
import org.eclipse.sumo.libtraci.Simulation;

public class Car {
	static int currentID=0;

	private String id;
	private double speed;

	public Car() {
		this.id = "Veh"+currentID;
		currentID++;
	}
	public Car(String id){
		this.id=id;
		currentID++;
	}
	public void update(){
		setSpeed(Vehicle.getSpeed(id));
	}

	public double getSpeed() {
		return speed;
	}



	public void setSpeed(double s) {
		this.speed = s;
	}

	@Override
	public String toString() {
		return "Car with ID: "+id;
	}

	public String getId() {
		return this.id;
	}

}
