package auxiliary_classes;

import java.io.Serializable;
import java.sql.ResultSet;

public class ResultSetWrapper implements Serializable {
    private transient ResultSet resultSet;

    public ResultSetWrapper(ResultSet resultSet) {
        this.resultSet = resultSet;
    }

    public ResultSet getResultSet() {
        return resultSet;
    }
}
