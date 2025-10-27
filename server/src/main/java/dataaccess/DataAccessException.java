package dataaccess;

import java.sql.SQLException;

public class DataAccessException extends RuntimeException {
    public DataAccessException(String message, SQLException ex) {
        super(message + "\n" + ex.getMessage());
    }

    public DataAccessException(String message) {
        super(message);
    }
}
