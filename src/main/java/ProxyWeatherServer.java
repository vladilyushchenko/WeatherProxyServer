import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ProxyWeatherServer implements ServerHandler {
    ServerSocket serverSocket;
    BufferedReader input;
    PrintWriter output;

    public ProxyWeatherServer(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    @Override
    public void startServer() throws IOException {
        while (true) {
            Socket socket = serverSocket.accept();
            this.input = new BufferedReader(new InputStreamReader(socket.getInputStream(),
                    StandardCharsets.UTF_8));
            this.output = new PrintWriter(socket.getOutputStream());

            String request = getRequest();

            if (!(request.split("/")[1]).equals("favicon.ico") &&
                    request.startsWith("GET")) {
                PrintWriter output = new PrintWriter(socket.getOutputStream());
                sendGetResponse(request, output);
            }

            output.close();
            input.close();
        }
    }

    @Override
    public String getRequest() throws IOException {
        return input.readLine();
    }

    @Override
    public void sendGetResponse(String request, PrintWriter output) {
        output.println("HTTP/1.1 200 OK");
        output.println("Content-Type: text/html; charset=utf-8");
        output.println();
        output.println();
        output.printf("<p>Your request : %s</p>", request);
        output.flush();
    }
}