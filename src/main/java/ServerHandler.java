import java.io.IOException;
import java.io.PrintWriter;
interface ServerHandler {
    String getRequest() throws IOException;
    void startServer() throws IOException;
    void sendGetResponse(String request, PrintWriter output);
}