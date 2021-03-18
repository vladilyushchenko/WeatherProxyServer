package string_utilities;
import weather.WeatherRequest;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class ParameterStringBuilder {
        /*
        http://api.weatherstack.com/current?access_key=YOUR_ACCESS_KEY&query=New York
        */
    public static String getWeatherParamsString(WeatherRequest weatherRequest, String ACCESS_KEY) {
        StringBuilder result = new StringBuilder();
        result.append("current?access_key=");
        result.append(ACCESS_KEY);
        result.append("&query=");
        result.append(URLEncoder.encode(weatherRequest.getQuery(), StandardCharsets.UTF_8));
        return result.toString();
    }
}