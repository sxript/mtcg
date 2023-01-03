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

    public static Response INTERNAL_SERVER_ERROR = new Response(
            HttpStatus.INTERNAL_SERVER_ERROR,
            ContentType.JSON,
            "{ \"error\": \"Something went wrong\" }"
    );
}
