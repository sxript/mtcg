package server;

import app.App;
import http.ContentType;
import http.HttpStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@Getter(AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE)
public class ClientHandler implements Runnable {
    private PrintWriter outputStream;
    private BufferedReader inputStream;
    private Request request;
    private Response response;
    private App app;
    Socket clientSocket;

    public ClientHandler(Socket socket, App app) {
        setClientSocket(socket);
        setApp(app);
    }

    @Override
    public void run() {
        try {
            setInputStream(new BufferedReader(new InputStreamReader(clientSocket.getInputStream())));
            setRequest(new Request(getInputStream()));
            setOutputStream(new PrintWriter(clientSocket.getOutputStream(), true));

            if (request.getPathName() == null) {
                setResponse(new Response(
                        HttpStatus.BAD_REQUEST,
                        ContentType.TEXT,
                        ""
                ));
            } else {
                setResponse(getApp().handleRequest(request));
            }

            getOutputStream().write(getResponse().build());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (getOutputStream() != null) {
                    getOutputStream().close();
                }
                if (getInputStream() != null) {
                    getInputStream().close();
                    getClientSocket().close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
