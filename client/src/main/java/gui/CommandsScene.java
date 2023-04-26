package gui;

import auxiliary_classes.FormField;
import auxiliary_classes.ResponseMessage;
import enums.Country;
import enums.MovieGenre;
import enums.MpaaRating;
import functional_classes.ClientManager;
import functional_classes.ClientReader;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import movies_classes.Movie;
import movies_classes.Movies;

import java.io.File;
import java.util.*;
import java.util.List;

public class  CommandsScene {
    FXApplication app;
    FlowPane root;
    ClientManager clientManager;
    ClientReader clientReader;
    static ArrayList<FormField> form = new ArrayList<>();
    static HashMap<Integer, Object> answers = new HashMap<>();
    int step = 0;
    ResponseMessage response = null;
    ResourceBundle bundle;


    public CommandsScene(FXApplication app, ClientManager clientManager) {
        this.app = app;
        this.clientManager = clientManager;
        bundle = app.getBundle();
        clientReader = new ClientReader(bundle);


        form.add(new FormField(0, "String", true, bundle.getString("nameInput")));
        form.add(new FormField(1, "Integer", true, bundle.getString("coordXInput")));
        form.add(new FormField(2, "int", true, bundle.getString("coordYInput")));
        form.add(new FormField(3, "long", true, bundle.getString("oscarsCountInput")));
        form.add(new FormField(4, "long", true, bundle.getString("lengthInput")));
        form.add(new FormField(5, "MovieGenre", false, bundle.getString("movieGenreInput") + Arrays.asList(MovieGenre.values())));
        form.add(new FormField(6, "MpaaRating", false, bundle.getString("mpaaRatingInput") + Arrays.asList(MpaaRating.values())));
        form.add(new FormField(7, "String", true, bundle.getString("operNameInput")));
        form.add(new FormField(8, "String", true, bundle.getString("operPassportInput")));
        form.add(new FormField(9, "Country", false, bundle.getString("operNationInput") + Arrays.asList(Country.values())));
        form.add(new FormField(10, "long", false, bundle.getString("locXInput")));
        form.add(new FormField(11, "long", false, bundle.getString("locYInput")));
        form.add(new FormField(12, "double", false, bundle.getString("locZInput")));
    }

    public Scene openScene() {
//        clientManager.startNewAction("login 88 88");
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
        Button addButton = new Button(bundle.getString("add"));
        addButton.setOnAction(e -> {
            step = 0;
            readInputNewMovieData("add");
        });
        return addButton;
    }

    public Button retAddIfMinButton() {
        Button addButton = new Button(bundle.getString("addIfMin"));
        addButton.setOnAction(e -> {
            step = 0;
            readInputNewMovieData("addIfMin");
        });
        return addButton;
    }

    public Button retAddIfMaxButton() {
        Button addButton = new Button(bundle.getString("addIfMax"));
        addButton.setOnAction(e -> {
            step = 0;
            readInputNewMovieData("addIfMax");
        });
        return addButton;
    }

    public Button retUpdateButton() {
        Button updateButton = new Button(bundle.getString("update"));
        updateButton.setOnAction(e -> {

            clientManager.commandsWithoutParam("getMovies");
            updateResponseData();

            List<Movie> moviesList = ((Movies) response.getResponseData()).getSortedMovies("name");
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
        Button button = new Button(bundle.getString("removeById"));
        button.setOnAction(e -> {
            Label label = new Label(bundle.getString("idInput"));
            TextField textField = new TextField();
            Button delButton = new Button(bundle.getString("delete"));
            FlowPane group = new FlowPane();
            group.getChildren().add(label);
            group.getChildren().add(textField);
            group.getChildren().add(delButton);
            root.getChildren().add(group);
            delButton.setOnAction(e2 -> {
                try{
                    int id = Integer.parseInt(textField.getText().trim());
                    clientManager.commandsWithParam("removeById", id);
                    updateResponseData();
                    app.customizedAlert(response.getResponseData().toString()).showAndWait();
                } catch (Exception err){
                    app.customizedAlert(bundle.getString("incorrectId")).showAndWait();
                }
            });
        });
        return button;
    }

    public Button retRemoveByOscarsCountButton(){
        Button button = new Button(bundle.getString("removeByOscarsCount"));
        button.setOnAction(e -> {
            Label label = new Label(bundle.getString("oscarsCountInputToDelete"));
            TextField textField = new TextField();
            Button delButton = new Button(bundle.getString("delete"));
            FlowPane group = new FlowPane();
            group.getChildren().add(label);
            group.getChildren().add(textField);
            group.getChildren().add(delButton);
            root.getChildren().add(group);
            delButton.setOnAction(e2 -> {
                try{
                    long oscarsCount = Long.parseLong(textField.getText().trim());
                    clientManager.commandsWithParam("removeByOscarsCount", oscarsCount);
                    updateResponseData();
                    app.customizedAlert(response.getResponseData().toString()).showAndWait();
                } catch (Exception err){
                    app.customizedAlert(bundle.getString("incorrectOscars")).showAndWait();
                }
            });
        });
        return button;
    }

    public Button retClearButton() {
        Button button = new Button(bundle.getString("clear"));
        button.setOnAction(e -> {
            step = 0;
            clientManager.commandsWithoutParam("clear");
            updateResponseData();
            app.customizedAlert(response.getResponseData().toString()).showAndWait();
        });
        return button;
    }

    // GET commands

    public Button retHelpButton() {
        Button button = new Button(bundle.getString("help"));
        button.setOnAction(e -> {
            app.customizedAlert(clientManager.noRSCommands("help")).showAndWait();
        });
        return button;
    }

    public Button retInfoButton() {
        Button button = new Button(bundle.getString("info"));
        button.setOnAction(e -> {
            clientManager.noRSCommands("info");
            updateResponseData();
            List<String> list = List.of(response.getResponseData().toString().split("/"));
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < list.size(); i++) {
                if (i < 8 && bundle.containsKey(list.get(i))){
                    stringBuilder.append(bundle.getString(list.get(i)) + " ");
                } else {
                    stringBuilder.append(list.get(i) + " ");
                }
            }
            app.customizedAlert(stringBuilder.toString()).showAndWait();
        });
        return button;
    }

    public Button retHistoryButton() {
        Button button = new Button(bundle.getString("history"));
        button.setOnAction(e -> {
            clientManager.noRSCommands("history");
            updateResponseData();
            app.customizedAlert(response.getResponseData().toString()).showAndWait();
        });
        return button;
    }

    public Button retShowButton() {
        Button button = new Button(bundle.getString("show"));
        button.setOnAction(e -> {
            app.setTableScene();
        });
        return button;
    }

    public Button retSumOfLengthButton() {
        Button button = new Button(bundle.getString("sumOfLength"));
        button.setOnAction(e -> {
            clientManager.noRSCommands("sumOfLength");
            updateResponseData();
            app.customizedAlert(bundle.getString("sumLengthIs") + response.getResponseData().toString()).showAndWait();
        });
        return button;
    }

    public Button retCountByOscarsCountButton(){
        Button button = new Button(bundle.getString("countByOscarsCount"));
        button.setOnAction(e -> {
            Label label = new Label(bundle.getString("countByOscarsCountInput"));
            TextField textField = new TextField();
            Button getButton = new Button(bundle.getString("getMovieCount"));
            FlowPane group = new FlowPane();
            group.getChildren().add(label);
            group.getChildren().add(textField);
            group.getChildren().add(getButton);
            root.getChildren().add(group);
            getButton.setOnAction(e2 -> {
                try{
                    long oscarsCount = Long.parseLong(textField.getText().trim());
                    clientManager.commandsWithParam("countByOscarsCount", oscarsCount);
                    updateResponseData();
                    app.customizedAlert(bundle.getString("havingSoManyOscars") + response.getResponseData().toString()).showAndWait();
                } catch (Exception err){
                    app.customizedAlert(err.getMessage()).showAndWait();
                }
            });
        });
        return button;
    }

    // DO commands

    public Button retExecuteFileButton() {
        Button button = new Button(bundle.getString("executeFile"));
        button.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(bundle.getString("openFile"));
            File selectedFile = fileChooser.showOpenDialog(app.getPrimaryStage());
            if (selectedFile != null) {
                clientManager.startReadFile(selectedFile.getAbsolutePath());
            }
        });
        return button;
    }

    public Button retExitButton() {
        Button button = new Button(bundle.getString("exit"));
        button.setOnAction(e -> {
            System.exit(0);
        });
        return button;
    }

    // not buttons
    
    public void readInputNewMovieData(String commandName) {
        Button nextStep = new Button(bundle.getString("next"));
        if (step < form.size()) {
            Label label = new Label(form.get(step).getLabel() + bundle.getString("valueType") + form.get(step).getExpectedType() + (form.get(step).getIsNecessary() ? bundle.getString("necessaryValue") : bundle.getString("unnecessaryValue")));
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
            nextStep.setText(bundle.getString("createMovie"));
            response = null;
            clientManager.commandsWithParam(commandName, answers);
            while (response == null || !app.clientSerializer.isReadyToReturnMessage()){
                response = app.clientSerializer.getNewResponse();
            }
            app.clientSerializer.setReadyToReturnMessage(false);
            app.customizedAlert(response.getResponseData().toString()).showAndWait();
        }
    }

    public void validate(String line, int nextStep) {
        try {
            if (line.length() == 0 && form.get(nextStep).getIsNecessary()) {
                app.customizedAlert(bundle.getString("mustNotNull")).showAndWait();
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
                        app.customizedAlert(bundle.getString("mustMoreMinus319")).showAndWait();
                    } else {
                        answers.put(nextStep, parsedValue);
                        nextStep += 1;
                    }
                    answers.put(nextStep, parsedValue);
                }
                case ("long") -> {
                    long parsedValue = Long.parseLong(line);
                    if ((form.get(nextStep).getKey() == 3 || form.get(nextStep).getKey() == 4) && parsedValue <= 0) {
                        app.customizedAlert(bundle.getString("mustMore0")).showAndWait();
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
                        app.customizedAlert(bundle.getString("mustNotNull")).showAndWait();
                    } else {
                        if (form.get(nextStep).getKey() == 8 && line.length() < 9) {
                            app.customizedAlert(bundle.getString("mustHaveLenMore9")).showAndWait();
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
            app.customizedAlert(bundle.getString("inputCorrectType") + " " + form.get(nextStep).getExpectedType()).showAndWait();
        } catch (IllegalArgumentException e) {
            app.customizedAlert(bundle.getString("inputCorrectValueFromList")).showAndWait();
        }
        step = nextStep;
    }

    public void updateResponseData(){
        response = null;
        while (response == null || !app.clientSerializer.isReadyToReturnMessage()){
            response = app.clientSerializer.getNewResponse();
        }
        app.clientSerializer.setReadyToReturnMessage(false);
    }
}