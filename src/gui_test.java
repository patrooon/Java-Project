public class gui_test {
	//this is the class that should be executed to run the simulation
	//the GitHub repo can be found at https://github.com/patrooon/Java-Project


	public static void main(String[] args) {
		trafficLight tl = new trafficLight("clusterJ6_J7_J8");
		Simulation sim=new Simulation();
		sim.start("SumoConfig/hello.sumocfg", 100);
		for (int i = 0; i < 15; i++) {
			Car car = new Car();
			sim.addCar(car, "route0");
			sim.step();
            //sim.printCars();
            //sim.printTrafficLights();
		}
		while(true){
			sim.step();
		}
	}
}