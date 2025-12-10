public class Transform2D {
    private Vector2 position;
    private float rotation;
    private float scale;

    public float getRotation() {
        return rotation;
    }

    public Vector2 getPosition() {
        return position;
    }

    public float getScale() {
        return scale;
    }
    public void setPosition(Vector2 pos){
        this.position=pos;
    }
    public void rotate(float diff){
        rotation+=diff;
        if (rotation>=3.14159*2){
            rotation-=3.14159*2;
        }
        if (rotation<0){
            rotation+=3.14159*2;
        }
    }
    public Transform2D(Vector2 position){
        this.position=position;
        rotation=0;
        scale=1;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }
}
