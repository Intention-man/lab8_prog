package functional_classes;

import auxiliary_classes.FormField;
import enums.Country;
import enums.MovieGenre;
import enums.MpaaRating;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


public class ClientReader {
    public ClientManager clientManager;
    public Scanner chosenScanner;
    public List<Scanner> scannerList;
    private List<String> executedFiles = new ArrayList<>();
    static ArrayList<FormField> form = new ArrayList<>();
    static HashMap<Integer, Object> answers = new HashMap<>();
    ResourceBundle bundle;

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

    public ClientReader(ResourceBundle bundle) {
        this.bundle = bundle;
        scannerList = new ArrayList<>();
        scannerList.add(new Scanner(System.in));
        chosenScanner = scannerList.get(0);
    }

    public String readNextLine() {
        chosenScanner = scannerList.get(scannerList.size() - 1);
        try {
            if ((!chosenScanner.equals(new Scanner(System.in)) && chosenScanner.hasNextLine()) || chosenScanner.equals(new Scanner(System.in))) {
                return chosenScanner.nextLine();
            } else {
                return null;
            }
        } catch (NoSuchElementException e) {
            System.out.println("Вот оно где!");
        }
        return null;
    }

    public HashMap<Integer, Object> readInputNewMovieData() {
        int step = 0;
        while (step < form.size()) {
            try {
                clientManager.app.customizedAlert(form.get(step).getLabel() + bundle.getString("valueType") + form.get(step).getExpectedType() + (form.get(step).getIsNecessary() ? bundle.getString("necessaryValue") : bundle.getString("unnecessaryValue"))).showAndWait();
                String line = chosenScanner.nextLine().trim();
                if (line.equals("exit")) {
                    System.exit(0);
                }
                if (line.length() == 0 && form.get(step).getIsNecessary()) {
                    clientManager.app.customizedAlert(bundle.getString("mustNotNull"));
                    continue;
                } else {
                    if (line.length() == 0) {
                        answers.put(step, null);
                        step += 1;
                        continue;
                    }
                }
                switch (form.get(step).getExpectedType()) {
                    case ("Integer"), ("int") -> {
                        int parsedValue = Integer.parseInt(line);
                        if (form.get(step).getKey() == 1 && parsedValue <= -319) {
                            clientManager.app.customizedAlert(bundle.getString("mustMoreMinus319"));
                        } else {
                            answers.put(step, parsedValue);
                            step += 1;
                        }
                        answers.put(step, parsedValue);
                    }
                    case ("long") -> {
                        long parsedValue = Long.parseLong(line);
                        if ((form.get(step).getKey() == 3 || form.get(step).getKey() == 4) && parsedValue <= 0) {
                            clientManager.app.customizedAlert(bundle.getString("mustMore0"));
                        } else {
                            answers.put(step, parsedValue);
                            step += 1;
                        }
                    }
                    case ("double") -> {
                        double parsedValue = Double.parseDouble(line);
                        answers.put(step, parsedValue);
                        step += 1;
                    }
                    case ("String") -> {
                        if ((form.get(step).getKey() == 0 || form.get(step).getKey() == 7 || form.get(step).getKey() == 8) && line.trim().isEmpty()) {
                            clientManager.app.customizedAlert(bundle.getString("mustNotNull"));
                        } else {
                            if (form.get(step).getKey() == 8 && line.length() < 9) {
                                clientManager.app.customizedAlert(bundle.getString("mustHaveLenMore9"));
                            } else {
                                answers.put(step, line);
                                step += 1;
                            }
                        }
                    }
                    case ("MovieGenre") -> {
                        MovieGenre parsedValue = Enum.valueOf(MovieGenre.class, line);
                        answers.put(step, parsedValue);
                        step += 1;
                    }
                    case ("MpaaRating") -> {
                        MpaaRating parsedValue = Enum.valueOf(MpaaRating.class, line);
                        answers.put(step, parsedValue);
                        step += 1;
                    }
                    case ("Country") -> {
                        Country parsedValue = Enum.valueOf(Country.class, line);
                        answers.put(step, parsedValue);
                        step += 1;
                    }
                }
            } catch (NumberFormatException e) {
                clientManager.app.customizedAlert(bundle.getString("inputCorrectType") + form.get(step).getExpectedType());
            } catch (IllegalArgumentException e) {
                clientManager.app.customizedAlert(bundle.getString("inputCorrectValueFromList"));
            }
        }
        return answers;
    }

    public void readFile(String fileName) {
        try {
            if (!chosenScanner.equals(new Scanner(System.in)) && executedFiles.contains(fileName)) {
                clientManager.app.customizedAlert(bundle.getString("fileRecursion"));
                return;
            } else if (chosenScanner.equals(new Scanner(System.in)) || !executedFiles.contains(fileName)) {
                File file = new File(fileName);
                if (!file.exists()){
                    clientManager.app.customizedAlert(bundle.getString("fileDoesNotExist"));
                    return;
                }
                if (!file.canRead()){
                    clientManager.app.customizedAlert(bundle.getString("fileNotAvailableToRead"));
                    return;
                }
                if (!file.canExecute()){
                    clientManager.app.customizedAlert(bundle.getString("fileNotAvailableToExecute"));
                    return;
                }
                Path path = Paths.get(fileName);
                String mimeType = Files.probeContentType(path);
                if (Objects.equals(mimeType.split("/")[0], "text"))  {
                    executedFiles.add(fileName);
                    scannerList.add(new Scanner(path));
                    chosenScanner = scannerList.get(scannerList.size() - 1);
                } else {
                    clientManager.app.customizedAlert(bundle.getString("fileMustHaveTextType"));
                    return;
                }
            }
            while (chosenScanner.hasNextLine()) {
                clientManager.startNewAction(chosenScanner.nextLine().trim());
            }
            getExecutedFiles().remove(fileName);
            chosenScanner.close();
            scannerList.remove(scannerList.size() - 1);
            if (getExecutedFiles().size() == 0) {
                chosenScanner = new Scanner(System.in);
            } else {
                chosenScanner = scannerList.get(scannerList.size() - 1);
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    // getters and setters

    public List<String> getExecutedFiles() {
        return executedFiles;
    }

    public void setClientManager(ClientManager clientManager) {
        this.clientManager = clientManager;
    }

}