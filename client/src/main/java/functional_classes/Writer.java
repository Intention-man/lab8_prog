package functional_classes;

import auxiliary_classes.ResponseMessage;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Writer {
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
        commandList.put("count_by_oscars_count oscarsCount", "вывести количество элементов, значение поля oscarsCount которых равно заданному");}

    public void suggestNewAction() {
        System.out.println("Введите команду. Чтобы узнать перечень доступных команд введите help");
    }

    public <T> void printResponse(ResponseMessage response) {
        if (response.getTypeName().equals("java.util.ArrayList")) {
            ((List<String>) response.getResponseData()).stream()
                    .map(String::strip)
                    .forEach(System.out::println);
        } else {
            if (Objects.equals(response.getResponseData(), true)) {
                System.out.println("Действие успешно выполненно");
            } else if (Objects.equals(response.getResponseData(), false)){
                System.out.println("Что-то пошло не так");
            }
            else {
//                System.out.println(response.getResponseData());
            }
        }
    }

    public void help() {
        for (var entry : commandList.entrySet()) {
            System.out.println(entry.getKey() + " - " + entry.getValue());
        }
    }
}