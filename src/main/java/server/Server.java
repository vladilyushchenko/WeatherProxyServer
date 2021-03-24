package server;

import http_utilities.RequestType;
import org.json.JSONObject;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Server {
    AsynchronousServerSocketChannel serverSocket;
    ServerHandler serverHandler;

    public Server(AsynchronousServerSocketChannel serverSocket, ServerHandler serverHandler) {
        this.serverSocket = serverSocket;
        this.serverHandler = serverHandler;
    }

    public void startServer() throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(8);

        while (serverSocket.isOpen()) {
            Future<AsynchronousSocketChannel> futureSocket = serverSocket.accept();

            executorService.submit(new ServerThread(futureSocket.get()));
        }
    }

    private class ServerThread implements Runnable {
        AsynchronousSocketChannel socketChannel;

        public ServerThread(AsynchronousSocketChannel socketChannel) {
            this.socketChannel = socketChannel;
        }

        @Override
        public void run() {
            try {
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                Future<Integer> readRequest = socketChannel.read(buffer);
                readRequest.get();

                String requestString = new String(buffer.array(), StandardCharsets.UTF_8).trim();
                String requestLine = requestString.split("\r\n")[0];

                JSONObject requestJSON = serverHandler.getRequestJSON(requestLine);

                JSONObject responseJSON = serverHandler.getResponse(requestJSON);

                buffer.flip();
                if (!responseJSON.getString("requestType").equals(RequestType.GET_FAVICON.toString())) {
                    buffer = ByteBuffer.wrap(
                            serverHandler.
                            getStringResponseFromJSON(responseJSON).
                            getBytes(StandardCharsets.UTF_8)
                    );
                    Future<Integer> writeResult = socketChannel.write(buffer);
                    writeResult.get();
                }
                buffer.clear();
                socketChannel.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}