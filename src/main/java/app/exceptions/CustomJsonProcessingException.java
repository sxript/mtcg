package app.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;

public class CustomJsonProcessingException extends JsonProcessingException {
    public CustomJsonProcessingException(String msg) {
        super(msg);
    }
}
