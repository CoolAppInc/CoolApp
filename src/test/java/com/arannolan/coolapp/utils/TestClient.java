package com.arannolan.coolapp.utils;

import com.arannolan.coolapp.App;
import com.arannolan.coolapp.TestSuite;
import com.restfb.json.JsonObject;
import org.glassfish.grizzly.http.server.HttpServer;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

/**
 * Created by aran on 28/05/17.
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

    public static TestClient getInstance() {
        if (instance == null) {
            instance = new TestClient();
        }
        return instance;
    }

    public void stop() {
        server.shutdownNow();
    }

    public JsonObject request(String path, String accessToken) {
        String message;
        if (accessToken == null) {
            message = target.path(path).request().get(String.class);
        } else {
            message = target.path(path).queryParam("access_token", accessToken).request().get(String.class);
        }
        return new JsonObject(message);
    }

    public boolean badRequestCheck(String path, String accessToken) {
        try {
            request(path, accessToken);
            return false;
        } catch (BadRequestException e) {
            return true;
        }
    }
}
