package gui;

import functional_classes.ClientManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class MovieInfoScene {
    FXApplication app;
    ClientManager clientManager;
    int movieId;
    HashMap<Integer, Object> map = new HashMap<>();
    ResultSet resultSet;
    String creator;

    public MovieInfoScene(FXApplication app, ClientManager clientManager, int movieId, String creator) {
        this.app = app;
        this.clientManager = clientManager;
        this.movieId = movieId;
        this.creator = creator;
    }

    public Scene openScene() throws SQLException {
        FlowPane root = new FlowPane((Node) app.navigateButtonList());
        root.setHgap(10);
        root.setVgap(10);
        root.setPrefWidth(1000);
        root.setPrefHeight(500);
        root.setPadding(new Insets(10));
        resultSet = (ResultSet) clientManager.commandsWithParam("getMovieRSById", movieId).getResponseData();
        System.out.println(resultSet.getMetaData());

        try {
            List<String> keysList = new ArrayList<>();
            for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                keysList.add(resultSet.getMetaData().getColumnName(i));
            }

            while (resultSet.next()) {
                //Iterate Row
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {
                    //Iterate Column
                    row.add(resultSet.getString(i + 1));
                    GridPane gridPane = new GridPane();
                    gridPane.add(new Label(keysList.get(i)), 0, 0);
                    TextField textField = retTextField(i, (creator.equals(clientManager.getLogin()) && !Arrays.asList("id", "creation_date", "creator").contains(keysList.get(i))));
                    gridPane.add(textField, 1, 0);
                    root.getChildren().add(gridPane);
                }
                map.put(map.size(), movieId);
                System.out.println("Row [1] added " + row);
            }

            if (Objects.equals(creator, clientManager.getLogin())){
                Button saveButton = retSaveButton();
                root.getChildren().add(saveButton);
                Button removeButton = retRemoveButton();
                root.getChildren().add(removeButton);
            }
            Button exitButton = retExitButton();
            root.getChildren().add(exitButton);
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        Scene scene = new Scene(root, 300, 150, Color.rgb(240, 217, 164));  // создание Scene
        return scene;
    }

    public TextField retTextField(int i, boolean isEditable) throws SQLException {
        TextField textField = new TextField(resultSet.getString(i + 1));

        if (isEditable) {
            int finalI = map.size();
            map.put(map.size(), resultSet.getString(i + 1));
            textField.textProperty().addListener((observable, oldValue, newValue) -> {
                System.out.println("textfield changed from " + oldValue + " to " + newValue);
                map.put(finalI, newValue);
            });
        }
        else {
            textField.setEditable(false);
        }
        return textField;
    }

    public Button retSaveButton(){
        Button saveButton = new Button("Сохранить изменения");
        saveButton.setOnAction(e -> {
            map.forEach((key, value) -> System.out.println(key + " " + value));
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText((String) clientManager.commandsWithParam("update", map).getResponseData());
            alert.setContentText(clientManager.getLogin());
            alert.showAndWait();
        });
        return saveButton;
    }

    public Button retRemoveButton(){
        Button removeButton = new Button("Удалить фильм");
        removeButton.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText((String) clientManager.commandsWithParam("remove", movieId).getResponseData());
            alert.setContentText(clientManager.getLogin());
            alert.showAndWait();
        });
        return removeButton;
    }

    public Button retExitButton(){
        Button exitButton = new Button("Выйти на главную страницу без сохранения изменений");
        exitButton.setOnAction(e -> {
            app.setMainScene();
        });
        return exitButton;
    }

}