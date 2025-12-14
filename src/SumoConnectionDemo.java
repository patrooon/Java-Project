

import it.polito.appeal.traci.SumoTraciConnection;
import de.tudresden.sumo.cmd.Trafficlight;

public class SumoConnectionDemo {

    public static void main(String[] args) throws Exception {

        String sumoBinary = args.length > 0 ? args[0] : "sumo-gui";
        String configFile = args.length > 1 ? args[1] : ".scenario.sumocfg";
        String port = args.length > 2 ? args[2] : "1337"; // MUST be String

        // Correct constructor for your TraaS version:
        SumoTraciConnection conn =
                new SumoTraciConnection(sumoBinary, configFile, port);

        conn.addOption("start", "true");

        System.out.println("Starting SUMO...");
        conn.runServer();
        System.out.println("Connected!");

        Object lights = conn.do_job_get(Trafficlight.getIDList());
        System.out.println("Traffic lights: " + lights);

        for (int i = 0; i < 5; i++) {
            conn.do_timestep();
            System.out.println("Step " + i + " done.");
        }

        conn.close();
        System.out.println("Connection closed.");
    }
}
