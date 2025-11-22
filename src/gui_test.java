import org.eclipse.sumo.libtraci.Simulation;
import org.eclipse.sumo.libtraci.StringVector;

public class gui_test {
	public static void main(String[] args) {
		Simulation.preloadLibraries();
		Simulation.start(
				new StringVector(new String[] {"sumo-gui", "-c", "hello.sumocfg", "--start", "--delay", "50"}));
		for (int i = 0; i < 100; i++) {
			Simulation.step();
		}
		Simulation.close();
	}
}
