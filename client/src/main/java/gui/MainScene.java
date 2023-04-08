package gui;

import auxiliary_classes.ResponseMessage;
import functional_classes.ClientManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class MainScene {
    FXApplication app;
    ClientManager clientManager;

    public MainScene(FXApplication app, ClientManager clientManager) {
        this.app = app;
        this.clientManager = clientManager;
    }

    public Scene openScene() throws SQLException {
        clientManager.startNewAction("login 88 88");
        ResponseMessage response = clientManager.commandsWithoutParam("getAllMoviesRS");
        ResultSet resultSet = (ResultSet) response.getResponseData();

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPrefWidth(1000);
        gridPane.setPrefHeight(500);
        gridPane.setPadding(new Insets(10));

        while (resultSet.next()) {
            ObservableList<String> row = FXCollections.observableArrayList();
            for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                //Iterate Column
                row.add(resultSet.getString(i));
            }
            System.out.println(gridPane.getPrefWidth() + " " + gridPane.getPrefHeight());
            Circle circle = new Circle();
            double posX = gridPane.getPrefWidth() * (0.5 + (Double.parseDouble(row.get(2)) / Math.pow(2, 32))) / 10;
            double posY = gridPane.getPrefHeight() * (0.5 + (Double.parseDouble(row.get(3)) / Math.pow(2, 32))) / 10;
            circle.setCenterX(posX);
            circle.setCenterY(posY);
            circle.setRadius(20 + 10 * Long.parseLong(row.get(6)) / Math.pow(2, 58));
            circle.setFill(Color.rgb(10, 225, 10));
            circle.setStroke(Color.BLACK);

            Label label = new Label(row.get(1));
            StackPane stackPane = new StackPane(circle, label);
            StackPane.setAlignment(label, Pos.CENTER);
            stackPane.setOnMouseClicked(event -> {
                try {
                    app.openMovieInfoScene(Integer.parseInt(row.get(0)), row.get(row.size() - 1));
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });

            gridPane.add(stackPane, (int) posX, (int) posY);
            System.out.println(Arrays.asList((int) posX, (int) posY));
        }

        return new Scene(gridPane, 300, 150, Color.rgb(240, 217, 164));
    }
}
