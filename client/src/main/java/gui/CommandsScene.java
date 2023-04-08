package gui;

import auxiliary_classes.CommandMessage;
import auxiliary_classes.FormField;
import enums.Country;
import enums.MovieGenre;
import enums.MpaaRating;
import functional_classes.ClientManager;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import java.util.*;
import java.util.List;

public class CommandsScene {
    FXApplication app;
    FlowPane flowPane;
    ClientManager clientManager;
    static ArrayList<FormField> form = new ArrayList<>();
    static HashMap<Integer, Object> answers = new HashMap<>();
    CommandMessage<Object> commandMessage;
    int step = 0;

    static {
        form.add(new FormField(0, "String", true, "Введите название фильма"));
        form.add(new FormField(1, "Integer", true, "Введите координату x (это значение должно быть целым и больше -319)"));
        form.add(new FormField(2, "int", true, "Введите координату y"));
        form.add(new FormField(3, "long", true, "Введите количество оскаров у этого фильма"));
        form.add(new FormField(4, "long", true, "Введите длину фильма"));
        form.add(new FormField(5, "MovieGenre", false, "Введите жанр фильма: " + Arrays.asList(MovieGenre.values())));
        form.add(new FormField(6, "MpaaRating", false, "Введите рейтинг фильма:" + Arrays.asList(MpaaRating.values())));
        form.add(new FormField(7, "String", true, "Введите имя оператора"));
        form.add(new FormField(8, "String", true, "Введите данные паспорта оператора"));
        form.add(new FormField(9, "Country", false, "Введите национальность оператора: " + Arrays.asList(Country.values())));
        form.add(new FormField(10, "long", false, "Введите местоположение оператора (координата x)"));
        form.add(new FormField(11, "long", false, "Введите местоположение оператора (координата y)"));
        form.add(new FormField(12, "double", false, "Введите местоположение оператора (координата z)"));
    }

    public CommandsScene(FXApplication app, ClientManager clientManager) {
        this.app = app;
        this.clientManager = clientManager;
    }

    public Scene openScene() {
        clientManager.startNewAction("login 88 88");
        flowPane = new FlowPane();
        flowPane.setHgap(10);
        flowPane.setVgap(10);
        flowPane.setPrefWidth(1000);
        flowPane.setPrefHeight(500);
        flowPane.setPadding(new Insets(10));

        List<Button> buttonList = retButtonList();
        buttonList.forEach(button -> flowPane.getChildren().add(button));

        Scene scene = new Scene(flowPane, 300, 150, Color.rgb(240, 217, 164));  // создание Scene
        return scene;
    }

    public List<Button> retButtonList() {
        List<Button> buttonList = new ArrayList<>();
        buttonList.add(retAddButton());
        buttonList.add(retAddIfMinButton());
        buttonList.add(retAddIfMaxButton());
        return buttonList;
    }

    public Button retAddButton() {
        Button addButton = new Button("add");
        addButton.setOnAction(e -> {
            step = 0;
            readInputNewMovieData("add");
        });
        return addButton;
    }

    public Button retAddIfMinButton() {
        Button addButton = new Button("addIfMin");
        addButton.setOnAction(e -> {
            step = 0;
            readInputNewMovieData("addIfMin");
        });
        return addButton;
    }

    public Button retAddIfMaxButton() {
        Button addButton = new Button("addIfMax");
        addButton.setOnAction(e -> {
            step = 0;
            readInputNewMovieData("addIfMax");
        });
        return addButton;
    }

    public void readInputNewMovieData(String commandName) {
        Button nextStep = new Button("Далее");
        if (step < form.size()) {
            Label label = new Label(form.get(step).getLabel() + ". Тип этого значения: " + form.get(step).getExpectedType() + (form.get(step).getIsNecessary() ? ". Обязательное значение" : ". Необязательное значение"));
            TextField textField = new TextField();
            FlowPane group = new FlowPane();
            group.getChildren().add(label);
            group.getChildren().add(textField);
            group.getChildren().add(nextStep);
            flowPane.getChildren().add(group);
            nextStep.setOnAction(e -> {
                String line = textField.getText();
                validate(line, step);
                readInputNewMovieData(commandName);
                flowPane.getChildren().remove(group);
            });
        } else {
            nextStep.setText("Создать фильм!");
            customizedAlert(clientManager.commandsWithParam(commandName, answers).getResponseData().toString()).showAndWait();
        }
    }

    public void validate(String line, int nextStep) {
        try {
//            if (line.equals("exit")) {
//                System.exit(0);
//            }
            if (line.length() == 0 && form.get(nextStep).getIsNecessary()) {
                System.out.println("Значение не может быть пустым");
                return;

            } else {
                if (line.length() == 0) {
                    answers.put(nextStep, null);
                    nextStep += 1;
                    step = nextStep;
                    return;
                }
            }
            switch (form.get(nextStep).getExpectedType()) {
                case ("Integer"), ("int") -> {
                    int parsedValue = Integer.parseInt(line);
                    if (form.get(nextStep).getKey() == 1 && parsedValue <= -319) {
                        System.out.println("Значение должно быть больше -319");
                    } else {
                        answers.put(nextStep, parsedValue);
                        nextStep += 1;
                    }
                    answers.put(nextStep, parsedValue);
                }
                case ("long") -> {
                    long parsedValue = Long.parseLong(line);
                    if ((form.get(nextStep).getKey() == 3 || form.get(nextStep).getKey() == 4) && parsedValue <= 0) {
                        System.out.println("Значение должно быть больше нуля");
                    } else {
                        answers.put(nextStep, parsedValue);
                        nextStep += 1;
                    }
                }
                case ("double") -> {
                    double parsedValue = Double.parseDouble(line);
                    answers.put(nextStep, parsedValue);
                    nextStep += 1;
                }
                case ("String") -> {
                    if ((form.get(nextStep).getKey() == 0 || form.get(nextStep).getKey() == 7 || form.get(nextStep).getKey() == 8) && line.trim().isEmpty()) {
                        System.out.println("Значение не может быть пустым");
                    } else {
                        if (form.get(nextStep).getKey() == 8 && line.length() < 9) {
                            System.out.println("Значение должно состоять не менее чем из 9 символов");
                        } else {
                            answers.put(nextStep, line);
                            nextStep += 1;
                        }
                    }
                }
                case ("MovieGenre") -> {
                    MovieGenre parsedValue = Enum.valueOf(MovieGenre.class, line);
                    answers.put(nextStep, parsedValue);
                    nextStep += 1;
                }
                case ("MpaaRating") -> {
                    MpaaRating parsedValue = Enum.valueOf(MpaaRating.class, line);
                    answers.put(nextStep, parsedValue);
                    nextStep += 1;
                }
                case ("Country") -> {
                    Country parsedValue = Enum.valueOf(Country.class, line);
                    answers.put(nextStep, parsedValue);
                    nextStep += 1;
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Введите значение правильного типа данных: " + form.get(nextStep).getExpectedType());
        } catch (IllegalArgumentException e) {
            System.out.println("Введите значение из списка допустимых значений ->");
        }
        step = nextStep;
    }

    public Alert customizedAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(message);
        alert.setContentText(clientManager.getLogin());
        return alert;
    }
}