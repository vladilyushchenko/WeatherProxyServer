import java.io.*;
import java.net.Socket;
import java.util.Random;

public class IntegrationTests {
    private static class RequestThread extends Thread {
        private final String request;
        private final String hostname;
        private String response;
        private final int port;

        public RequestThread(String hostname, int port, String request) {
            this.request = request;
            this.hostname = hostname;
            this.port = port;
        }

        @Override
        public void run() {
            try {
                testServersSendsCorrectResponse();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void testServersSendsCorrectResponse() throws IOException {
            try (Socket socket = new Socket(hostname, port)) {
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                writer.println(request);

                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                String line;
                StringBuilder responseBuilder = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    responseBuilder.append(line);
                    responseBuilder.append("\n");
                }
                writer.close();
                reader.close();

                response = responseBuilder.toString();
            }
        }

        public String getResponse() {
            return response;
        }
    }

    private static final Random rand = new Random(System.currentTimeMillis());

    public static void main(String[] args) throws IOException, InterruptedException {
        RequestThread[] threads = new RequestThread[10];

        for (int i = 0; i < 10; i++) {
            threads[i] = new RequestThread("localhost", 8080, generateWeatherRequest());
            threads[i].start();
        }

        for (int i = 0; i < 10; i++) {
            threads[i].join();
            System.out.println(threads[i].getResponse());
        }

    }

    private static final String[] CITIES = {
            "Minsk", "Gorodok", "Odessa", "Kiev", "New York", "Soligorsk"
    };

    private static String generateWeatherRequest() {
        return String.format("GET /weather?city=%s", CITIES[Math.abs(rand.nextInt() % CITIES.length)]);
    }
}
