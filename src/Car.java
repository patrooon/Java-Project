import org.eclipse.sumo.libtraci.TraCIPosition;
import org.eclipse.sumo.libtraci.Vehicle;

public class Car {

	// attributes: ID, speed, transform
	private static int currentID=0;
	private final String id;
	private double speed;
	private String edge;
	private String route;
	private String color;//color as a hexcode
	private Transform2D transform;
	private Vector2D lastPosition = null;
	private double angle = 0.0; // für die Rotation
    private double departTime = -1;  // start time (seconds)
    private boolean arrived = false; // finished trip



    public Vector2D getPosition(){
		return this.transform.getPosition();
	}
	// Constructors
	public Car() {
		this.id = "Veh"+currentID;
		currentID++;
		transform= new Transform2D();
	}
	public double getAngle() {
		return angle;
	}
	public Car(String id){
		//if (!id.equals("Veh"+String.valueOf(currentID))){
		//    System.out.println("vehicle initialized with invalid ID");
		//}
		this.id=id;
		currentID++;
		transform=new Transform2D();
	}
    // functions
    //updates data each step
    public void update(){
        //  If vehicle is gone it fnished
        if (!Vehicle.getIDList().contains(id)) {
            arrived = true;
            return;
        }

        // simulation time
        if (departTime < 0) {
            departTime = org.eclipse.sumo.libtraci.Simulation.getTime();
        }

        // Update current edge
        this.edge = Vehicle.getRoadID(id);

        // Update current speed
        this.speed = Vehicle.getSpeed(id);

        // Update position
        TraCIPosition tracipos = Vehicle.getPosition(id);
        Vector2D newPos = new Vector2D(
                (float) tracipos.getX(),
                (float) tracipos.getY()
        );

        if (lastPosition != null) {
            double dx = newPos.x - lastPosition.x;
            double dy = newPos.y - lastPosition.y;

            if (dx != 0 || dy != 0) {
                angle = Math.toDegrees(Math.atan2(dy, dx));
                angle = -angle; // wegen invertierter Y-Achse im GUI
            }
        }

        lastPosition = newPos;
        transform.setPosition(newPos);



		/*
		if(!Vehicle.getIDList().contains(id)){
			System.out.println("cant find vehicle id");
			System.out.println(id);
			return;
		}
		System.out.println("id found");
		//setSpeed(Vehicle.getSpeed(id));
        //TODO look into tracipos and how to directly use it for rendering, rather than casting to vector2
        TraCIPosition tracipos=Vehicle.getPosition(id);
        transform.setPosition(new Vector2D((float) tracipos.getX(), (float) tracipos.getY()));//all this does is overwrite the position in transform with the one in sumo
	*/
    }
	public void setColor(String hexColor){
		this.color=hexColor;
	}
	public String getColor() {
		return color;
	}
	public String getRoute(){
		return route;
	}
	public String getEdge(){
		return edge;
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
	// Get car ID
	public String getId() {
		return this.id;
	}
	// Get car transform
	public Transform2D getTransform(){
		return transform;
	}

}
