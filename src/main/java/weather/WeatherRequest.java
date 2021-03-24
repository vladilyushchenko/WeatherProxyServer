package weather;

import http_utilities.RequestType;
import lombok.Data;
import org.json.JSONObject;

@Data
public class WeatherRequest {

    private String query;
    private RequestType requestType;
    private String request;

    public WeatherRequest() { }

    public WeatherRequest(JSONObject jsonRequest) {
        this.query = jsonRequest.has("query") ? jsonRequest.getString("query") : null;
        this.requestType = jsonRequest.has("requestType") ?
                RequestType.valueOf(jsonRequest.getString("requestType")) : null;
        this.request = jsonRequest.has("request") ? jsonRequest.getString("request") : null;
    }

    public JSONObject toJSON() {
        JSONObject jsonWeather = new JSONObject();
        if (query != null) jsonWeather.put("query", query);
        if (requestType != null) jsonWeather.put("requestType", requestType.toString());
        if (request != null) jsonWeather.put("request", request);
        return jsonWeather;
    }
}
