package string_utilities;
import lombok.experimental.UtilityClass;
import weather.WeatherRequest;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@UtilityClass
public class ParameterStringBuilder {

    public String getWeatherParamsString(WeatherRequest weatherRequest, String ACCESS_KEY) {
        return "current?access_key=" +
                ACCESS_KEY +
                "&query=" +
                URLEncoder.encode(weatherRequest.getQuery(), StandardCharsets.UTF_8);
    }
}