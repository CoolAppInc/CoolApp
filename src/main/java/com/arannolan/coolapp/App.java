package com.arannolan.coolapp;

import com.arannolan.coolapp.database.Database;
import com.restfb.Version;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;

/**
 * App class.
 *
 * Program entry point, and initialisation of Grizzly HTTP server and DynamoDB.
 */
public class App {
    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://localhost:8080/coolapp/";

    // Facebook App ID and Secret
    public static final String APP_ID = "1909908742603299";
    public static final String APP_SECRET = "f36f601c8b1c7d1c2fea77e5e6009dc1";
    public static final String APP_ACCESS_TOKEN = APP_ID + "|" + APP_SECRET;

    // Facebook Graph API version to target
    public static final Version GRAPH_API_VERSION = Version.VERSION_2_9;

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer(String baseUri) {
        // create a resource config that scans for JAX-RS resources and providers
        // in com.arannolan.coolapp package
        final ResourceConfig rc = new ResourceConfig().packages("com.arannolan.coolapp");

        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(baseUri), rc);
    }

    /**
     * App method.
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        // Connect to and initialise DynamoDB
        Database.getInstance();

        // create Grizzly server
        final HttpServer server = startServer(BASE_URI);
        System.out.println(String.format(
                "Jersey app started with base URI %s\n" +
                "WADL available at %sapplication.wadl\n" +
                "Hit enter to stop app...", BASE_URI, BASE_URI));
        System.in.read();
        server.shutdownNow();
    }
}

