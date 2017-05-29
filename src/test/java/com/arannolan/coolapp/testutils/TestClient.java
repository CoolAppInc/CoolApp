package com.arannolan.coolapp.testutils;

import com.arannolan.coolapp.App;
import com.restfb.json.JsonObject;
import org.glassfish.grizzly.http.server.HttpServer;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

/**
 * Singleton class for use as a test client.
 */
public class TestClient {

    // Base URI the Grizzly HTTP test server will listen on
    public static final String BASE_URI = "http://localhost:8081/myapp/";

    private HttpServer server;
    private WebTarget target;

    private static TestClient instance = null;

    private TestClient() {
        // start grizzly server
        server = App.startServer(BASE_URI);

        // create the client
        Client c = ClientBuilder.newClient();
        target = c.target(BASE_URI);
    }

    /**
     * Get an instance of a test client.
     *
     * Will spawn a test server if instance does not yet exist
     *
     * @return Test client instance
     */
    public static TestClient getInstance() {
        if (instance == null) {
            instance = new TestClient();
        }
        return instance;
    }

    /**
     * Shutdown the test server, and clear instance of test client
     */
    public void stop() {
        server.shutdownNow();
        instance = null;
    }

    /**
     * Make GET request to test server.
     *
     * @param path Resource path
     * @param accessToken Query parameter 'access_token'
     * @return JsonObject response message
     */
    public JsonObject getRequest(String path, String accessToken) {
        String message;
        if (accessToken == null) {
            message = target.path(path).request().get(String.class);
        } else {
            message = target.path(path).queryParam("access_token", accessToken).request().get(String.class);
        }
        return new JsonObject(message);
    }

    /**
     * Make and check that GET request returns '400 Bad Response'
     *
     * @param path Resource path
     * @param accessToken Query parameter 'access_token'
     * @return True if '400 Bad Response' received, false otherwise
     */
    public boolean badGetRequest(String path, String accessToken) {
        try {
            getRequest(path, accessToken);
            return false;
        } catch (BadRequestException e) {
            return true;
        }
    }

    /**
     * Make POST request to test server.
     *
     * @param path Resource path
     * @param accessToken Query parameter 'access_token'
     * @return JsonObject response message
     */
    public JsonObject postRequest(String path, String accessToken) {
        String message;
        if (accessToken == null) {
            message = target.path(path).request().buildPost(null).invoke(String.class);
        } else {
            message = target.path(path).queryParam("access_token", accessToken)
                    .request().buildPost(null).invoke(String.class);
        }
        return new JsonObject(message);
    }

    /**
     * Make and check that POST request returns '400 Bad Response'
     *
     * @param path Resource path
     * @param accessToken Query parameter 'access_token'
     * @return True if '400 Bad Response' received, false otherwise
     */
    public boolean badPostRequest(String path, String accessToken) {
        try {
            postRequest(path, accessToken);
            return false;
        } catch (BadRequestException e) {
            return true;
        }
    }
}
