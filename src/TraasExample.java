import it.polito.appeal.traci.SumoTraciConnection;

public class TraasExample {
	public static void main(String[] args) throws Exception {

		// Start SUMO and connect via TraCI
		SumoTraciConnection conn = new SumoTraciConnection("sumo-gui","hello.net.xml","hello.rou.xml");
		conn.runServer();                // launches SUMO
		conn.setOrder(1);                // required by TraCI
		// Step simulation
		for (int i = 0; i < 1000; i++) {
			conn.do_timestep();
		}

		//conn.close();
	}
}
