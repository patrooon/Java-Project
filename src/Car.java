import org.eclipse.sumo.libtraci.TraCIPosition;
import org.eclipse.sumo.libtraci.Vehicle;

public class Car {
	private static int currentID=0;

	private final String id;
	private double speed;
    private Transform2D transform;

    public Vector2D getPosition(){
        return this.transform.getPosition();
    }

	public Car() {
		this.id = "Veh"+currentID;
		currentID++;
	}
	public Car(String id){
        if (!id.equals("Veh"+String.valueOf(currentID))){
            System.out.println("vehicle initialized with invalid ID");
        }
		this.id=id;
		currentID++;
	}
	public void update(){
		setSpeed(Vehicle.getSpeed(id));
        //TODO look into tracipos and how to directly use it for rendering, rather than casting to vector2
        TraCIPosition tracipos=Vehicle.getPosition(id);
        transform.setPosition(new Vector2D((float) tracipos.getX(), (float) tracipos.getY()));//all this does is overwrite the position in transform with the one in sumo
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
	public Transform2D getTransform(){
		return transform;
	}

}
