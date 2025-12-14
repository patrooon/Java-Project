import org.eclipse.sumo.libtraci.*;
import org.eclipse.sumo.libtraci.Route;

public class Simulation {
	//contains the objects within the current simulation
	private Car[] cars;
	private route[] routes;
	private trafficLight[] trafficLights;
    private Statistics stats;
	Simulation(){
		cars=new Car[0];
		trafficLights=new trafficLight[0];
        stats=new Statistics();
	}
	void addCar(Car c,String routeID){
		//pretty inefficient, maybe use  a linked list instead. or  a dictionary
		Vehicle.add(c.getId(),routeID,"Car");
		Car[] g=new Car[cars.length+1];
		for(int i=0;i< cars.length;i++){
			g[i]=cars[i];
		}
		g[cars.length]=c;
		cars=g;
	}
	void createNewCar(String edge,String initialSpeed,String color,String route){
		Car c=new Car();
		c.setSpeed(Integer.parseInt(initialSpeed));
		c.setColor(color);
		addCar(c,route);
	}
	Car getCarFromID(String carID){
		for(Car c:getCars()){
			if (c.getId().equals(carID)){
				return c;
			}
		}
		return null;
	}

	String getCarsColorFromID(String carID){
		Car c=getCarFromID(carID);
		if (c!=null){
			return c.getColor();
		}
		return null;

	}
	String getCarsSpeedFromID(String carID){
		Car c=getCarFromID(carID);
		if (c!=null){
			return String.valueOf(c.getSpeed());
		}
		return null;

	}
	String getCarsRouteFromID(String carID){
		Car c=getCarFromID(carID);
		if (c!=null){
			return String.valueOf(c.getSpeed());
		}
		return null;

	}

	public Car[] getCars() {
		return cars;
	}
	// might be useful if we want to select cars from the map directly
	public Car getClosestCarToPosition(Vector2D globalPosition){
		float leastDist=-1;
		Car closestCar=null;
		for (Car i :getCars()){
			if (leastDist==-1 || i.getPosition().distanceTo(globalPosition)<leastDist){
				leastDist=i.getPosition().distanceTo(globalPosition);
				closestCar=i;
			}
		}
		return closestCar;
	}

	public trafficLight[] getTrafficLights() {
		return trafficLights;
	}

	public void load() {
		org.eclipse.sumo.libtraci.Simulation.preloadLibraries();
	}
	public void printCars(){
		for (int i=0;i<cars.length;i++){
			System.out.println(cars[i]);
		}
	}
	public void printTrafficLights(){
		for (int i=0;i<trafficLights.length;i++){
			System.out.println(trafficLights[i]);
		}
	}

	public void start(String cfg, int Delay) {
		load();
		org.eclipse.sumo.libtraci.Simulation.start(new StringVector(new String[] {"sumo-gui", "-c", cfg, "--start", "--delay", String.valueOf(Delay)}));
		step();
		trafficLights=getInitialTrafficLights();
		cars=getInitialCars();
		printTrafficLights();
	}
	public trafficLight[] getInitialTrafficLights(){
		String[] tlIDs= TrafficLight.getIDList().toArray(new String[0]);
		trafficLight[] lights=new trafficLight[tlIDs.length];
		for (int i=0;i< tlIDs.length;i++){
			lights[i]=new trafficLight(tlIDs[i]);
		}
		return lights;
	}
	public Car[] getInitialCars(){
		String[] carIDs=Vehicle.getIDList().toArray(new String[0]);
		Car[] cars=new Car[carIDs.length];
		for (int i=0;i< carIDs.length;i++){

			cars[i]=new Car(carIDs[i]);
		}
		return cars;
	}
	public route[] getInitialRoutes(){
		String[] routeIDs= Route.getIDList().toArray(new String[0]);
		route[] routes=new route[routeIDs.length];
		for (int i=0;i< routeIDs.length;i++){
			routes[i]=new route(routeIDs[i],Route.getEdges(routeIDs[i]));

		}
		return routes;
	}
	public route getRouteFromID(String routeID){
		for(route r:getRoutes()){
			if (r.getID().equals(routeID)){
				return r;
			}
		}
		return null;
	}
	public route[] getRoutes(){
		return routes;
	}
	public String[] getRouteIDs(){
		route[] routes=getRoutes();
		String[] ids=new String[routes.length];
		for (int i=0;i<routes.length;i++){
			ids[i]=routes[i].getID();
		}
		return ids;
	}
	public String[] getCarIDs(){
		Car[] cars=getCars();
		String[] ids=new String[cars.length];
		for (int i=0;i<cars.length;i++){
			ids[i]=cars[i].getId();
		}
		return ids;
	}
	public String[] getColors(){
		return null;
		//TODO add a list of preset colors
	}
	public String[] getTrafficLightIDs(){
		trafficLight[] tls=getTrafficLights();
		String[] ids=new String[tls.length];
		for (int i=0;i<tls.length;i++){
			ids[i]=tls[i].getID();
		}
		return ids;
	}
	public trafficLight getTrafficLightFromID(String id){
		for (trafficLight t:getTrafficLights()){
			if (t.getID().equals(id)){
				return t;
			}
		}
		return null;
	}


	public void step() {
        // called from the simulation thread once per tick
		org.eclipse.sumo.libtraci.Simulation.step();
		for (int i=0;i<cars.length;i++){
			//cars[i].update();
		}
        for (int i=0;i<trafficLights.length;i++){
            trafficLights[i].update();
        }
        stats.update();
	}
}

