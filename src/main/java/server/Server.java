package server;

import http_utilities.RequestType;
import org.json.JSONObject;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    ServerSocket serverSocket;
    ServerHandler serverHandler;

    public Server(ServerSocket serverSocket, ServerHandler serverHandler) {
        this.serverSocket = serverSocket;
        this.serverHandler = serverHandler;
    }

    public void startServer() throws IOException {
        ExecutorService executorService = Executors.newFixedThreadPool(8);

        while (!serverSocket.isClosed()) {
            Socket socket = serverSocket.accept();
            executorService.submit(new ServerThread(socket,
                    new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8)),
                    new PrintWriter(socket.getOutputStream())));
        }
    }

    private class ServerThread implements Runnable {
        Socket socket;
        BufferedReader input;
        PrintWriter output;

        public ServerThread(Socket socket, BufferedReader input, PrintWriter output) {
            this.socket = socket;
            this.input = input;
            this.output = output;
        }

        @Override
        public void run() {
            try {
                String requestLine = input.readLine();

                JSONObject requestJSON = serverHandler.getRequestJSON(requestLine);

                JSONObject responseJSON = serverHandler.getResponse(requestJSON);

                if (!responseJSON.getString("requestType").equals(RequestType.GET_FAVICON.toString())) {
                    output.println(serverHandler.getStringResponseFromJSON(responseJSON));
                }

                output.flush();
                output.close();
                input.close();
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}