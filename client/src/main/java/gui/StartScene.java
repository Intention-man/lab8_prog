package gui;

import auxiliary_classes.LocationStore;
import auxiliary_classes.ResponseMessage;
import functional_classes.ClientManager;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
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
    static List<LocationStore> locationStores = new ArrayList<>();
    Locale locale;
    FlowPane localeZone;

    static {
        locationStores.add(new LocationStore("Русский", "ru-RU", "Europe/Moscow", "Gui_ru_RU"));
        locationStores.add(new LocationStore("English (NZE)", "en-NZE", "Etc/GMT+12", "Gui_en_NZE"));
        locationStores.add(new LocationStore("Hrvatski", "hr-HR", "Europe/Zagreb", "Gui_HR"));
        locationStores.add(new LocationStore("Netherlands", "nl_NL", "Europe/Amsterdam", "Gui_NL"));
    }

    public StartScene(FXApplication app, ClientManager clientManager, String loginMode) {
        this.app = app;
        this.clientManager = clientManager;
        mode = loginMode;
    }

    public Scene openScene() {
        GridPane root = new GridPane();
        root.getChildren().removeAll();
        bundle = app.getBundle();
        usernameLabel = new Label(bundle.getString("Username"));
        usernameField = new TextField();
        passwordLabel = new Label(bundle.getString("Password"));
        passwordField = new PasswordField();
        Button changeModBtn = new Button((Objects.equals(mode, "L") ? bundle.getString("toRegister") : bundle.getString("toLogin")));

        root.setHgap(10);
        root.setVgap(10);
        root.setPadding(new Insets(10));
        root.add(usernameLabel, 0, 0);
        root.add(usernameField, 1, 0);
        root.add(passwordLabel, 0, 1);
        root.add(passwordField, 1, 1);

        HBox buttonLayout = new HBox(10);
        if (Objects.equals(mode, "R")){
            buttonLayout.getChildren().add(registration());
        } else {
            buttonLayout.getChildren().add(authorization());
        }
        root.add(buttonLayout, 1, 2);
        root.add(changeModBtn, 2, 2);
        root.add(retLangButtons(), 0, 3);

        changeModBtn.setOnAction(e -> {
            mode = (Objects.equals(mode, "L") ? "R" : "L");
            app.setStartScene(mode);
//            app.render();
        });

        Button signOutBtn = new Button(bundle.getString("signOut"));
        signOutBtn.setOnAction(e -> {
            clientManager.setLogin(null);
            clientManager.setPassword(null);
            app.customizedAlert(bundle.getString("success")).showAndWait();
        });
        root.add(signOutBtn, 3, 0);

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
                app.customizedAlert(bundle.getString("loginException")).showAndWait();
            }
        });
        return loginButton;
    }

    public Button registration()  {
        Button regButton = new Button(bundle.getString("Registration"));

        // Set the action for the login button
        regButton.setOnAction(event -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            clientManager.commandsWithParam("registration", (username + " " + password));
            updateResponseData();

            if (response.getResponseData().equals(true)) {
                mode = "L";
                app.setStartScene("L");
            } else {
               app.customizedAlert(response.getResponseData().toString()).showAndWait();
            }
        });
        return regButton;
    }

    public HBox retLangButtons() {
        ToggleGroup group = new ToggleGroup();
        HBox langButtons = new HBox(10);
        for (LocationStore locationStore : locationStores) {
            RadioButton rBtn = new RadioButton(locationStore.getLangName());
            rBtn.setToggleGroup(group);
            langButtons.getChildren().add(rBtn);
            rBtn.setOnAction(event -> {
                bundle = ResourceBundle.getBundle(locationStore.getResourseFileName());
                app.setBundle(bundle);
                app.renderByDataUpdate();
                app.setLangShortTag(List.of(locationStore.getLangTag().split("-")).get(0));
                retLocaleDataZone(locationStore);
            });

        }
        return langButtons;
    }

    public void updateResponseData() {
        response = null;
        while (response == null || !app.clientSerializer.isReadyToReturnMessage()) {
            response = app.clientSerializer.getNewResponse();
        }
        app.clientSerializer.setReadyToReturnMessage(false);
    }

    public void retLocaleDataZone(LocationStore locationStore) {
        locale = Locale.forLanguageTag(locationStore.getLangTag());
        LocalDateTime nowTime = LocalDateTime.now(ZoneId.of(locationStore.getZoneId()));
        ZonedDateTime zoned = ZonedDateTime.now();
        DateTimeFormatter pattern = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(locale);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        app.primaryStage.setTitle(bundle.getString("MovieStore"));

        Label dataLabel = new Label(zoned.format(pattern));
        Label timeLabel = new Label(nowTime.format(formatter));
        localeZone = new FlowPane(dataLabel, timeLabel);
        app.setLocaleZone(localeZone);
        app.setLocationStore(locationStore);
    }
}