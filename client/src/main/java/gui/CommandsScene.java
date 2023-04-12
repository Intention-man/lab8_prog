package gui;

import auxiliary_classes.FormField;
import enums.Country;
import enums.MovieGenre;
import enums.MpaaRating;
import functional_classes.ClientManager;
import functional_classes.ClientReader;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import movies_classes.Movie;
import movies_classes.Movies;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

public class CommandsScene {
    FXApplication app;
    FlowPane root;
    ClientManager clientManager;
    ClientReader clientReader;
    static ArrayList<FormField> form = new ArrayList<>();
    static HashMap<Integer, Object> answers = new HashMap<>();
    int step = 0;
    String alertText;

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
        clientReader = new ClientReader();
    }

    public Scene openScene() {
        clientManager.startNewAction("login 88 88");
        root = new FlowPane(Orientation.VERTICAL, 30.0, 30.0, app.navigateButtonList());

        List<Button> buttonList = retButtonList();
        FlowPane buttonContainer = new FlowPane(Orientation.HORIZONTAL, 30.0, 30.0);
        buttonContainer.setPrefWidth(app.getPrimaryStage().getWidth());
        buttonList.forEach(button -> buttonContainer.getChildren().add(button));
        root.getChildren().add(buttonContainer);
        return new Scene(root, 100, 50, Color.rgb(240, 217, 164));
    }

    public List<Button> retButtonList() {
        List<Button> buttonList = new ArrayList<>();
        buttonList.add(retAddButton());
        buttonList.add(retAddIfMinButton());
        buttonList.add(retAddIfMaxButton());
        buttonList.add(retUpdateButton());
        buttonList.add(retHelpButton());
        buttonList.add(retInfoButton());
        buttonList.add(retShowButton());
        buttonList.add(retRemoveByIdButton());
        buttonList.add(retRemoveByOscarsCountButton());
        buttonList.add(retClearButton());
        buttonList.add(retHistoryButton());
        buttonList.add(retSumOfLengthButton());
        buttonList.add(retCountByOscarsCountButton());
        buttonList.add(retExecuteFileButton());
        buttonList.add(retExitButton());
        return buttonList;
    }

    // POST commands

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

    public Button retUpdateButton() {
        Button updateButton = new Button("Изменить фильм");
        updateButton.setOnAction(e -> {
            List<Movie> moviesList = ((Movies) clientManager.commandsWithoutParam("getMovies").getResponseData()).getSortedMovies("name");
            ToggleGroup group = new ToggleGroup();
            moviesList.forEach(movie -> {
                System.out.println(movie.getName() + " " + movie.getCreator());
                RadioButton rBtn = new RadioButton(movie.getName());
                rBtn.setToggleGroup(group);
                root.getChildren().add(rBtn);
                rBtn.setOnAction(event -> {
                    app.setMovieInfoScene(movie.getId(), movie.getCreator());
                });
            });
        });
        return updateButton;
    }
    
    public Button retRemoveByIdButton(){
        Button button = new Button("removeById");
        button.setOnAction(e -> {
            Label label = new Label("Введите id фильма, который хотите удалить");
            TextField textField = new TextField();
            Button delButton = new Button("Удалить");
            FlowPane group = new FlowPane();
            group.getChildren().add(label);
            group.getChildren().add(textField);
            group.getChildren().add(delButton);
            root.getChildren().add(group);
            delButton.setOnAction(e2 -> {
                try{
                    int id = Integer.parseInt(textField.getText().trim());
                    app.customizedAlert((String) clientManager.commandsWithParam("removeById", id).getResponseData()).showAndWait();
                } catch (Exception err){
                    app.customizedAlert("Вы ввели некорректное значение id. Повторите попытку").showAndWait();
                }
            });
        });
        return button;
    }

    public Button retRemoveByOscarsCountButton(){
        Button button = new Button("removeByOscarsCount");
        button.setOnAction(e -> {
            Label label = new Label("Введите количество оскаров - удалится случайный фильм с таким количеством (если есть хотя бы 1 такой фильм)");
            TextField textField = new TextField();
            Button delButton = new Button("Удалить");
            FlowPane group = new FlowPane();
            group.getChildren().add(label);
            group.getChildren().add(textField);
            group.getChildren().add(delButton);
            root.getChildren().add(group);
            delButton.setOnAction(e2 -> {
                try{
                    long oscarsCount = Long.parseLong(textField.getText().trim());
                    app.customizedAlert((String) clientManager.commandsWithParam("removeByOscarsCount", oscarsCount).getResponseData()).showAndWait();
                } catch (Exception err){
                    app.customizedAlert("Вы ввели некорректное количество оскаров. Повторите попытку").showAndWait();
                }
            });
        });
        return button;
    }

    public Button retClearButton() {
        Button button = new Button("clear");
        button.setOnAction(e -> {
            step = 0;
            app.customizedAlert(clientManager.commandsWithoutParam("clear").getResponseData().toString()).showAndWait();
        });
        return button;
    }

    // GET commands

    public Button retHelpButton() {
        Button button = new Button("help");
        button.setOnAction(e -> {
            app.customizedAlert(clientManager.noRSCommands("help")).showAndWait();
        });
        return button;
    }

    public Button retInfoButton() {
        Button button = new Button("info");
        button.setOnAction(e -> {
            clientManager.noRSCommands("info");
        });
        return button;
    }

    public Button retHistoryButton() {
        Button button = new Button("history");
        button.setOnAction(e -> {
            app.customizedAlert(clientManager.noRSCommands("history")).showAndWait();
        });
        return button;
    }

    public Button retShowButton() {
        Button button = new Button("show");
        button.setOnAction(e -> {
            app.setTableScene();
        });
        return button;
    }

    public Button retSumOfLengthButton() {
        Button button = new Button("sumOfLength");
        button.setOnAction(e -> {
            app.customizedAlert("Суммарная длина всех фильмов в коллекции: " + clientManager.noRSCommands("sumOfLength")).showAndWait();
        });
        return button;
    }

    public Button retCountByOscarsCountButton(){
        Button button = new Button("countByOscarsCount");
        button.setOnAction(e -> {
            Label label = new Label("Введите количество оскаров - выведется количество фильмов с таким количеством оскаров");
            TextField textField = new TextField();
            Button getButton = new Button("Узнать количество подходящих фильмов");
            FlowPane group = new FlowPane();
            group.getChildren().add(label);
            group.getChildren().add(textField);
            group.getChildren().add(getButton);
            root.getChildren().add(group);
            getButton.setOnAction(e2 -> {
                try{
                    long oscarsCount = Long.parseLong(textField.getText().trim());
                    app.customizedAlert("Такое количество оскаров имеет(-ют): " + clientManager.commandsWithParam("countByOscarsCount", oscarsCount).getResponseData().toString() + " фильмов").showAndWait();
                } catch (Exception err){
                    app.customizedAlert(err.getMessage()).showAndWait();
                }
            });
        });
        return button;
    }

    // DO commands

    public Button retExecuteFileButton() {
        Button button = new Button("executeFile");
        button.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Resource File");
            File selectedFile = fileChooser.showOpenDialog(app.getPrimaryStage());
            if (selectedFile != null) {
                clientManager.startReadFile(selectedFile.getAbsolutePath());
            }
        });
        return button;
    }

    public Button retExitButton() {
        Button button = new Button("exit");
        button.setOnAction(e -> {
            System.exit(0);
        });
        return button;
    }



    // not buttons
    
    public void readInputNewMovieData(String commandName) {
        Button nextStep = new Button("Далее");
        if (step < form.size()) {
            Label label = new Label(form.get(step).getLabel() + ". Тип этого значения: " + form.get(step).getExpectedType() + (form.get(step).getIsNecessary() ? ". Обязательное значение" : ". Необязательное значение"));
            TextField textField = new TextField();
            FlowPane group = new FlowPane();
            group.getChildren().add(label);
            group.getChildren().add(textField);
            group.getChildren().add(nextStep);
            root.getChildren().add(group);
            nextStep.setOnAction(e -> {
                String line = textField.getText();
                validate(line, step);
                readInputNewMovieData(commandName);
                root.getChildren().remove(group);
            });
        } else {
            nextStep.setText("Создать фильм!");
            app.customizedAlert(clientManager.commandsWithParam(commandName, answers).getResponseData().toString()).showAndWait();
        }
    }

    public void validate(String line, int nextStep) {
        try {
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


}