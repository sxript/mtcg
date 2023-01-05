package http;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public enum HttpStatus {
    OK(200, "OK"),
    CREATED(201, "CREATED"),
    NO_CONTENT(204, "NO CONTENT"),
    BAD_REQUEST(400, "BAD REQUEST"),
    UNAUTHORIZED(401, "UNAUTHORIZED"),
    FORBIDDEN(403, "FORBIDDEN"),
    NOT_FOUND(404, "Not Found"),
    CONFLICT(409, "RESOURCE ALREADY EXISTS"),
    GONE(410, "GONE"),
    INTERNAL_SERVER_ERROR(500, "INTERNAL SERVER ERROR");

    @Getter
    @Setter(AccessLevel.PRIVATE)
    private int code;
    @Getter
    @Setter(AccessLevel.PRIVATE)
    private String message;

    HttpStatus(int code, String message) {
        setCode(code);
        setMessage(message);
    }
}
