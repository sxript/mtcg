package app.exceptions;

public class InvalidDeckException extends Exception {
    public InvalidDeckException(String error) {
        super(error);
    }
}
