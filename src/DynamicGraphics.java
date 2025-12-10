public class DynamicGraphics {
    //used for moving/changing visual elements like cars and traffic lights
    public void renderCurrentState(Camera2D camera, Car[] cars, trafficLight[] trafficLights, GraphicalMap staticMap){
    //called from the rendering thread once per frame
		for (Car car : cars) {
			displayTexture("Car_texture", camera.getLocalTransformFromGlobal(car.getTransform()));
		}
		for (trafficLight tl:trafficLights){
			displayTexture("TL_Texture",camera.getLocalTransformFromGlobal(tl.getTransform()));
		}
    }
	public void displayTexture(String texturepath /* or a Texture class from a library*/,Transform2D transform){
		//renders and individual texture at the given position,rotation and scale
	}
}
