package gui;

import auxiliary_classes.ResponseMessage;
import com.sun.javafx.collections.ObservableListWrapper;
import functional_classes.ClientManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
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
import java.util.ResourceBundle;


public class TableScene {
    FXApplication app;
    FlowPane root;
    ClientManager clientManager;
    TableView table;
    Label lbl;
    List<String> columnNamesList;
    ObservableList<ObservableList<String>> data;
    ResponseMessage response = null;
    ResourceBundle bundle;


    public TableScene(FXApplication app, ClientManager clientManager) {
        this.app = app;
        this.clientManager = clientManager;
        table = new TableView<>();
        table.setPrefWidth(750);
        table.setPrefHeight(200);
        lbl = new Label();
        data = FXCollections.observableArrayList();
        columnNamesList = new ArrayList<>();
        bundle = app.getBundle();
    }

    public Scene openScene() {
        root = new FlowPane(Orientation.VERTICAL, 30.0, 30.0, app.navigateButtonList(), table);
        System.out.println("New render");
//        clientManager.startNewAction("login 88 88");
        response = null;
        clientManager.commandsWithoutParam("getAllMoviesRS");
        while (response == null || !app.clientSerializer.isReadyToReturnMessage()){
            response = app.clientSerializer.getNewResponse();
        }
        app.clientSerializer.setReadyToReturnMessage(false);
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
            app.customizedAlert(bundle.getString("tableBuildingErr"));
        }

        Scene scene = new Scene(root, 300, 150, Color.rgb(240, 217, 164));  // создание Scene
        return scene;
    }

    public void filterRecords() {
        FlowPane filterZone = new FlowPane();
        Label label = new Label(bundle.getString("filterStringInput"));
        TextField columnNameField = new TextField();
        TextField conditionField = new TextField();
        Button btnFilterByNumberCompare = new Button(bundle.getString("filterByNumberValue"));
        Button btnFilterBySubstring = new Button(bundle.getString("filterBySubstring"));
        Button resetButton = new Button(bundle.getString("resetFilteringParams"));
        btnFilterByNumberCompare.setOnAction(e -> {
            if (columnNamesList.contains(columnNameField.getText().trim())) {
                String requestString = columnNameField.getText().trim() + " " + conditionField.getText().trim();
                changeData(requestString, "d");
                table.setItems(data);
            } else {
                app.customizedAlert(bundle.getString("thereIsNotColumn")).showAndWait();
            }
        });
        btnFilterBySubstring.setOnAction(e -> {
            if (columnNamesList.contains(columnNameField.getText().trim())) {
                String requestString = columnNameField.getText().trim() + " " + conditionField.getText().trim();
                changeData(requestString, "s");
                table.setItems(data);
            } else {
                app.customizedAlert(bundle.getString("thereIsNotColumn")).showAndWait();
            }
        });
        resetButton.setOnAction(e -> {
            changeData("", "i");
            table.setItems(data);
        });

        filterZone.getChildren().addAll(label, columnNameField, conditionField, btnFilterByNumberCompare, btnFilterBySubstring, resetButton);
        root.getChildren().add(filterZone);
    }

    public void changeData(String requestString, String typeOfRequest) {
        try {
            System.out.println(typeOfRequest);
            response = null;
            if (typeOfRequest.equals("d")) {
                clientManager.commandsWithParam("getDigitFilteredMoviesRS", requestString);
            } else if (typeOfRequest.equals("s")) {
                clientManager.commandsWithParam("getSubstringFilteredMoviesRS", requestString);
            } else {
                clientManager.commandsWithoutParam("getAllMoviesRS");
            }
            while (response == null || !app.clientSerializer.isReadyToReturnMessage()){
                response = app.clientSerializer.getNewResponse();
            }
            app.clientSerializer.setReadyToReturnMessage(false);
            ResultSet resultSet = (ResultSet) response.getResponseData();
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
            app.customizedAlert(bundle.getString("incorrectFilterData")).showAndWait();
        } catch (Exception err) {
            err.printStackTrace();
            app.customizedAlert(bundle.getString("stupidDevEr")).showAndWait();
        }
    }
}