package auxiliary_classes;

import java.io.Serializable;
import java.util.ResourceBundle;

public class CommandMessage<T> implements Serializable {
    private String commandName;
    private String classname;
    private T commandData;
    private String login;
    private String password;

    public CommandMessage(String classname, String commandName, T commandData, String login, String password) {
        this.commandName = commandName;
        this.classname = classname;
        this.commandData = commandData;
        this.login = login;
        this.password = password;
    }

    public CommandMessage(String classname, String commandName, String login, String password) {
        this(classname, commandName, null, login, password);
    }

    public String getClassname() {
        return classname;
    }

    public String getCommandName() {
        return commandName;
    }

    public T getCommandData() {
        return commandData;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }

    public void setCommandData(T commandData) {
        this.commandData = commandData;
    }

}
