package server;

import com.google.gson.Gson;
import model.AuthData;
import model.UserData;
import model.requests.LoginRequest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class ServerFacade {
    private static final int TIMEOUT_LIMIT_MILLIS = 5000;

    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public AuthData register(UserData userData) throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(serverUrl + "/user"))
                .timeout(java.time.Duration.ofMillis(ServerFacade.TIMEOUT_LIMIT_MILLIS))
                .POST(makeRequestBody(userData))
                .build();

        HttpResponse<String> response = sendRequest(request);

        return new Gson().fromJson(response.body(), AuthData.class);
    }

    public AuthData login(LoginRequest userData) throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(serverUrl + "/session"))
                .timeout(java.time.Duration.ofMillis(ServerFacade.TIMEOUT_LIMIT_MILLIS))
                .POST(makeRequestBody(userData))
                .build();

        HttpResponse<String> response = sendRequest(request);

        return new Gson().fromJson(response.body(), AuthData.class);
    }

    public void clear() throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(serverUrl + "/db"))
                .timeout(java.time.Duration.ofMillis(TIMEOUT_LIMIT_MILLIS))
                .DELETE()
                .build();

        sendRequest(request);
    }

    private HttpRequest.BodyPublisher makeRequestBody(Object body) {
        if (body != null) {
            return HttpRequest.BodyPublishers.ofString(new Gson().toJson(body), StandardCharsets.UTF_8);
        } else {
            return HttpRequest.BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws ResponseException, IOException, InterruptedException {
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return response;
        } else {
            throw new ResponseException(response.statusCode(), response.body());
        }
    }
}
