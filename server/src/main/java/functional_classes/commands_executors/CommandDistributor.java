package functional_classes.commands_executors;

import auxiliary_classes.CommandMessage;
import exceptions.AuthorizationException;
import functional_classes.database.DBUserHandler;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Objects;


/**
 * Get CommandMessage from ServerSerializer and execute commands by reflection API, calling executed classes
 */


public class CommandDistributor {

    private HashMap<String, Object> executors = new HashMap<>();

    public <T> void addExecutor(String className, T executor) {
        executors.put(className, executor);
    }

    public <T> T execution(CommandMessage commandMessage) {
        String className = commandMessage.getClassname();
        System.out.println(className + " " + commandMessage.getCommandName());
        Object o;
        Method methodToInvoke;
        try {
            // execution

            Class<?> c = Class.forName("functional_classes." + ((!className.contains(".")) ? ((Objects.equals(className, "CollectionAnalyzer") || Objects.equals(className, "FileWorker")) ? "commands_executors." + className : "database." + className) : className));
            if (Objects.equals(commandMessage.getCommandName(), "registration")) {
                methodToInvoke = c.getMethod(commandMessage.getCommandName(), commandMessage.getLogin().getClass(), commandMessage.getPassword().getClass());
            } else if (Objects.equals(commandMessage.getCommandName(), "isUserExists")) {
                methodToInvoke = c.getMethod(commandMessage.getCommandName(), commandMessage.getLogin().getClass(), commandMessage.getPassword().getClass());
            } else if (((DBUserHandler) executors.get("DBUserHandler")).isUserExists(commandMessage.getLogin(), commandMessage.getPassword())) {
                ((CollectionAnalyzer) executors.get("CollectionAnalyzer")).setCurrentLogin(commandMessage.getLogin());
                methodToInvoke = (commandMessage.getCommandData() != null ? c.getMethod(commandMessage.getCommandName(), commandMessage.getCommandData().getClass()) : c.getMethod(commandMessage.getCommandName()));
            } else {
                throw new AuthorizationException();
            }

            // return answer

            if (commandMessage.getCommandData() != null) {
                o = (T) methodToInvoke.invoke(executors.get(className), commandMessage.getCommandData());
            } else if (Objects.equals(commandMessage.getCommandName(), "registration")) {
                o = ((DBUserHandler) executors.get("DBUserHandler")).registration(commandMessage.getLogin(), commandMessage.getPassword());
            } else if (Objects.equals(commandMessage.getCommandName(), "isUserExists")) {
                o = ((DBUserHandler) executors.get("DBUserHandler")).isUserExists(commandMessage.getLogin(), commandMessage.getPassword());
            } else if (Objects.equals(commandMessage.getCommandName(), "getAllMoviesRS")) {
                o = (T) methodToInvoke.invoke(executors.get(className));
            } else {
                o = (T) methodToInvoke.invoke(executors.get(className));
            }
            return (T) o;
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException | SecurityException |
                 IllegalArgumentException |
                 InvocationTargetException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            return (T) "Что-то пошло не так...";
        } catch (AuthorizationException ignored) {
        }
        return null;
    }

}