import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class GuiMain extends Application {

    private Simulation sim;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(GuiMain.class.getResource("gui.fxml"));
        Parent root = fxmlLoader.load();
        GuiController controller = fxmlLoader.getController();
        sim = new Simulation();
        controller.setSimulation(sim);
		controller.loadImagesFromDisk();
        Scene scene = new Scene(root, 708, 486);
        stage.setTitle("Simulation");
        stage.setScene(scene);
        stage.show();
        new Thread(() -> roadsim(sim, controller), "RoadSim-Thread").start();
    }

    public void roadsim(Simulation sim, GuiController controller){
        sim.start("SumoConfig/hello.sumocfg", 100);
		sim.step();
        while(true){
			if(!sim.paused) {
				sim.step();
			}
            Platform.runLater(controller::comboBoxFill);
            Platform.runLater(controller::currentCar);
			Platform.runLater(controller::draw);
        }
    }
}
