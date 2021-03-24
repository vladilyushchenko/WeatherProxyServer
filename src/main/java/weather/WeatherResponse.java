package weather;

import http_utilities.RequestType;
import lombok.Data;
import org.json.JSONObject;

@Data
public class WeatherResponse {
    private String query = null;
    private String observationTime = null;
    private int feelsLike = Integer.MIN_VALUE;
    private int temperature = Integer.MIN_VALUE;
    private String protocolVersion = null;
    private String request = null;
    private int httpCode = 0;
    private RequestType requestType = null;

    public WeatherResponse(JSONObject jsonResponse) {
        if (jsonResponse.has("query")) this.query = jsonResponse.getString("query");
        if (jsonResponse.has("observationTime")) this.observationTime = jsonResponse.
                                                                            getString("observationTime");
        if (jsonResponse.has("feelslike")) this.feelsLike = (int) jsonResponse.get("feelslike");
        if (jsonResponse.has("temperature")) this.temperature = (int) jsonResponse.get("temperature");
        if (jsonResponse.has("protocolVersion")) this.protocolVersion = jsonResponse.
                                                                            getString("protocolVersion");
        if (jsonResponse.has("request")) this.request = jsonResponse.getString("request");
        if (jsonResponse.has("code")) this.httpCode = (int) jsonResponse.get("code");
        if (jsonResponse.has("requestType")) this.requestType = RequestType.valueOf(jsonResponse.
                                                                            getString("requestType"));
    }

    public WeatherResponse() {}

    public JSONObject toJSON() {
        JSONObject jsonResponse = new JSONObject();
        if (query != null) jsonResponse.put("query", query);
        if (observationTime != null) jsonResponse.put("observationTime", observationTime);
        if (feelsLike != Integer.MIN_VALUE) jsonResponse.put("feelslike", feelsLike);
        if (temperature != Integer.MIN_VALUE) jsonResponse.put("temperature", temperature);
        if (protocolVersion != null) jsonResponse.put("protocolVersion", protocolVersion);
        if (request != null) jsonResponse.put("request", request);
        if (httpCode != 0) jsonResponse.put("code", httpCode);
        if (requestType != null) jsonResponse.put("requestType", requestType.toString());
        return jsonResponse;
    }
}
