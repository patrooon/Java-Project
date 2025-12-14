public class Transform2D {
    private Vector2D position;
    private float rotation;
    private float scale;

    public float getRotation() {
        return rotation;
    }

    public Vector2D getPosition() {
        return position;
    }

    public float getScale() {
        return scale;
    }
    public void setPosition(Vector2D pos){
        this.position=pos;
    }
    public void rotate(float diff){// Rotate by a difference
        rotation+=diff;
		// Keep rotation between 0 and 2*Pi
        if (rotation>=3.14159*2){
            rotation-=3.14159*2;
        }
        if (rotation<0){
            rotation+=3.14159*2;
        }
    }
	// Constructors

	// Constructor: position, default rotation=0, scale=1
    public Transform2D(Vector2D position){
        this.position=position;
        rotation=0;
        scale=1;
    }
	public Transform2D(){
		this.position=new Vector2D(0,0);
		rotation=0;
		scale=1;
	}
	// Constructor:  position, rotation, scale
	public Transform2D(Vector2D position,float rotation, float scale){
		this.position=position;
		this.rotation=rotation;
		this.scale=scale;
	}

    public void setScale(float scale) {
        this.scale = scale;
    }
}
