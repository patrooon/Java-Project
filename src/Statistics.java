public class Statistics {
    public enum properties{};
    //will be used for collecting and exporting Statistics of the traffic flow
    private int timesteps=0;
    public void getCurrentCarsPerRoute(){
    }
    public void update(){
		timesteps++;
	}
    private float[] averageCarsPerRoute;
    public void exportCSV(String filepath){}
    public void graphProperty(properties prop, int routeIndex){}
    public void exportProperty(properties prop, int routeIndex,String filepath){}
}
