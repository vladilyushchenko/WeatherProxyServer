package weather;

import cache.Cache;
import http_utilities.HttpCodeTypes;
import server.ServerHandler;
import string_utilities.ParameterStringBuilder;
import http_utilities.RequestType;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherServerHandler implements ServerHandler {
    private final String ACCESS_KEY;
    private final String URL;
    private final String PROTOCOL_VERSION;
    private final Cache<String, JSONObject> cache;

    public WeatherServerHandler(int cashSize) {
        this.ACCESS_KEY = System.getenv("WEATHER_API_ACCESS_KEY");
        this.URL = System.getenv("WEATHER_URL");
        this.PROTOCOL_VERSION = System.getenv("PROTOCOL_VERSION");
        cache = new Cache<>(cashSize);
    }

    @Override
    public JSONObject getResponse(JSONObject jsonRequest) throws IOException {
        WeatherRequest weatherRequest = new WeatherRequest(jsonRequest);

        if (cache.contains(weatherRequest.getQuery())) {
            return cache.get(weatherRequest.getQuery());
        }

        if (weatherRequest.getRequestType().equals(RequestType.GET_FAVICON)) {
            return processFaviconResponse(weatherRequest);
        } else if (weatherRequest.getRequestType().equals(RequestType.GET)) {
            return processGetResponse(weatherRequest);
        } else {
            return processIncorrectAddress(weatherRequest);
        }
    }

    @Override
    public JSONObject getRequestJSON(String strRequest) {
        WeatherRequest weatherRequest = new WeatherRequest();

        try {
            String[] splitRequest = strRequest.split(" ");

            if (splitRequest[1].startsWith("/favicon")) {
                weatherRequest.setRequestType(RequestType.GET_FAVICON);
            } else if (splitRequest[0].equals("GET")) {
                weatherRequest.setRequestType(RequestType.GET);
                weatherRequest.setRequest(splitRequest[1]);
                String[] parameters = splitRequest[1].split("\\?");
                weatherRequest.setQuery(parameters[1].split("=")[1]);
            }
        } catch (Exception e) {
            e.printStackTrace();
            weatherRequest.setRequestType(RequestType.INCORRECT);
        }

        return weatherRequest.toJSON();
    }

    private JSONObject processFaviconResponse(WeatherRequest jsonRequest) {
        WeatherResponse weatherResponse = new WeatherResponse();
        weatherResponse.setRequestType(jsonRequest.getRequestType());
        weatherResponse.setProtocolVersion(PROTOCOL_VERSION);
        return weatherResponse.toJSON();
    }

    private JSONObject processGetResponse(WeatherRequest weatherRequest) throws IOException {
        String stringURL = URL + ParameterStringBuilder.getWeatherParamsString(weatherRequest, ACCESS_KEY);
        URL url = new URL(stringURL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        int responseCode = connection.getResponseCode();

        StringBuilder content;

        try (BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            content = new StringBuilder();
            while ((line = input.readLine()) != null) {
                content.append(line);
            }
        } finally {
            connection.disconnect();
        }

        WeatherResponse weatherResponse = new WeatherResponse();
        JSONObject jsonResponse = new JSONObject(content.toString());

        if (jsonResponse.has("location")) {
            weatherResponse.setQuery(jsonResponse.getJSONObject("location").getString("name"));
        }
        if (jsonResponse.has("current")) {
            weatherResponse.setObservationTime(jsonResponse.getJSONObject("current").getString("observation_time"));
            weatherResponse.setFeelsLike((int) jsonResponse.getJSONObject("current").get("feelslike"));
            weatherResponse.setTemperature((int) jsonResponse.getJSONObject("current").get("temperature"));
        }

        weatherResponse.setHttpCode(responseCode);
        weatherResponse.setProtocolVersion(PROTOCOL_VERSION);
        weatherResponse.setRequest(weatherRequest.getRequest());
        weatherResponse.setRequestType(jsonResponse.has("success") ? RequestType.INCORRECT :
                                                                            weatherRequest.getRequestType());

        cache.put(weatherRequest.getQuery(), weatherResponse.toJSON());

        return weatherResponse.toJSON();
    }

    private JSONObject processIncorrectAddress(WeatherRequest weatherRequest) {
        WeatherResponse weatherResponse = new WeatherResponse();

        weatherResponse.setHttpCode(404);
        weatherResponse.setRequestType(weatherRequest.getRequestType());
        weatherResponse.setProtocolVersion(PROTOCOL_VERSION);
        weatherResponse.setRequest(weatherRequest.getRequest());

        return weatherResponse.toJSON();
    }

    @Override
    public String getStringResponseFromJSON(JSONObject jsonResponse) {
        WeatherResponse weatherResponse = new WeatherResponse(jsonResponse);
        if (weatherResponse.getRequestType().equals(RequestType.INCORRECT) ||
                !HttpCodeTypes.isSuccessCode(weatherResponse.getHttpCode())) {
            return incorrectHtmlResponse(weatherResponse);
        }
        return String.format("""
                        %s %s
                        Content-Type: text/html; charset=utf-8
                                                
                        <p>
                            <h3>City</h3> : %s
                            <h3>Observation time</h3> : %s
                            <h3>Weather</h3>:
                            <h5>Feels like</h5> : %s
                            <h5>True temperature</h5> : %s
                        </p>
                        """,
                weatherResponse.getProtocolVersion(), weatherResponse.getHttpCode(),
                weatherResponse.getQuery(), weatherResponse.getObservationTime(),
                weatherResponse.getFeelsLike(), weatherResponse.getTemperature());
    }

    private String incorrectHtmlResponse(WeatherResponse weatherResponse) {
        return String.format("""
                %s %s
                Content-Type: text/html; charset=utf-8
                
                <p>
                    Your request "%s" is incorrect. Please type parameters as in following example:
                    http://localhost:8080/weather?city=Minsk
                </p>
                """,
                weatherResponse.getProtocolVersion(), weatherResponse.getHttpCode(),
                weatherResponse.getRequest());
    }
}