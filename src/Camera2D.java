public class Camera2D {
    private float zoom=1;
    public static Vector2 defaultFrameSize=new Vector2(1920,1080);
    private Vector2 position;
    private float rotation;//flat rotation (rotation around the y-axis in 3d)
    public Transform2D getLocalTransformFromGlobal(Transform2D global){
        //TODO include rotation and zoom
        Transform2D result=new Transform2D(global.getPosition().subtract(position));
        if (!(zoom==0)){
            result.setScale(1/zoom);
        }
        return result;
    }
}
