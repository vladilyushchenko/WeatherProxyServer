package server;
import org.json.JSONObject;
import java.io.IOException;

public interface ServerHandler {
    JSONObject getResponse(JSONObject request) throws IOException;

    JSONObject getRequestJSON(String request);

    String getStringResponseFromJSON(JSONObject jsonResponse);
}