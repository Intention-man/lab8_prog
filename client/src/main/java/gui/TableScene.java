package gui;

import auxiliary_classes.ResponseMessage;
import com.sun.javafx.collections.ObservableListWrapper;
import functional_classes.ClientManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class TableScene {
    FXApplication app;
    FlowPane root;
    ClientManager clientManager;
    TableView table;
    Label lbl;
    List<String> columnNamesList;
    ObservableList<ObservableList<String>> data;


    public TableScene(FXApplication app, ClientManager clientManager) {
        this.app = app;
        this.clientManager = clientManager;
        table = new TableView<>();
        table.setPrefWidth(750);
        table.setPrefHeight(200);
        lbl = new Label();
        data = FXCollections.observableArrayList();
        columnNamesList = new ArrayList<>();
    }

    public Scene openScene() {
        System.out.println("New render");
        clientManager.startNewAction("login 88 88");
        root = new FlowPane(Orientation.VERTICAL, 30.0, 30.0, app.navigateButtonList(), table);
        ResponseMessage response = clientManager.commandsWithoutParam("getAllMoviesRS");
        ResultSet resultSet = (ResultSet) response.getResponseData();
        try {
            for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {
                //We are using non property style for making dynamic table
                final int j = i;
                TableColumn column = new TableColumn(resultSet.getMetaData().getColumnName(i + 1));
                column.setCellValueFactory((Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>) param -> new SimpleStringProperty(param.getValue().get(j) != null ? param.getValue().get(j).toString() : null));
                table.getColumns().addAll(column);
                columnNamesList.add(column.getText());
            }
            changeData("", "i");
            //FINALLY ADDED TO TableView
            FXCollections.sort(data, Comparator.comparing(list -> Integer.parseInt(list.get(0))));
//            data = (ObservableList<ObservableList<String>>) data.stream().sorted(Comparator.comparing(list -> Integer.parseInt(list.get(0))));
            table.setItems(data);
            filterRecords();
            // on-table-row-click action
            table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    // handle click on row with data 'newSelection'
                    System.out.println("Clicked on row with data: " + newSelection);
                    System.out.println(newSelection.getClass());
                    ObservableList list = FXCollections.observableList((ObservableListWrapper) newSelection);
                    int id = Integer.parseInt(list.get(0).toString());
                    String creator = list.get(list.size() - 1).toString();
                    app.setMovieInfoScene(id, creator);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error on Building Data");
        }

        Scene scene = new Scene(root, 300, 150, Color.rgb(240, 217, 164));  // создание Scene
        return scene;
    }

    public void filterRecords() {
        FlowPane filterZone = new FlowPane();
        Label label = new Label("Введите строку с запросом для фильтрации в формате");
        TextField columnNameField = new TextField();
        TextField conditionField = new TextField();
        Button btnFilterByNumberCompare = new Button("Отфильтровать по числовому условию");
        Button btnFilterBySubstring = new Button("Отфильтровать по подстроке");
        Button resetButton = new Button("БСролить параметры фильрации");
        btnFilterByNumberCompare.setOnAction(e -> {
            if (columnNamesList.contains(columnNameField.getText().trim())) {
                String requestString = columnNameField.getText().trim() + " " + conditionField.getText().trim();
                changeData(requestString, "d");
                table.setItems(data);
            } else {
                app.customizedAlert("Столбца с таким названием не существует!").showAndWait();
            }
        });
        btnFilterBySubstring.setOnAction(e -> {
            if (columnNamesList.contains(columnNameField.getText().trim())) {
                String requestString = columnNameField.getText().trim() + " " + conditionField.getText().trim();
                changeData(requestString, "s");
                table.setItems(data);
            } else {
                app.customizedAlert("Столбца с таким названием не существует!").showAndWait();
            }
        });
        resetButton.setOnAction(e -> {
            changeData("", "i");
            table.setItems(data);
        });

        filterZone.getChildren().addAll(label, columnNameField, conditionField, btnFilterByNumberCompare, btnFilterBySubstring, resetButton);
        root.getChildren().add(filterZone);

//        data = table.getItems();
//        textfield.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
//            if (oldValue != null && (newValue.length() < oldValue.length())) {
//                table.setItems(data);
//            }
//            String value = newValue.toLowerCase();
//            ObservableList subentries = FXCollections.observableArrayList().sorted();
//
//            long count = table.getColumns().stream().count();
//            for (int i = 0; i < table.getItems().size(); i++) {
//                for (int j = 0; j < count; j++) {
//                    String entry = "" + table.getColumns().get(j).getCellData(i);
//                    if (entry.toLowerCase().contains(value)) {
//                        subentries.add(table.getItems().get(i));
//                        break;
//                    }
//                }
//            }
//            table.setItems(subentries);
    }

    public void changeData(String requestString, String typeOfRequest) {
        try {
            ResultSet resultSet;
            System.out.println(typeOfRequest);
            if (typeOfRequest.equals("d")) {
                resultSet = (ResultSet) clientManager.commandsWithParam("getDigitFilteredMoviesRS", requestString).getResponseData();
            } else if (typeOfRequest.equals("s")) {
                resultSet = (ResultSet) clientManager.commandsWithParam("getSubstringFilteredMoviesRS", requestString).getResponseData();
            } else {
                resultSet = (ResultSet) clientManager.commandsWithoutParam("getAllMoviesRS").getResponseData();
            }
            ObservableList<ObservableList<String>> newData = FXCollections.observableArrayList();
            while (resultSet.next()) {
                //Iterate Row
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                    //Iterate Column
                    row.add(resultSet.getString(i));
                }
                System.out.println("Row [1] added " + row);
                newData.add(row);
            }
            data = FXCollections.observableArrayList(newData);
        } catch (ClassCastException err) {
            app.customizedAlert("Введены некорректные данные для фильтрации!").showAndWait();
        } catch (Exception err) {
            err.printStackTrace();
            app.customizedAlert("Разработчик приложение недоработал! Что-то непредвиденное произошло(").showAndWait();
        }
    }

    // graphic objects

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
}