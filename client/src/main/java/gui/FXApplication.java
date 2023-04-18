package gui;

import functional_classes.ClientManager;
import functional_classes.ClientReader;
import functional_classes.ClientSerializer;
import functional_classes.Writer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.io.InputStream;
import java.net.BindException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.*;

public class FXApplication extends Application {
    Stage primaryStage;
    static ClientManager clientManager;
    Scene currentScene;
    ClientReader reader = new ClientReader();
    Writer writer = new Writer();
    int port;
    ClientSerializer clientSerializer;


    public static void main(String[] args) {
        Application.launch();
    }

    @Override
    public void start(Stage primaryStage) {
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
        currentScene = setCommandsScene();
        primaryStage.setTitle("Movie Store");
        InputStream iconStream = getClass().getResourceAsStream("/images/river.jpg");
        assert iconStream != null;
        Image image = new Image(iconStream);
        primaryStage.getIcons().add(image);
        render();

        Thread t2 = new Thread(() -> {
            while (true) {
                String answer = clientSerializer.getAndReturnMessageLoop();
                if (Objects.equals(answer, "U")) {
                    System.out.println("inside task");
                    render();
                }
            }
        });
        t2.start();
    }

    public void render() {
        System.out.println(1 + " " + currentScene);
        primaryStage.setScene(currentScene);
        primaryStage.show();
    }

    public Scene openStartScene() {
        StartScene startScene = new StartScene(this, clientManager);
        currentScene = startScene.common();
        render();
        return currentScene;
    }

    public FlowPane navigateButtonList() {
        Button btn1 = new Button("Страница оттображения всех фильмов на доске");
        btn1.setOnAction(e -> setMainScene());
        Button btn2 = new Button("Страница оттображения всех фильмов в таблице");
        btn2.setOnAction(e -> setTableScene());
        Button btn3 = new Button("Страница с командами");
        btn3.setOnAction(e -> setCommandsScene());
        Button btn4 = retUserProfileButton();

        FlowPane flowPane = new FlowPane(Orientation.HORIZONTAL, 30, 30);
        flowPane.setLayoutX(20);
        flowPane.setLayoutY(20);
        flowPane.setPrefWidth(primaryStage.getWidth());
        List<Button> buttonList = new ArrayList<>(Arrays.asList(btn1, btn2, btn3, btn4));
        buttonList.forEach(button -> flowPane.getChildren().add(button));
        return flowPane;
    }

    public Scene setMainScene() {
        try {
            MoviesDisplayScene moviesDisplayScene = new MoviesDisplayScene(this, clientManager);
            currentScene = moviesDisplayScene.openScene();
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
        render();
        return currentScene;
    }

    public Scene setMovieInfoScene(int id, String creator) {
        try {
            MovieInfoScene movieInfoScene = new MovieInfoScene(this, clientManager, id, creator);
            currentScene = movieInfoScene.openScene();
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
        render();
        return currentScene;
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
            alert.setHeaderText("Пользователь:");
            alert.setContentText(clientManager.getLogin());
            alert.showAndWait();
        });
        return userButton;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public Alert customizedAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("");
        alert.setContentText(message);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        return alert;
    }

    public ClientSerializer getClientSerializer() {
        return clientSerializer;
    }

    public void changed(ObservableValue<? extends String> prop,
                        String oldValue,
                        String newValue) {
        customizedAlert(newValue).showAndWait();
    }

}