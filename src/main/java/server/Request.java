package server;

import http.Method;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;


@Getter
@Setter(AccessLevel.PROTECTED)
public class Request {
    private Method method;
    private String pathName;
    private String params;
    private List<String> pathParams;
    private String basePath;
    private String contentType;
    private Integer contentLength = 0;
    private String authorization;
    private String body = "";

    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private static final String CONTENT_TYPE = "Content-Type: ";
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private static final String CONTENT_LENGTH = "Content-Length: ";
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private static final String AUTHORIZATION = "Authorization: Bearer ";

    public Request(BufferedReader inputStream) {
        try {
            buildRequest(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void buildRequest(BufferedReader inputStream) throws IOException {
        try {
            String line = inputStream.readLine();

            if (line != null) {
                String[] splitFirstLine = line.split(" ");
                Boolean hasParams = splitFirstLine[1].contains("?");

                setMethod(getMethodFromInputLine(splitFirstLine));
                setPathName(getPathnameFromInputLine(splitFirstLine, hasParams));
                setParams(getParamsFromInputLine(splitFirstLine, hasParams));

                setPathParams(getPathParams(splitFirstLine[1]));
                setBasePath(getBasePath(splitFirstLine[1]));

                while (!line.isEmpty()) {
                    line = inputStream.readLine();
                    if (line.startsWith(CONTENT_LENGTH)) {
                        setContentLength(getContentLengthFromInputLine(line));
                    }
                    if (line.startsWith(CONTENT_TYPE)) {
                        setContentType(getContentTypeFromInputLine(line));
                    }
                    if (line.startsWith(AUTHORIZATION)) {
                        setAuthorization(getAuthorizationTokenFromInputLine(line));
                    }
                }

                if (getMethod() == Method.POST || getMethod() == Method.PUT) {
                    int asciiChar;
                    for (int i = 0; i < getContentLength(); i++) {
                        asciiChar = inputStream.read();
                        String body = getBody();
                        setBody(body + ((char) asciiChar));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private Method getMethodFromInputLine(String[] splitFirstLine) {
        return Method.valueOf(splitFirstLine[0].toUpperCase(Locale.ROOT));
    }

    // This only works for one pathParam
    // e.g  /users/:id  --> will return :id
    private List<String> getPathParams(String path) {
        if (path.startsWith("/")) path = path.substring(1);

        List<String> splitPaths = new ArrayList<>();
        Collections.addAll(splitPaths, path.split("/"));

        if (splitPaths.size() <= 1) return Collections.emptyList();

        splitPaths.remove(0);
        return splitPaths;
    }


    private String getBasePath(String path) {
        if (path.startsWith("/")) path = path.substring(1);

        String[] splitPaths = path.split("/");
        return "/" + splitPaths[0];
    }

    private String getPathnameFromInputLine(String[] splitFirstLine, Boolean hasParams) {
        String pathNameFiltered = splitFirstLine[1];

        if (hasParams) {
            pathNameFiltered = pathNameFiltered.split("\\?")[0];
        }

        if(pathNameFiltered.lastIndexOf("/") == pathNameFiltered.length() - 1) {
            pathNameFiltered = pathNameFiltered.substring(0, pathNameFiltered.length() - 1);
        }

        return pathNameFiltered;
    }


    private String getParamsFromInputLine(String[] splittedFirstLine, Boolean hasParams) {
        if (hasParams) {
            return splittedFirstLine[1].split("\\?")[1];
        }

        return "";
    }

    private Integer getContentLengthFromInputLine(String line) {
        return Integer.parseInt(line.substring(CONTENT_LENGTH.length()));
    }

    private String getContentTypeFromInputLine(String line) {
        return line.substring(CONTENT_TYPE.length());
    }

    private String getAuthorizationTokenFromInputLine(String line) {
        return line.substring(AUTHORIZATION.length());
    }
}
