import java.io.IOException;
import java.net.ServerSocket;

public class Main {
    public static void main(String[] args) throws IOException {
        try {
            ServerSocket serverSocket = new ServerSocket(8080);

            ProxyWeatherServer server = new ProxyWeatherServer(serverSocket);
            server.startServer();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
