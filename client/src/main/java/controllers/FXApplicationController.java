package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

public class FXApplicationController {
    @FXML
    private Button btn;

    @FXML
    private void click(ActionEvent event) {
        btn.setOnAction(e -> {
//            openScene(primaryStage);
//            Alert alert = new Alert(Alert.AlertType.INFORMATION, "EEEEEEEE!!!");
//            alert.showAndWait();
        });
    }
}
