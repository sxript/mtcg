package helper;

import http.ContentType;
import http.HttpStatus;
import server.Response;

public class CommonErrors {
    public static Response TOKEN_ERROR = new Response(
            HttpStatus.UNAUTHORIZED,
            ContentType.JSON,
            "{ \"error\": \"Access token is missing or invalid\"}"
    );
}
