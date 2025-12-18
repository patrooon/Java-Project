public class Camera2D {
    private float zoom=1;
    public static Vector2D defaultFrameSize=new Vector2D(1920,1080);
	private double halfwidth;
	private double halfheight;
    private Vector2D position;//this is the center of the screen/camerar
    private float rotation;//flat rotation (rotation around the y-axis in 3d)

	public void setZoom(float zoom) {
		if (zoom<=0){
			System.out.println("zoom must be larger than 0");
			return;
		}
		this.zoom = zoom;
	}
	Camera2D(float halfwidth,float halfheight){
		this.halfheight=halfheight;
		this.halfwidth=halfwidth;
		zoom=1;
		this.position=new Vector2D(0,0);
	}

	public Transform2D getLocalTransformFromGlobal(Transform2D global){
        //TODO verify that rotation and zoom work properly
		Vector2D newPosition=global.getPosition().subtract(position);
		newPosition.rotatedBy(-rotation);
		return new Transform2D(newPosition, global.getRotation()-rotation, global.getScale()*zoom);
		//Local transforms are not yet in screen space. For that there is the getScreenPositionFromLocal method.
    }
	public Vector2D getScreenPositionFromLocal(Transform2D local){
		return new Vector2D(0.5f+zoom*local.getPosition().x/ defaultFrameSize.x,0.5f+zoom*local.getPosition().y/ defaultFrameSize.y);
		// this returns a vector between (0,0)  and (1,1) where 0,0 is the top left corner and 1,1 is the bottom right corner
		// assuming the local_position is within the framesize
	}
}
