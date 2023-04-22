package gui;

import auxiliary_classes.ResponseMessage;
import functional_classes.ClientManager;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

import java.sql.ResultSet;
import java.util.*;

public class StartScene {
    FXApplication app;
    ClientManager clientManager;
    Label usernameLabel;
    Label passwordLabel;
    TextField usernameField;
    PasswordField passwordField;
    String mode;
    ResponseMessage response = null;
    ResourceBundle bundle;

    public StartScene(FXApplication app, ClientManager clientManager) {
        this.app = app;
        this.clientManager = clientManager;
        mode = "L";
    }

    public Scene openScene() {
        GridPane root = new GridPane();
        bundle = app.getBundle();
        usernameLabel = new Label(bundle.getString("Username"));
        usernameField = new TextField();
        passwordLabel = new Label(bundle.getString("Password"));
        passwordField = new PasswordField();
        Button changeModBtn = new Button((Objects.equals(mode, "L") ? bundle.getString("toRegister") : bundle.getString("toLogin")));
        changeModBtn.setOnAction(e -> {
            mode = (Objects.equals(mode, "L") ? "R" : "L");
            this.openScene();
        });

        root.setHgap(10);
        root.setVgap(10);
        root.setPadding(new Insets(10));
        root.add(usernameLabel, 0, 0);
        root.add(usernameField, 1, 0);
        root.add(passwordLabel, 0, 1);
        root.add(passwordField, 1, 1);

        HBox buttonLayout = new HBox(10);
        System.out.println(mode);
        buttonLayout.getChildren().add(Objects.equals(mode, "L") ? authorization() : registration());

        root.add(buttonLayout, 1, 2);
        root.add(changeModBtn, 2, 2);
        root.add(retLangButtons(), 0, 3);
        Scene scene = new Scene(root, 300, 150, Color.rgb(240, 217, 164));  // создание Scene
        return scene;
    }

    public Button authorization() {
        Button loginButton = new Button(bundle.getString("Login"));
        // Set the action for the login button
        loginButton.setOnAction(event -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            clientManager.commandsWithParam("login", (username + " " + password));
            updateResponseData();
            if (response.getResponseData().equals(true)) {
                app.setTableScene();
            } else {
                app.customizedAlert((String) response.getResponseData()).showAndWait();
            }
        });
        return loginButton;
    }

    public Button registration() {
        Button regButton = new Button(bundle.getString("Registration"));
        System.out.println("Registration");
        // Set the action for the login button
        regButton.setOnAction(event -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            clientManager.commandsWithParam("registration", (username + " " + password));
            updateResponseData();

            if (response.getResponseData().equals(true)) {
                mode = "L";
                openScene();
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, (String) response.getResponseData());
                alert.showAndWait();
            }
        });
        return regButton;
    }

    public HBox retLangButtons(){
        ToggleGroup group = new ToggleGroup();
        HBox langButtons = new HBox(10);
        HashMap<String, String> langMap = new HashMap<>();
        langMap.put("English (NZE)", "Gui_en_NZE");
        langMap.put("Русский", "Gui_ru_RU");
        for (String key : langMap.keySet()) {
            RadioButton rBtn = new RadioButton(key);
            rBtn.setToggleGroup(group);
            langButtons.getChildren().add(rBtn);
            rBtn.setOnAction(event -> {
                bundle = ResourceBundle.getBundle(langMap.get(key));
                app.setBundle(bundle);
                app.renderByDataUpdate();
            });
        }
        return langButtons;
    }

    public void updateResponseData(){
        response = null;
        while (response == null || !app.clientSerializer.isReadyToReturnMessage()){
            response = app.clientSerializer.getNewResponse();
        }
        System.out.println("got data");
        app.clientSerializer.setReadyToReturnMessage(false);
    }
}