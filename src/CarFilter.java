import java.lang.reflect.Array;
import java.util.*;

public class CarFilter{
	private route[] routes;
	enum Colors{WHITE,BLACK,BLUE,RED,GREEN,YELLOW}
	HashMap<Colors,Boolean> colorFilter;
	HashMap<route,Boolean> routeFilter;
	CarFilter(HashMap<Colors,Boolean> col,HashMap<route,Boolean> rou){
		this.colorFilter=col;
		this.routeFilter=rou;
	}
	CarFilter(route[] routes){
		this.routes=routes;
		routeFilter=new HashMap<route,Boolean>();
		for (route route:routes){
			routeFilter.put(route,true);
		}
		colorFilter=new HashMap<Colors,Boolean>();
		for(int i=0;i<Colors.values().length;i++){
			colorFilter.put(Colors.values()[i],true);
		}
	}
	boolean doesCarFitFilter(Car car){
		for (int i=0;i<Colors.values().length;i++){
			if (!colorFilter.get(i)){
				return false;
			}
		}
		for (route route : routes) {
			if (!routeFilter.get(route)) {
				return false;
			}
		}
		return true;
	}
	public ArrayList<Car> applyFilter(HashMap<String,Car> allCars){
		ArrayList<Car> resultCars= (ArrayList<Car>) allCars.values();
		resultCars.removeIf(c -> !doesCarFitFilter(c));//this is the filtering step
		return resultCars;
	}
	public void setColorValue(Colors c,boolean bool){
		colorFilter.put(c,bool);
	}
	public void setRouteValue(route r,boolean bool){
		routeFilter.put(r,bool);
	}
}
