package gui;

import auxiliary_classes.ResponseMessage;
import functional_classes.ClientManager;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.util.Duration;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MoviesDisplayScene {
    FXApplication app;
    FlowPane root;
    ClientManager clientManager;

    public MoviesDisplayScene(FXApplication app, ClientManager clientManager) {
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
        gridPane.setPadding(new Insets(1));


        String lastCreator = "";
        List<Color> colorList = new ArrayList<>();
//        int columnCount = gridPane.getColumnCount();
//        int rowCount =  gridPane.getRowCount();
//        System.out.println(columnCount + " " + rowCount);
        while (resultSet.next()) {
            ObservableList<String> row = FXCollections.observableArrayList();
            for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                //Iterate Column
                row.add(resultSet.getString(i));
            }
            if (!Objects.equals(row.get(row.size() - 1), lastCreator)) {
                lastCreator = row.get(row.size() - 1);
                Color newColor;
                do {
                    newColor = Color.rgb((int) Math.round(30 + (Math.random() * 225)), (int) Math.round(30 + (Math.random() * 225)), (int) Math.round(30 + (Math.random() * 225)));
                } while (colorList.contains(newColor));
                colorList.add(newColor);
            }
            System.out.println(gridPane.getPrefWidth() + " " + gridPane.getPrefHeight());
            root = new FlowPane(Orientation.VERTICAL, 30.0, 30.0, app.navigateButtonList());
            Polygon star = new Polygon();
//            center = 100, 44
            double k = Long.parseLong(row.get(6)) / Math.pow(2, 64);
            double r = 0.7 + k * 8;
            System.out.println(r);
            star.getPoints().addAll(
                    100.0, 44 - 44 * r,
                    100 + 17 * r, 44.0 - 4 * r,
                    100 + 60*r, 44.0 - 4*r,
                    100 + 27*r, 44 + 26*r,
                    100 + 39*r, 44 + 78*r,
                    100.0, 44 + 45*r,
                    100 - 39*r, 44 + 78*r,
                    100 - 27*r, 44 + 26*r,
                    100 - 60*r, 44.0 - 4 * r,
                    100 - 17 * r, 44.0 - 4 * r
            );
            double posX = Math.max(gridPane.getPrefWidth() / 10 * (0.5 + (Double.parseDouble(row.get(2)) / Math.pow(2, 33))) - 20*k, 0);
            double posY = Math.max(gridPane.getPrefHeight() / 10 * (0.5 + (Double.parseDouble(row.get(3)) / Math.pow(2, 33))) - 20*k, 0);
            star.setFill(colorList.get(colorList.size() - 1));
            star.setStroke(Color.BLACK);
//            star.setStrokeWidth(2);
//            root.getChildren().add(star);

            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.ZERO, event -> star.setRotate(star.getRotate() + 1)),
                    new KeyFrame(Duration.millis(10))
            );
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.play();
//            Circle circle = new Circle();
//            circle.setRadius(20 + 10 * Long.parseLong(row.get(6)) / Math.pow(2, 58));
//            double posX = Math.max(gridPane.getPrefWidth() * (0.5 + (Double.parseDouble(row.get(2)) / Math.pow(2, 33))) / 10 - circle.getRadius(), 0);
//            double posY = Math.max(gridPane.getPrefHeight() * (0.5 + (Double.parseDouble(row.get(3)) / Math.pow(2, 33))) / 10 - circle.getRadius(), 0);
//            circle.setCenterX(posX);
//            circle.setCenterY(posY);
//            circle.setFill(colorList.get(colorList.size() - 1));
//            circle.setStroke(Color.BLACK);

            Label label = new Label(row.get(1));
            StackPane stackPane = new StackPane(star, label);
            StackPane.setAlignment(label, Pos.CENTER);
            stackPane.setOnMouseClicked(event -> {
                app.setMovieInfoScene(Integer.parseInt(row.get(0)), row.get(row.size() - 1));
            });

            gridPane.add(stackPane, (int) posX, (int) posY);
            System.out.println(Arrays.asList((int) posX, (int) posY));
        }

//        for (int i = 0; i < 3; i++) {
//            Polygon star = new Polygon();
//            star.getPoints().addAll(
//                    100.0, 0.0,
//                    117.0, 40.0,
//                    160.0, 40.0,
//                    127.0, 70.0,
//                    139.0, 112.0,
//                    100.0, 89.0,
//                    61.0, 112.0,
//                    73.0, 70.0,
//                    40.0, 40.0,
//                    83.0, 40.0
//            );
//            star.setFill(Color.YELLOW);
//            star.setStroke(Color.BLACK);
//            star.setStrokeWidth(2);
//
//            root.getChildren().add(star);
//
//            Timeline timeline = new Timeline(
//                    new KeyFrame(Duration.ZERO, event -> {
//                        star.setRotate(star.getRotate() + 1);
//                    }),
//                    new KeyFrame(Duration.millis(10))
//            );
//            timeline.setCycleCount(Animation.INDEFINITE);
//            timeline.play();
//        }
        root.getChildren().add(gridPane);
        return new Scene(root, 300, 150, Color.rgb(240, 217, 164));
    }
}
