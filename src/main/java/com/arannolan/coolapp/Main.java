package com.arannolan.coolapp;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.*;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;

/**
 * Main class.
 *
 * Program entry point, and initialisation of Grizzly HTTP server and DynamoDB.
 */
public class Main {
    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://localhost:8080/myapp/";

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer() {
        // create a resource config that scans for JAX-RS resources and providers
        // in com.arannolan.coolapp package
        final ResourceConfig rc = new ResourceConfig().packages("com.arannolan.coolapp");

        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    /**
     * Connect to and initialise local DynamoDB.
     * @return AmazonDynamoDB
     */
    public static AmazonDynamoDB initBD() {
        // connect to local DynamoDB
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:8000", "us-west-2"))
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials("", "")))
                .build();

        // check that User table exists, and create if not
        String tableName = "Users";
        if (!client.listTables().getTableNames().contains(tableName)) {
            try {
                System.out.println("Attempting to create User table; please wait...");
                // use DynamoDBMapper to create table from annotated User class
                DynamoDBMapper mapper = new DynamoDBMapper(client);
                CreateTableRequest req = mapper.generateCreateTableRequest(User.class);
                req.setProvisionedThroughput(new ProvisionedThroughput(10L, 10L));
                CreateTableResult result = client.createTable(req);
                System.out.println("Success.  Table status: " + result.getTableDescription().getTableStatus());

            } catch (Exception e) {
                System.err.println("Unable to create table: ");
                System.err.println(e.getMessage());
            }
        }

        return client;
    }

    /**
     * Main method.
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        final AmazonDynamoDB dynamoDB = initBD();
        final HttpServer server = startServer();
        System.out.println(String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));
        System.in.read();
        server.stop();
    }
}

