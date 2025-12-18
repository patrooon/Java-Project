import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;

public class DynamicGraphics {
	Graphics2D g2;
	Image carImage;
	JPanel jp;
	public DynamicGraphics(Graphics2D g2,JPanel jp){
		this.jp=jp;
		this.g2=g2;

	}
    //used for moving/changing visual elements like cars and traffic lights
    public void renderCurrentState(Camera2D camera, Car[] cars, trafficLight[] trafficLights){
    //called from the rendering thread once per frame
		for (trafficLight tl:trafficLights){
			//displayTexture("TL_Texture",camera.getLocalTransformFromGlobal(tl.getTransform()));
		}
    }
}
