package server;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;
import model.requests.JoinGameRequest;
import model.requests.LoginRequest;
import model.results.CreateGameResult;
import model.results.ListGamesResult;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

public class ServerFacade {
    private static final int TIMEOUT_LIMIT_MILLIS = 5000;

    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    /**
     * Executes the 'join' HTTP request on the server
     *
     * @param playerColor a TeamColor representing the color to be played
     * @param gameID      the database ID of the game to be joined
     * @param authData    an instance of AuthData
     * @throws IOException          for internal errors
     * @throws InterruptedException for internal errors
     * @throws URISyntaxException   for internal errors
     */
    public void joinGame(ChessGame.TeamColor playerColor, int gameID, AuthData authData) throws IOException, InterruptedException, URISyntaxException {
        HttpRequest request = buildRequest("/game",
                "PUT",
                new String[]{"authorization", authData.authToken()},
                new JoinGameRequest(authData.authToken(), playerColor, gameID));

        sendRequest(request);
    }

    /**
     * Sends a 'create' HTTP request to the server
     * @param name the name of the game to be joined
     * @param authData an instance of AuthData
     * @return the ID of the created game
     * @throws URISyntaxException for internal errors
     * @throws IOException for internal errors
     * @throws InterruptedException for internal errors
     */
    public int createGame(String name, AuthData authData) throws URISyntaxException, IOException, InterruptedException {
        record GameName(String gameName) {
        }

        HttpRequest request = buildRequest("/game",
                "POST",
                new String[]{"authorization", authData.authToken()},
                new GameName(name));
        HttpResponse<String> response = sendRequest(request);
        return new Gson().fromJson(response.body(), CreateGameResult.class).gameID();
    }

    /**
     * Sends a request to the HTTP server to list all games
     * @param authData an instance of AuthData
     * @return a Collection of GameData containing the information about all of the games currently on file
     * @throws URISyntaxException for internal errors
     * @throws IOException for internal errors
     * @throws InterruptedException for internal errors
     */
    public Collection<GameData> getGameList(AuthData authData) throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request = buildRequest("/game",
                "GET",
                new String[]{"authorization", authData.authToken()});
        HttpResponse<String> response = sendRequest(request);
        ListGamesResult result = new Gson().fromJson(response.body(), ListGamesResult.class);
        return result.games();
    }

    /**
     * Sends an HTTP request to the server to log a user out
     * @param authData the auth session to end
     * @throws IOException for internal errors
     * @throws InterruptedException for internal errors
     * @throws URISyntaxException for internal errors
     */
    public void logout(AuthData authData) throws IOException, InterruptedException, URISyntaxException {
        HttpRequest request = buildRequest("/session",
                "DELETE",
                new String[]{"authorization", authData.authToken()});
        sendRequest(request);
    }

    /**
     * Registers a new user in the database and logs the user in automatically
     * @param userData an instance of UserData
     * @return an instance of AuthData representing the new auth session created for the user
     * @throws URISyntaxException for internal errors
     * @throws IOException for internal errors
     * @throws InterruptedException for internal errors
     */
    public AuthData register(UserData userData) throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request = buildRequest("/user", "POST", userData);
        HttpResponse<String> response = sendRequest(request);

        return new Gson().fromJson(response.body(), AuthData.class);
    }

    /**
     * Starts a new auth session for a user
     * @param userData a LoginRequest (containing username and password)
     * @return an instance of AuthData
     * @throws URISyntaxException for internal errors
     * @throws IOException for internal errors
     * @throws InterruptedException for internal errors
     */
    public AuthData login(LoginRequest userData) throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request = buildRequest("/session", "POST", userData);
        HttpResponse<String> response = sendRequest(request);

        return new Gson().fromJson(response.body(), AuthData.class);
    }

    /**
     * Sends a DELETE HTTP request to the server and clears the database. Only used for testing purposes.
     * @throws URISyntaxException for internal errors
     * @throws IOException for internal errors
     * @throws InterruptedException for internal errors
     */
    public void clear() throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request = buildRequest("/db", "DELETE");
        sendRequest(request);
    }

    private HttpRequest buildRequest(String path, String type, String[] header, Object body) throws URISyntaxException {
        HttpRequest.Builder request = HttpRequest.newBuilder()
                .uri(new URI(serverUrl + path))
                .timeout(java.time.Duration.ofMillis(ServerFacade.TIMEOUT_LIMIT_MILLIS));

        request = addRequestHeader(request, header);

        HttpRequest.BodyPublisher preparedBody = makeRequestBody(body);

        request = switch (type) {
            case "POST" -> request.POST(preparedBody);
            case "GET" -> request.GET();
            case "DELETE" -> request.DELETE();
            case "PUT" -> request.PUT(preparedBody);
            default -> request;
        };

        return request.build();
    }

    private HttpRequest buildRequest(String path, String type, Object body) throws URISyntaxException {
        return buildRequest(path, type, new String[]{"", ""}, body);
    }

    private HttpRequest buildRequest(String path, String type, String[] header) throws URISyntaxException {
        return buildRequest(path, type, header, null);
    }

    private HttpRequest buildRequest(String path, String type) throws URISyntaxException {
        return buildRequest(path, type, new String[]{"", ""}, null);
    }

    private HttpRequest.Builder addRequestHeader(HttpRequest.Builder request, String[] header) {
        if (!header[0].isEmpty() && !header[1].isEmpty()) {
            request = request.header(header[0], header[1]);
        }
        return request;
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
            record ResponseBody(String message) {
            }
            ResponseBody message = new Gson().fromJson(response.body(), ResponseBody.class);
            throw new ResponseException(response.statusCode(), message.message());
        }
    }
}
