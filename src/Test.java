import org.eclipse.sumo.libtraci.Simulation;
import org.eclipse.sumo.libtraci.StringVector;
import org.eclipse.sumo.libtraci.Vehicle;

public class Test{
	public static void main(String[] args) {
		Simulation.preloadLibraries();
		Simulation.start(new StringVector(new String[] {"sumo-gui", "-n", "hello.net.xml"}));
		for (int i = 0; i <32 ; i++) {
			Simulation.step();
			Vehicle.add("Veh"+String.valueOf(i+1),"route0","passenger",String.valueOf(Simulation.getCurrentTime() / 1000.0));
		}
		Simulation.close();
	}
}
