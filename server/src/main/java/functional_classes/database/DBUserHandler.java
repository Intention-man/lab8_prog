package functional_classes.database;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

public class DBUserHandler {
    Connection connection;
    Statement stat;

    public DBUserHandler(Connection connection) throws SQLException {
        this.connection = connection;
        stat = connection.createStatement();
        createUserTableIfNotExist();
    }

    public synchronized boolean isLoginOccupied(String login) throws SQLException {;
        PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM users WHERE login=(?)");
        statement.setString(1, login);
        return ContainerCommonParts.existenceQuery(statement);
    }

    public synchronized boolean isUserExists(String login, String password) throws SQLException {;
        PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM users WHERE login=(?) AND password=(?)");
        statement.setString(1, login);
        statement.setString(2, getMD5Hash(password));
        return ContainerCommonParts.existenceQuery(statement);
    }

    public synchronized void createUserTableIfNotExist() throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("CREATE table IF NOT EXISTS users (id SERIAL PRIMARY KEY, login VARCHAR(255) unique, password VARCHAR(255))");
        statement.close();
    }
    
    public synchronized String registration(String login, String password) throws SQLException {
        if (isLoginOccupied(login)){
            return "Пользователь с таким username уже существует. Если это вы, то введите команду с верным логином и паролем, иначе придумайте другой логин, чтобы зарегистриоваться";
        }
        else {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO users VALUES (default, ?, ?)");
            statement.setString(1, login);
            statement.setString(2, getMD5Hash(password));
            statement.executeUpdate();
            statement.close();
            if (isUserExists(login, password)) {
                return "Регистрация успешна. Можете авторизироваться";
            } else {return "Ошибка при попытке регистрации";}
        }
    }

    public synchronized String getMD5Hash(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashInBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashInBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public synchronized void close() throws SQLException {
        stat.close();
        connection.close();
    }
}
