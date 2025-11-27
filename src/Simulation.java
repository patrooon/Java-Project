import org.eclipse.sumo.libtraci.StringVector;
import org.eclipse.sumo.libtraci.TrafficLight;
import org.eclipse.sumo.libtraci.Vehicle;

public class Simulation {
	//contains the objects within the current simulation
	private Car[] cars;
	private trafficLight[] trafficLights;
	Simulation(){
		cars=new Car[0];
		trafficLights=new trafficLight[0];
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

	public Car[] getCars() {
		return cars;
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

	public void step() {
		org.eclipse.sumo.libtraci.Simulation.step();
		for (int i=0;i<cars.length;i++){
			//cars[i].update();

		}
	}
}

