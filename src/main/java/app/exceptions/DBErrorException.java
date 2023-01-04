package app.exceptions;

import java.sql.SQLException;

public class DBErrorException extends SQLException {
    public DBErrorException(String error) {
        super(error);
    }
}
