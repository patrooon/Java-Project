import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

public class GuiController {
    @FXML
    private ComboBox<String> comboBoxEdges;
    @FXML
    private ComboBox<String> comboBoxColors;
    @FXML
    private ComboBox<String> comboBoxRoutes;

    public void initialize(){
        comboBoxEdges.setItems(FXCollections.observableArrayList());
        comboBoxColors.setItems(FXCollections.observableArrayList("black", "white"));
        comboBoxRoutes.setItems(FXCollections.observableArrayList());
    }

}

