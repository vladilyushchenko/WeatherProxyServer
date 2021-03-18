package main;

import server.*;
import weather.WeatherServerHandler;
import java.io.IOException;
import java.net.ServerSocket;


public class Main {

    public static void main(String[] args) throws IOException {
        runServer();
    }

    public static void runServer() {
        try {
            ServerSocket serverSocket = new ServerSocket(8080);

            ServerHandler serverHandler = new WeatherServerHandler(10);

            Server server = new Server(serverSocket, serverHandler);
            server.startServer();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
