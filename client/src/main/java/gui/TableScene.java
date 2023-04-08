package gui;

import auxiliary_classes.ResponseMessage;
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


public class TableScene {
    FXApplication app;
    ClientManager clientManager;
    TableView table;
    Label lbl;
    ObservableList data;

    public TableScene(FXApplication app, ClientManager clientManager) {
        this.app = app;
        this.clientManager = clientManager;
        table = new TableView<>();
        table.setPrefWidth(750);
        table.setPrefHeight(200);
        lbl = new Label();
        data = FXCollections.observableArrayList();
    }

    public Scene openScene(){
        ResponseMessage response = clientManager.commandsWithoutParam("getAllMoviesRS");
        ResultSet resultSet = (ResultSet) response.getResponseData();
        try {
            for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {
                //We are using non property style for making dynamic table
                final int j = i;
                TableColumn col = new TableColumn(resultSet.getMetaData().getColumnName(i + 1));
                col.setCellValueFactory((Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>) param -> new SimpleStringProperty(param.getValue().get(j) != null ? param.getValue().get(j).toString() : null));
                table.getColumns().addAll(col);
            }

            while (resultSet.next()) {
                //Iterate Row
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                    //Iterate Column
                    row.add(resultSet.getString(i));
                }
                System.out.println("Row [1] added " + row);
                data.add(row);
            }
            //FINALLY ADDED TO TableView
            table.setItems(data);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error on Building Data");
        }

        TextField textField = new TextField();
        Button btnFilter = new Button("Отфильтровать");
        btnFilter.setOnAction(e -> {
            String text = textField.getText();
            filterTableData(text);
        });

        // userButton
        Button userButton = retUserProfileButton();
        
        FlowPane root = new FlowPane(Orientation.VERTICAL, 30, 30, userButton, textField, btnFilter, table);
        Scene scene = new Scene(root, 300, 150, Color.rgb(240, 217, 164));  // создание Scene
        return scene;
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

    public void filterTableData(String text) {
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
//        });
    }
}