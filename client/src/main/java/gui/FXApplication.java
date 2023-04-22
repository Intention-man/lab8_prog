package gui;

import functional_classes.ClientManager;
import functional_classes.ClientReader;
import functional_classes.ClientSerializer;
import functional_classes.Writer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.InputStream;
import java.net.BindException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.*;

public class FXApplication extends Application implements PropertyChangeListener {
    Stage primaryStage;
    static ClientManager clientManager;
    Scene currentScene;
    String currentSceneName;
    ClientReader reader = new ClientReader();
    Writer writer = new Writer();
    int port;
    ClientSerializer clientSerializer;
    int lastMovieId;
    String lastCreator;
    ResourceBundle bundle;


    public static void main(String[] args) {
        Application.launch();
    }

    @Override
    public void start(Stage primaryStage) {
        bundle = ResourceBundle.getBundle("Gui_ru_RU");
        try {
            port = 5000;
            while (true) {
                try {
                    port += 1;
                    clientSerializer = new ClientSerializer(port);
                    break;
                } catch (BindException ignored) {
                }
            }
            System.out.println(port);

            clientSerializer.setApp(this);
            clientManager = new ClientManager(clientSerializer, reader, writer);
            reader.setClientManager(clientManager);
        } catch (SocketException | UnknownHostException e) {
            throw new RuntimeException(e);
        }
        this.primaryStage = primaryStage;
        primaryStage.setX(20);
        primaryStage.setY(20);
        primaryStage.setWidth(1200);
        primaryStage.setHeight(600);
        currentScene = setStartScene();
        primaryStage.setTitle(bundle.getString("MovieStore"));
        InputStream iconStream = getClass().getResourceAsStream("/images/river.jpg");
        assert iconStream != null;
        Image image = new Image(iconStream);
        primaryStage.getIcons().add(image);
        render();

        // Subscribe to data update
        clientSerializer.addPropertyChangeListener(this);

        Thread t2 = new Thread(() -> {
            while (true) {
                String answer = clientSerializer.getAndReturnMessageLoop();
                if (Objects.equals(answer, "U")) {
                    System.out.println("inside task");
                }
            }
        });
        t2.start();
    }

    public void render() {
        System.out.println(1 + " " + currentScene);
        primaryStage.setScene(currentScene);
        primaryStage.show();
        System.out.println("render");
    }

    public void renderByDataUpdate() {
        switch (currentSceneName) {
            case ("StartScene") -> setStartScene();
            case ("MoviesDisplayScene") -> setMovieDisplayScene();
            case ("TableScene") -> setTableScene();
            case ("MovieInfoScene") -> setMovieInfoScene(lastMovieId, lastCreator);
            case ("CommandsScene") -> setCommandsScene();
        }
    }

    public FlowPane navigateButtonList() {
        Button btn1 = new Button(bundle.getString("StartScene"));
        btn1.setOnAction(e -> setStartScene());
        Button btn2 = new Button(bundle.getString("TableScene"));
        btn2.setOnAction(e -> setTableScene());
        Button btn3 = new Button(bundle.getString("CommandsScene"));
        btn3.setOnAction(e -> setCommandsScene());
        Button btn4 = new Button(bundle.getString("MoviesDisplayScene"));
        btn4.setOnAction(e -> setMovieDisplayScene());
        Button btn5 = retUserProfileButton();

        FlowPane flowPane = new FlowPane(Orientation.HORIZONTAL, 30, 30);
        flowPane.setLayoutX(20);
        flowPane.setLayoutY(20);
        flowPane.setPrefWidth(primaryStage.getWidth());
        List<Button> buttonList = new ArrayList<>(Arrays.asList(btn1, btn2, btn3, btn5));
        buttonList.forEach(button -> flowPane.getChildren().add(button));
        return flowPane;
    }

    public Scene setStartScene() {
        StartScene startScene = new StartScene(this, clientManager);
        currentScene = startScene.openScene();
        currentSceneName = "StartScene";
        render();
        return currentScene;
    }

    public Scene setMovieDisplayScene() {
        try {
            MoviesDisplayScene moviesDisplayScene = new MoviesDisplayScene(this, clientManager);
            currentScene = moviesDisplayScene.openScene();
            currentSceneName = "MoviesDisplayScene";
            render();
            return currentScene;
        } catch (SQLException err) {
            customizedAlert(err.getMessage()).showAndWait();
        }
        return null;
    }

    public Scene setTableScene() {
        TableScene tableScene = new TableScene(this, clientManager);
        currentScene = tableScene.openScene();
        currentSceneName = "TableScene";
        render();
        return currentScene;
    }

    public Scene setMovieInfoScene(int id, String creator) {  
        try {
            lastMovieId = id;
            lastCreator = creator;
            MovieInfoScene movieInfoScene = new MovieInfoScene(this, clientManager, id, creator);
            currentScene = movieInfoScene.openScene();
            currentSceneName = "MovieInfoScene";
            render();
            return currentScene;
        } catch (SQLException err) {
            customizedAlert(err.getMessage()).showAndWait();
        }
        return null;
    }

    public Scene setCommandsScene() {
        CommandsScene commandsScene = new CommandsScene(this, clientManager);
        currentScene = commandsScene.openScene();
        currentSceneName = "CommandsScene";
        render();
        return currentScene;
    }

    public void setBundle(ResourceBundle bundle) {
        this.bundle = bundle;
    }

    public Button retUserProfileButton() {
        Button userButton = new Button();
        Image userImage = new Image("/images/user_icon.png");
        ImageView userImageView = new ImageView(userImage);
        userImageView.setFitHeight(50);
        userImageView.setFitWidth(50);
        userButton.setGraphic(userImageView);
        userButton.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(bundle.getString("Username"));
            alert.setContentText(clientManager.getLogin());
            alert.showAndWait();
        });
        return userButton;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public ResourceBundle getBundle() {
        return bundle;
    }

    public Alert customizedAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("");
        alert.setContentText(message);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        return alert;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
//        this.render();
        Platform.runLater(this::renderByDataUpdate);
    }
}