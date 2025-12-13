import org.eclipse.sumo.libtraci.StringVector;

public class route {
	private final String[] edgeIDs;//list of all the edges the route consists of
	private final String id;
	String[] getEdgeIDs(){
		return edgeIDs;
	}
	route(String ID,String[] edgeIDs){
		this.edgeIDs=edgeIDs;
		this.id=ID;
	}
	route(String ID,StringVector ids){
		this.edgeIDs= ids.toArray(new String[0]);
		this.id=ID;
	}
	public String getID(){
		return id;
	}
}
