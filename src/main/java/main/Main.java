package main;

import server.*;
import weather.WeatherServerHandler;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;

public class Main {

    public static void main(String[] args) {
        runServer();
    }

    public static void runServer() {
        try {
            AsynchronousServerSocketChannel serverSocket = AsynchronousServerSocketChannel.open().
                    bind(new InetSocketAddress(8080));
            
            ServerHandler serverHandler = new WeatherServerHandler(3);

            Server server = new Server(serverSocket, serverHandler);
            server.startServer();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}