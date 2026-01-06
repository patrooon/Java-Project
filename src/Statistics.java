public class Statistics {

	private Simulation sim;
	private int timesteps = 0;
	private float averageSpeed = 0;

	public Statistics(Simulation sim) {
		this.sim = sim;
	}

	public void update() {
		timesteps++;

		Car[] cars = sim.getCars();
		if (cars.length == 0) {
			averageSpeed = 0;
			return;
		}

		double sum = 0;
		for (Car c : cars) {
			sum += c.getSpeed();
		}
		averageSpeed = (float)(sum / cars.length);
	}

	public float getAverageSpeed() {
		return averageSpeed;
	}
}
