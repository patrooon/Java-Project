import org.eclipse.sumo.libtraci.TraCIPosition;
import org.eclipse.sumo.libtraci.Vehicle;
import org.eclipse.sumo.libtraci.Simulation;

public class Car {
	private static int currentID=0;

	private String id;
	private double speed;
    private Vector2 position;
    private float rotation;//y rotation in 3dspace

    public Vector2 getPosition(){
        return this.position;
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
        position=new Vector2((float) tracipos.getX(), (float) tracipos.getY());
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
