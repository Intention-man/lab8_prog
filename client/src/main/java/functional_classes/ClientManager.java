package functional_classes;

import auxiliary_classes.CommandMessage;
import auxiliary_classes.ResponseMessage;

import java.util.*;

/**
 * Command Distributor of client app. Get data from ClientReader, send command to Client Serializer get from last and send to Writer to print answer for user
 */

public class ClientManager {

    // initialization

    ClientReader clientReader;
    Writer writer;
    ClientSerializer clientSerializer;
    String login;
    String password;
    static HashMap<String, String> commandList = new HashMap<>();

    static {
        commandList.put("help", "вывести справку по доступным командам");
        commandList.put("info", "вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)");
        commandList.put("show ", "вывести в стандартный поток вывода все элементы коллекции в строковом представлении");
        commandList.put("add {element}", "добавить новый элемент в коллекцию");
        commandList.put("update id {element}", "обновить значение элемента коллекции, id которого равен заданному");
        commandList.put("remove_by_id id", "удалить элемент из коллекции по его id");
        commandList.put("clear", "очистить коллекцию");
        commandList.put("save", "сохранить коллекцию в файл");
        commandList.put("execute_script file_name", "считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.");
        commandList.put("exit ", "завершить программу (без сохранения в файл)");
        commandList.put("add_if_max {element}", "добавить новый элемент в коллекцию, если его значение превышает значение наибольшего элемента этой коллекции");
        commandList.put("add_if_min {element}", "добавить новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции");
        commandList.put("history", "вывести последние 12 команд (без их аргументов)");
        commandList.put("remove_any_by_oscars_count oscarsCount", "удалить из коллекции один элемент, значение поля oscarsCount которого эквивалентно заданному");
        commandList.put("sum_of_length", "вывести сумму значений поля length для всех элементов коллекции");
        commandList.put("count_by_oscars_count oscarsCount", "вывести количество элементов, значение поля oscarsCount которых равно заданному");
    }

    public ClientManager(ClientSerializer clientSerializer, ClientReader clientReader, Writer writer) {
        this.clientSerializer = clientSerializer;
        this.clientReader = clientReader;
        this.writer = writer;

    }

    // main action

    public ResponseMessage<? extends Object> startNewAction(String clientInput) {
        try {
            List<String> splitedClientInput = Arrays.asList(clientInput.split("\\s+"));
            String executedCommand = splitedClientInput.get(0);
            CommandMessage<Object> commandMessage;
            if (Objects.equals(executedCommand, "help")) {
                writer.help();
            } else if (Objects.equals(executedCommand, "login")) {
                login = splitedClientInput.get(splitedClientInput.size() - 2);
                password = splitedClientInput.get(splitedClientInput.size() - 1);
                commandMessage = new CommandMessage<>("DBUserHandler", "isUserExists", login, password);
                return clientSerializer.send(commandMessage);
            } else if (Objects.equals(executedCommand, "registration")) {
                login = splitedClientInput.get(splitedClientInput.size() - 2);
                password = splitedClientInput.get(splitedClientInput.size() - 1);
                commandMessage = new CommandMessage<>("DBUserHandler", "registration", login, password);
                ResponseMessage<? extends Object> response = clientSerializer.send(commandMessage);
                writer.printResponse(response);
                return response;
            } else {
                String param = "";
                if (splitedClientInput.size() >= 2) {
                    param = String.join(" ", splitedClientInput.subList(1, splitedClientInput.size()));
                    System.out.println(splitedClientInput.subList(1, splitedClientInput.size() - 1));
                    System.out.println(splitedClientInput + "  /////  " + param);
                }

                if (login != null && password != null) {
                    commandMessage = new CommandMessage<>("CollectionAnalyzer", "addCommandToHistory", executedCommand, login, password);
                    clientSerializer.send(commandMessage);
                    System.out.println("executedCommand: " + executedCommand);
                    switch (executedCommand) {
                        case ("add") -> {
                            commandMessage = new CommandMessage<>("CollectionAnalyzer", "addMovie", clientReader.readInputNewMovieData(), login, password);
                            writer.printResponse(clientSerializer.send(commandMessage));
                        }
                        case ("add_if_max") -> {
                            commandMessage = new CommandMessage<>("CollectionAnalyzer", "addIfMax", clientReader.readInputNewMovieData(), login, password);
                            writer.printResponse(clientSerializer.send(commandMessage));
                        }
                        case ("add_if_min") -> {
                            commandMessage = new CommandMessage<>("CollectionAnalyzer", "addIfMin", clientReader.readInputNewMovieData(), login, password);
                            writer.printResponse(clientSerializer.send(commandMessage));
                        }
                        case ("clear") -> {
                            commandMessage = new CommandMessage<>("CollectionAnalyzer", "clear", login, password);
                            writer.printResponse(clientSerializer.send(commandMessage));
                        }
                        case ("count_by_oscars_count") -> {
                            if (param.matches("\\d*")) {
                                commandMessage = new CommandMessage<Object>("CollectionAnalyzer", "countByOscarsCount", Long.parseLong(param), login, password);
                                writer.printResponse(clientSerializer.send(commandMessage));
                            } else {
                                System.out.println("Количество оскаров должно быть целым числом");
                            }
                        }
                        case ("execute_script") -> clientReader.readFile(param);
                        case ("history") -> {
                            commandMessage = new CommandMessage<>("CollectionAnalyzer", "getLast12Commands", login, password);
                            writer.printResponse(clientSerializer.send(commandMessage));
                        }
                        case ("info") -> {
                            commandMessage = new CommandMessage<>("CollectionAnalyzer", "info", login, password);
                            writer.printResponse(clientSerializer.send(commandMessage));
                            System.out.println("Исполняемые в данный момент файлы: " + clientReader.getExecutedFiles());
                        }
                        case ("show") -> {
                            commandMessage = new CommandMessage<>("CollectionAnalyzer", "show", login, password);
                            writer.printResponse(clientSerializer.send(commandMessage));
                        }
                        case ("sum_of_length") -> {
                            commandMessage = new CommandMessage<>("CollectionAnalyzer", "sumOfLength", login, password);
                            writer.printResponse(clientSerializer.send(commandMessage));
                        }
                        case ("remove_by_id") -> {
                            if (param.matches("\\d*")) {
                                commandMessage = new CommandMessage<>("CollectionAnalyzer", "removeById", Integer.parseInt(param), login, password);
                                ResponseMessage<?> response = clientSerializer.send(commandMessage);
                                writer.printResponse(response);
                            } else {
                                System.out.println("id должно быть целым числом");
                            }
                        }
                        case ("remove_any_by_oscars_count") -> {
                            if (param.matches("\\d*")) {
                                commandMessage = new CommandMessage<Object>("CollectionAnalyzer", "removeAnyByOscarsCount", Long.parseLong(param), login, password);
                                ResponseMessage<? extends Object> response = clientSerializer.send(commandMessage);
                                writer.printResponse(response);
                            } else {
                                System.out.println("Количество оскаров должно быть целым числом");
                            }
                        }
                        case ("update") -> {
                            if (param.matches("\\d*") && Integer.parseInt(param) >= 0) {
                                HashMap<Integer, Object> map = clientReader.readInputNewMovieData();
                                map.put(map.size(), Integer.parseInt(param));
                                commandMessage = new CommandMessage<>("CollectionAnalyzer", "updateMovie", map, login, password);
                                ResponseMessage<? extends Object> response = clientSerializer.send(commandMessage);
                                writer.printResponse(response);
                            } else {
                                System.out.println("id должно быть целым числом");
                            }
                        }
                        default -> System.out.println("Введите команду из доступного перечня");
                    }
                } else {
                    System.out.println("Залогиньтесь!");
                }
            }
        } catch (NumberFormatException e) {
            System.out.println(e);
        } catch (Exception e) {
            System.out.println("Что-то пошло не так. Проверьте корректность введеных данных");
        }
        return null;
    }

    public String noRSCommands(String commandName) {
        CommandMessage<Object> commandMessage;
        commandMessage = new CommandMessage<>("CollectionAnalyzer", "addCommandToHistory", commandName, login, password);
        clientSerializer.send(commandMessage);
        switch (commandName) {
            case ("help") -> {
                StringBuilder message = new StringBuilder();
                for (var entry : commandList.entrySet()) {
                    message.append(entry.getKey()).append(" - ").append(entry.getValue()).append("\n");
                }
                return message.toString();
            }
            case ("info") -> {
                commandMessage = new CommandMessage<>("CollectionAnalyzer", "info", login, password);
                clientSerializer.send(commandMessage);
//                ArrayList<String> answer = (ArrayList<String>) clientSerializer.send(commandMessage).getResponseData();
//                while (!clientSerializer.isReadyToReturnMessage()){
//                    int t = 0;
//                }

            }
            case ("history") -> {
                commandMessage = new CommandMessage<>("CollectionAnalyzer", "getLast12Commands", login, password);
                clientSerializer.send(commandMessage);
//                ArrayList<String> answer = (ArrayList<String>) clientSerializer.send(commandMessage).getResponseData();
//                StringBuilder message = new StringBuilder();
//                for (var str : answer) {
//                    message.append(str).append("\n");
//                }
//                return message.toString();
            }
            case ("sumOfLength") -> {
                commandMessage = new CommandMessage<>("CollectionAnalyzer", "sumOfLength", login, password);
                return clientSerializer.send(commandMessage).getResponseData().toString();
            }
        }
        return null;
    }

    public ResponseMessage commandsWithoutParam(String commandName) {
        CommandMessage<Object> commandMessage;
        commandMessage = new CommandMessage<>("CollectionAnalyzer", "addCommandToHistory", commandName, login, password);
        clientSerializer.send(commandMessage);
        switch (commandName) {
            case ("getMovies") -> {
                commandMessage = new CommandMessage<>("CollectionAnalyzer", "getMovies", login, password);
                return clientSerializer.send(commandMessage);
            }
            case ("getAllMoviesRS") -> {
                commandMessage = new CommandMessage<>("CollectionAnalyzer", "getAllMoviesRS", login, password);
                return clientSerializer.send(commandMessage);
            }
            case ("clear") -> {
                commandMessage = new CommandMessage<>("CollectionAnalyzer", "clear", login, password);
                return clientSerializer.send(commandMessage);
            }
        }
        return null;
    }

    public ResponseMessage commandsWithParam(String commandName, java.io.Serializable commandData) {
        CommandMessage<Object> commandMessage;
        commandMessage = new CommandMessage<>("CollectionAnalyzer", "addCommandToHistory", commandName, login, password);
        clientSerializer.send(commandMessage);
        switch (commandName) {
            case ("getMovieRSById") -> {
                commandMessage = new CommandMessage<>("CollectionAnalyzer", "getMovieRSById", commandData, login, password);
                return clientSerializer.send(commandMessage);
            }
            case ("getDigitFilteredMoviesRS") -> {
                commandMessage = new CommandMessage<>("CollectionAnalyzer", "getDigitFilteredMoviesRS", commandData, login, password);
                return clientSerializer.send(commandMessage);
            }
            case ("getSubstringFilteredMoviesRS") -> {
                commandMessage = new CommandMessage<>("CollectionAnalyzer", "getSubstringFilteredMoviesRS", commandData, login, password);
                return clientSerializer.send(commandMessage);
            }
            case ("add") -> {
                commandMessage = new CommandMessage<>("CollectionAnalyzer", "addMovie", commandData, login, password);
                return clientSerializer.send(commandMessage);
            }
            case ("addIfMin") -> {
                commandMessage = new CommandMessage<>("CollectionAnalyzer", "addIfMin", commandData, login, password);
                return clientSerializer.send(commandMessage);
            }
            case ("addIfMax") -> {
                commandMessage = new CommandMessage<>("CollectionAnalyzer", "addIfMax", commandData, login, password);
                return clientSerializer.send(commandMessage);
            }
            case ("update") -> {
                commandMessage = new CommandMessage<>("CollectionAnalyzer", "updateMovie", commandData, login, password);
                return clientSerializer.send(commandMessage);
            }
            case ("removeById") -> {
                commandMessage = new CommandMessage<>("CollectionAnalyzer", "removeById", commandData, login, password);
                return clientSerializer.send(commandMessage);
            }
            case ("removeByOscarsCount") -> {
                commandMessage = new CommandMessage<>("CollectionAnalyzer", "removeAnyByOscarsCount", commandData, login, password);
                return clientSerializer.send(commandMessage);
            }
            case ("countByOscarsCount") -> {
                    commandMessage = new CommandMessage<>("CollectionAnalyzer", "countByOscarsCount", commandData, login, password);
                return clientSerializer.send(commandMessage);
            }
        }
        return null;
    }

    public String getLogin() {
        return login;
    }

    public void startReadFile(String fileName){
        clientReader.readFile(fileName);
    }
}