public class Vector2 {
    public float x;
    public float y;
    public Vector2(float x,float y){
        this.x=x;
        this.y=y;
    }
    public float length(){
        return (float) Math.sqrt(x*x+y*y);
    }
    public void normalize(){
        float len=length();
        if (len<=0.001){
            return;
        }
        float inverse=1/len;
        x*=inverse;
        y*=inverse;
    }
    public Vector2 subtract(Vector2 other){
        x-=other.x;
        y-=other.y;
        return this;
    }
}
