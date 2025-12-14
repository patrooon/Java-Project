public class Vector2D {
    public float x;
    public float y;
    public Vector2D(float x, float y){ // Vector with values x and y
        this.x=x;
        this.y=y;
    }
    public float length(){
        return (float) Math.sqrt(x*x+y*y);
    }
    public void normalize(){// Normalize vector (make the length==1)
        float len=length();
        if (len<=0.001){
            return;
        }
        float inverse=1/len;
        x*=inverse;
        y*=inverse;
    }
	// Subtracts another vector from this vector
    public Vector2D subtract(Vector2D other){
        x-=other.x;
        y-=other.y;
        return this;
    }
	// Distance to another vector
	public float distanceTo(Vector2D other){
		return this.subtract(other).length();
	}
	public void rotatedBy(float radians){
		float x=this.x;
		float y=this.y;
		this.x=(float) (x * Math.cos(radians) - y * Math.sin(radians));
		this.y=(float) (x * Math.sin(radians) + y * Math.cos(radians));
	}
}
