import org.eclipse.sumo.libtraci.*;
import org.eclipse.sumo.libtraci.Route;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Simulation {
	final String defaultNetworkPath="SumoConfig/hello.net.xml";
	private String activeNetworkPath="";
	//contains the objects within the current simulation
	private HashMap<String,Car> cars;
	private ArrayList<Lane> lanes;
	private route[] routes;
	public boolean paused=false;
	private trafficLight[] trafficLights;
    private Statistics stats;
	private DynamicGraphics g;
	//constructor
	Simulation(){
		cars=new HashMap<String,Car>();
		trafficLights=new trafficLight[0];
        stats=new Statistics();
		lanes=new ArrayList<Lane>();
		g=new DynamicGraphics(null,null);
		try {
			loadNetwork("");
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (SAXException e) {
			throw new RuntimeException(e);
		}
	}
	void addCar(Car c,String routeID){
		Vehicle.add(c.getId(),routeID,"Car");
		cars.put(c.getId(),c);
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
	public void togglePause(){
		paused=!paused;
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
	//check what the exceptions are
	void loadNetwork(String pathname) throws ParserConfigurationException, IOException, SAXException {
		System.out.println("load_called");
		File file;
		if (pathname.isEmpty()) {
			file=new File(defaultNetworkPath);
		}
		else{
			file = new File(pathname);
		}
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document document = db.parse(file);
		document.getDocumentElement().normalize();
		Element root = document.getDocumentElement();
		NodeList tls=document.getElementsByTagName("tlLogic");
		NodeList junctions=document.getElementsByTagName("junction");
		NodeList edges = document.getElementsByTagName("edge");
		NodeList lanes =document.getElementsByTagName("lane");
		// horrible mess of code fix as soon as possible
		for (int i=0;i<lanes.getLength();i++) {
			Node l = lanes.item(i);
			if (l.getNodeType() == Node.ELEMENT_NODE) {
				Element laneElement = (Element) l;
				this.lanes.add(new Lane(laneElement.getAttribute("id"), laneElement.getAttribute("shape")));
			}
		}
		for (int i=0;i<tls.getLength();i++){
			Node tl=tls.item(i);
			if (tl.getNodeType()==Node.ELEMENT_NODE){
				Element el=(Element)tl;
				String id=el.getAttribute("id");
				System.out.println(id);
				trafficLight t=new trafficLight(id);
				ArrayList<Vector2D> stopLines=new ArrayList<Vector2D>();
				for(int j=0;j<junctions.getLength();j++){
					Node junction= junctions.item(j);
					if (junction.getNodeType()==Node.ELEMENT_NODE){
						Element junctionElement=(Element)junction;
						if (junctionElement.getAttribute("id").equals(id)){
							String[] laneNames=junctionElement.getAttribute("intLanes").split(" ");
							for(String s:laneNames) {
								for(int k=0;k<edges.getLength();k++){
									Node lane=lanes.item(k);
									if (lane.getNodeType()==Node.ELEMENT_NODE) {
										Element laneElement = (Element) lane;
										if (laneElement.getAttribute("id").equals(s)){
											String[] numbers= laneElement.getAttribute("shape").split("[ ,]+");
											stopLines.add(new Vector2D(Float.parseFloat(numbers[0]),Float.parseFloat(numbers[1])));
										}

									}
								}
							}
						}
					}
				}
				t.setStopLinePositions(stopLines);
			}
			else{
				System.out.println("not an element");
			}
		}

	}
	String getCarsRouteFromID(String carID){
		Car c=getCarFromID(carID);
		if (c!=null){
			return String.valueOf(c.getRoute());
		}
		return null;

	}
	public ArrayList<Lane> getLanes(){
		return lanes;
	}
	public Car[] getCars() {
		return cars.values().toArray(new Car[0]);
	}

	//might be useful if we want to select cars from the map directly
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
		for (Car c: cars.values()){
			System.out.println(c);
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
		org.eclipse.sumo.libtraci.Simulation.step();
		trafficLights=getInitialTrafficLights();
		cars=getInitialCars();
	}
	public trafficLight[] getInitialTrafficLights(){
		String[] tlIDs= TrafficLight.getIDList().toArray(new String[0]);
		trafficLight[] lights=new trafficLight[tlIDs.length];
		for (int i=0;i< tlIDs.length;i++){
			lights[i]=new trafficLight(tlIDs[i]);
		}
		return lights;
	}
	public HashMap<String,Car> getInitialCars(){
		String[] carIDs=Vehicle.getIDList().toArray(new String[0]);
		HashMap<String,Car> newmap= new HashMap<String,Car>();
		for (int i=0;i< carIDs.length;i++){
			newmap.put(carIDs[i],new Car(carIDs[i]));
		}
		return newmap;
	}
	public void updateCarMapping(){
		StringVector idList=Vehicle.getIDList();
		String[] keyList= cars.keySet().toArray(new String[0]);
		for (String id:keyList){
			if (!idList.contains(id)){
				cars.remove(id);
				System.out.println("removed car from simulation"+id);
			}
		}
		for (String id:idList){
			if (!cars.containsKey(id)){
				Car c=new Car();
				cars.put(c.getId(),c);
				System.out.println("added new car to the simulation "+ id);

			}
		}
	}
	public void setTrafficLightWithID(String id,String color){
		TrafficLight.setRedYellowGreenState(id,color);
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
        if (routes == null || routes.length == 0) return new String[] {"route0"};
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
	public void setTrafficLightCycleLengthFromID(String id,String color){
		trafficLight tl=getTrafficLightFromID(id);
		if(tl!=null){
			tl.setTrafficLight(color);
		}
	}
	public String getTrafficLightCycleLengthFromID(String id){
		trafficLight tl=getTrafficLightFromID(id);
		if(tl!=null){
			return tl.getTrafficLight();
		}
		return null;
	}
	public String getTrafficLightColorFromID(String id){
		trafficLight t=getTrafficLightFromID(id);
		if(t!=null){
			return t.getTrafficLight();
		}
		return null;
	}


	public void step() {
        // called from the simulation thread once per tick
		org.eclipse.sumo.libtraci.Simulation.step();
		updateCarMapping();
		for (Car c:cars.values()){
			c.update();
		}
        for (int i=0;i<trafficLights.length;i++){
            trafficLights[i].update();
        }
        stats.update();
		printCars();

	}
	public void addNumberOfCarsToRoute(int n,String RouteID){
		if (n<=0){
			return;
		}
		for (int i=0;i<n;i++){
			createNewCar("","10","",RouteID);
			}
		}
        public void add100Cars(){
            addNumberOfCarsToRoute(100,"Route0");
        }

}

