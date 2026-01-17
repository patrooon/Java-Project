import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.sumo.libtraci.Edge;
import org.eclipse.sumo.libtraci.Vehicle;

public class Statistics {

    static final double CONGESTION_THRESHOLD = 0.15; // hotspot limit

    private final Simulation sim;

    private float averageSpeed = 0f; //initial value
    private Map<String, Double> densityPerEdge = new HashMap<>();
    private int congestionHotspots = 0;

    // travel time tracking
    private Map<String, Double> departTimes = new HashMap<>(); //start time per vehicle
    private Set<String> lastSeenIds = new HashSet<>();         //vehicles from last step
    private List<Double> finishedTravelTimes = new ArrayList<>(); //finished travel times

    public Statistics(Simulation sim) {
        this.sim = sim;
    }

    public float getAverageSpeed() {
        return averageSpeed;
    }

    public Map<String, Double> getDensityPerEdge() {
        return densityPerEdge;
    }

    public int getCongestionHotspots() {
        return congestionHotspots;
    }

    public double getAverageTravelTime() {
        if (finishedTravelTimes.isEmpty()) return -1;

        double sum = 0;
        for (double t : finishedTravelTimes) {
            sum += t;
        }
        return sum / finishedTravelTimes.size();
    }

    public void update() {

        double now = org.eclipse.sumo.libtraci.Simulation.getTime();

        //get all current vehicle ids
        Set<String> currentIds = new HashSet<>();
        for (String id : Vehicle.getIDList()) {
            currentIds.add(id);

            //remember start time for new vehicles
            departTimes.putIfAbsent(id, now);
        }

        //vehicles that disappeared are finished
        for (String oldId : lastSeenIds) {
            if (!currentIds.contains(oldId)) {
                Double dep = departTimes.get(oldId);
                if (dep != null) {
                    finishedTravelTimes.add(now - dep);
                }
                departTimes.remove(oldId);
            }
        }

        lastSeenIds = currentIds;

        Car[] cars = sim.getCars();
        if (cars == null || cars.length == 0) {
            return;
        }

        //calculate average speed
        double sumSpeed = 0;
        for (Car c : cars) {
            sumSpeed += c.getSpeed();
        }
        averageSpeed = (float) (sumSpeed / cars.length);

        //count cars per edge
        Map<String, Integer> countPerEdge = new HashMap<>();
        for (Car c : cars) {
            String edgeId = c.getEdge();
            if (edgeId == null || edgeId.isEmpty()) continue;
            if (edgeId.startsWith(":")) continue;

            countPerEdge.put(edgeId, countPerEdge.getOrDefault(edgeId, 0) + 1);
        }

        //calculate density per edge
        densityPerEdge.clear();
        for (Map.Entry<String, Integer> e : countPerEdge.entrySet()) {
            double length = Edge.getLastStepLength(e.getKey());
            if (length <= 0) continue;

            double density = e.getValue() / length;
            densityPerEdge.put(e.getKey(), density);
        }

        //count hotspots
        congestionHotspots = 0;
        for (double density : densityPerEdge.values()) {
            if (density >= CONGESTION_THRESHOLD) {
                congestionHotspots++;
            }
        }
    }
    //travel time for chart
    public List<Double> getAllTravelTimes() {
        return List.of();
    }

}
