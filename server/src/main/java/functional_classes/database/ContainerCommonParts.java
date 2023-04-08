package functional_classes.database;

import movies_classes.Movie;

import java.sql.*;

public class ContainerCommonParts {

    public static int countQuery(PreparedStatement statement) throws SQLException {
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        int count = resultSet.getInt(1);
        resultSet.close();
        statement.close();
        return count;
    }

    public static boolean existenceQuery(PreparedStatement statement) throws SQLException {
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        int count = resultSet.getInt(1);
//        if (count > 0) {
//            System.out.println("Запись существует");
//        } else {
//            System.out.println("Запись не существует");
//        }
        resultSet.close();
        statement.close();
        return (count > 0);
    }

    public static PreparedStatement fillMovieRecordStatement(PreparedStatement statement, Movie movie) throws SQLException {
        statement.setString(1, movie.getName());
        statement.setInt(2, movie.getCoordinates().getCoordX());
        statement.setInt(3, movie.getCoordinates().getCoordY());
        statement.setLong(4, movie.getOscarsCount());
        statement.setLong(5, movie.getLength());
        if ((movie.getGenre() != null)) {
            statement.setString(6, movie.getGenre().toString());
        } else {
            statement.setNull(6, Types.VARCHAR);
        }
        if ((movie.getMpaaRating() != null)) {
            statement.setString(7, movie.getMpaaRating().toString());
        } else {
            statement.setNull(7, Types.VARCHAR);
        }
        statement.setString(8, movie.getOperator().getName());
        statement.setString(9, movie.getOperator().getPassportID());
        if ((movie.getOperator().getNationality() != null)) {
            statement.setString(10, movie.getOperator().getNationality().toString());
        } else {
            statement.setNull(10, Types.VARCHAR);
        }
        if ((movie.getOperator().getLocation().getLocX() != null)) {
            statement.setLong(11, Long.parseLong(movie.getOperator().getLocation().getLocX().toString()));
        } else {
            statement.setNull(11, Types.BIGINT);
        }
        if ((movie.getOperator().getLocation().getLocY() != null)) {
            statement.setLong(12, Long.parseLong(movie.getOperator().getLocation().getLocY().toString()));
        } else {
            statement.setNull(12, Types.BIGINT);
        }
        if ((movie.getOperator().getLocation().getLocZ() != null)) {
            statement.setDouble(13, Double.parseDouble(movie.getOperator().getLocation().getLocZ().toString()));
        } else {
            statement.setNull(13, Types.BIGINT);
        }
      return statement;
    }
}
