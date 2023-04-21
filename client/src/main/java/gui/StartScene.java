package gui;

import auxiliary_classes.ResponseMessage;
import functional_classes.ClientManager;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

public class StartScene {
    FXApplication app;
    ClientManager clientManager;
    Label usernameLabel;
    Label passwordLabel;
    TextField usernameField;
    PasswordField passwordField;
    String mode;

    public StartScene(FXApplication app, ClientManager clientManager) {
        this.app = app;
        this.clientManager = clientManager;
        mode = "L";
    }

    public Scene common() {
        GridPane root = new GridPane();

        usernameLabel = new Label("Username:");
        usernameField = new TextField();
        passwordLabel = new Label("Password:");
        passwordField = new PasswordField();
        Button changeModBtn = new Button((Objects.equals(mode, "L") ? "Не зарегистрированы? К форме регистрации!" : "Уже зарегистрированы? Авторизоваться!"));
        changeModBtn.setOnAction(e -> {
            mode = (Objects.equals(mode, "L") ? "R" : "L");
            this.common();
        });

        root.setHgap(10);
        root.setVgap(10);
        root.setPadding(new Insets(10));
        root.add(usernameLabel, 0, 0);
        root.add(usernameField, 1, 0);
        root.add(passwordLabel, 0, 1);
        root.add(passwordField, 1, 1);

        HBox buttonLayout = new HBox(10);
        buttonLayout.getChildren().add(Objects.equals(mode, "L") ? authorization() : registration());
        root.add(buttonLayout, 1, 2);
        root.add(changeModBtn, 2, 2);
        Scene scene = new Scene(root, 300, 150, Color.rgb(240, 217, 164));  // создание Scene
        return scene;
    }

    public Button authorization() {
        Button loginButton = new Button("Login");
        // Set the action for the login button
        loginButton.setOnAction(event -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            ResponseMessage responseMessage = clientManager.startNewAction("login " + username + " " + password);
            if (responseMessage.getResponseData().equals(true)) {
                app.setTableScene();
            } else {
                app.customizedAlert((String) responseMessage.getResponseData()).showAndWait();
            }
        });
        return loginButton;
    }

    public Button registration() {
        Button regButton = new Button("Registration");

        // Set the action for the login button
        regButton.setOnAction(event -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            ResponseMessage responseMessage = clientManager.startNewAction("registration " + username + " " + password);
            if (responseMessage.getResponseData().equals(true)) {
                mode = "L";
                common();
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, (String) responseMessage.getResponseData());
                alert.showAndWait();
            }
        });
        return regButton;
    }
}