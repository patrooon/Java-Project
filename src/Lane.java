import java.util.ArrayList;

public class Lane {
	final String id;
	private	final ArrayList<Vector2D> Line;
	Lane(String id,ArrayList<Vector2D> Line){
		this.id=id;
		this.Line=Line;
	}
	Lane(String id,String stringOfPoints){
		this.id=id;
		Line=new ArrayList<Vector2D>();
		for (String s:stringOfPoints.split(" ")){
			String[] p=s.split(",");
			Vector2D point=new Vector2D(Float.parseFloat(p[0]),Float.parseFloat(p[1]));
			Line.add(point);
		}
	}

	public ArrayList<Vector2D> getLine() {
		return Line;
	}
}
