package gui;

import functional_classes.ClientManager;
import functional_classes.ClientReader;
import functional_classes.ClientSerializer;
import functional_classes.Writer;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Objects;

import javafx.fxml.FXMLLoader;

public class FXApplication extends Application{
    Stage primaryStage;
    static ClientManager clientManager;
    Scene currentScene;

    public static void main(String[] args) {
        ClientReader reader = new ClientReader();
        Writer writer = new Writer();
        int port;
        ClientSerializer clientSerializer;
        port = 5000;
        try {
            clientSerializer = new ClientSerializer(port);
            clientManager = new ClientManager(clientSerializer, reader, writer);
            reader.setClientManager(clientManager);
        } catch (SocketException | UnknownHostException e) {
            throw new RuntimeException(e);
        }
        Application.launch();
    }

    @Override
    public void start(Stage primaryStage) throws SQLException, IOException {
        this.primaryStage = primaryStage;
        currentScene = openCommandsScene();
        primaryStage.setTitle("Movie Store");
        primaryStage.setWidth(1200);
        primaryStage.setHeight(600);
        InputStream iconStream = getClass().getResourceAsStream("/images/river.jpg");
        assert iconStream != null;
        Image image = new Image(iconStream);
        primaryStage.getIcons().add(image);
        render();
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

    public Scene openMainScene() throws SQLException {
        MainScene mainScene = new MainScene(this, clientManager);
        currentScene = mainScene.openScene();
        render();
        return currentScene;
    }

    public Scene openTableScene() throws SQLException, IOException {
        TableScene tableScene = new TableScene(this, clientManager);
        currentScene = tableScene.openScene();
        render();
        return currentScene;
    }

    public Scene openMovieInfoScene(int id, String creator) throws SQLException {
        MovieInfoScene movieInfoScene = new MovieInfoScene(this, clientManager, id, creator);
        currentScene = movieInfoScene.openScene();
        render();
        return currentScene;
    }

    public Scene openCommandsScene() throws SQLException {
        CommandsScene commandsScene = new CommandsScene(this, clientManager);
        currentScene = commandsScene.openScene();
        render();
        return currentScene;
    }


    public void changeSceneFromFXML(String fxml) throws IOException {
        Parent pane = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxml)));
        primaryStage.getScene().setRoot(pane);
    }
}