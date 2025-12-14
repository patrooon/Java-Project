import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class GuiMain extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(GuiMain.class.getResource("gui.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 708, 486);
        stage.setTitle("Simulation");
        stage.setScene(scene);
        stage.show();
        new Thread(this::roadsim, "RoadSim-Thread").start();
    }

    public void roadsim(){
        trafficLight tl = new trafficLight("clusterJ6_J7_J8");
        Simulation sim=new Simulation();
        sim.start("SumoConfig/hello.sumocfg", 100);
        for (int i = 0; i < 15; i++) {
            Car car = new Car();
            sim.addCar(car, "route0");
            sim.step();
            //sim.printCars();
            //sim.printTrafficLights();
        }
        while(true){
            sim.step();
        }
    }
}

