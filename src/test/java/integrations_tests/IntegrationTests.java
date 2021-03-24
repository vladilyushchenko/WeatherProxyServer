package integrations_tests;

import org.junit.Assert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class IntegrationTests {
    private static final String incorrectResponse = "Please type parameters";
    private static final String hostname = "localhost";
    private static final int port = 8080;

    public static void main(String[] args) throws InterruptedException {
        testServerResponsesWeatherAboutCity();
        testServerShowsInfoAboutIncorrectRequest();
    }

    private static void testServerShowsInfoAboutIncorrectRequest() throws InterruptedException {
        RequestThread requestThread = new RequestThread(hostname, port,
                "GET /???????? HTTP/ 1.1", "Minsk");
        requestThread.start();
        requestThread.join();

        Assert.assertTrue(requestThread.response.contains(incorrectResponse));
    }

    private static void testServerResponsesWeatherAboutCity() throws InterruptedException {
        RequestThread[] threads = new RequestThread[UNIQUE_CITIES.length];

        for (int i = 0; i < UNIQUE_CITIES.length; i++) {
            threads[i] = new RequestThread(hostname, port, generateWeatherRequest(UNIQUE_CITIES[i]),
                    UNIQUE_CITIES[i]);
            threads[i].start();
        }

        for (int i = 0; i < UNIQUE_CITIES.length; i++) {
            threads[i].join();
            Assert.assertTrue(threads[i].isCityResponseCorrect());
        }
    }

    private static class RequestThread extends Thread {
        private final String request;
        private final String hostname;
        private String response;
        private final int port;
        private final String city;

        public RequestThread(String hostname, int port, String request, String city) {
            this.request = request;
            this.hostname = hostname;
            this.port = port;
            this.city = city;
        }

        @Override
        public void run() {
            try {
                testServerResponsesWeatherAboutCity();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void testServerResponsesWeatherAboutCity() throws IOException {
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

        public boolean isCityResponseCorrect() {
            return response.contains("<h3>City</h3> : " + city);
        }
    }

    private static final String[] UNIQUE_CITIES = {
            "Minsk", "Gorodok", "Odessa",
            "Kiev", "Soligorsk", "Moscow"
    };

    private static String generateWeatherRequest(String city) {
        return ("GET /weather?city=" + city + " HTTP/ 1.1");
    }
}
