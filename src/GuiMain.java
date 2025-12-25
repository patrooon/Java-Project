import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class GuiMain extends Application {

    private Simulation sim;
    private Thread simThread;
    private GuiController controller;
    

    @Override
    public void start(Stage stage) throws IOException {
    	
        FXMLLoader fxmlLoader = new FXMLLoader(GuiMain.class.getResource("gui.fxml"));
        Parent root = fxmlLoader.load();
        controller = fxmlLoader.getController();
        
        sim = new Simulation();
        controller.setSimulation(sim);
		controller.loadImagesFromDisk();
		
		controller.setOnRestart(this::restartSimulation);
		
        Scene scene = new Scene(root, 708, 486);
        stage.setTitle("Simulation");
        stage.setScene(scene);
        stage.show();
        
        startSimulation();
    }
    
    private void startSimulation() {
    	sim.startSimulation(100);
    	simThread = new Thread(()->{
    		while(sim.isRunning()) {
    			
    			if(!sim.paused) {
    				sim.step();
    			}
    			
    			Platform.runLater(controller::comboBoxFill);              
    			Platform.runLater(controller::currentCar);
  			    Platform.runLater(controller::draw);
    			
    			try {
    				Thread.sleep(100);
    			} catch (InterruptedException ignored) {}
    		}
    	});
    	
    	simThread.start();
    }
    
    private void restartSimulation() {
    	sim.stopSimulation();
    	try {
    		simThread.join();
    	} catch (InterruptedException ignored) {}
    	
    	sim.reloadNetwork(sim.getCurrentNetFile());
    	Platform.runLater(controller::draw);
    	
    	startSimulation();
    }

//    public void roadsim(Simulation sim, GuiController controller){
//        sim.start(sim.getSumocfgPath(), 100);
//		sim.step();
//        while(true){
//			if(!sim.paused) {
//				sim.step();
//			}
//            Platform.runLater(controller::comboBoxFill);
//            Platform.runLater(controller::currentCar);
//			  Platform.runLater(controller::draw);
//        }
//    }
}
