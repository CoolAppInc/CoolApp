package com.arannolan.coolapp;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.CreateTableResult;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;

/**
 * Singleton class for database access
 */
public class Database {

    private static Database instance = null;

    private final AmazonDynamoDB client;

    /**
     * Connect to and initialise DynamoDB
     *
     * private to prevent other classes from instantiating
     */
    private Database() {
        // connect to local DynamoDB
         client = AmazonDynamoDBClientBuilder.standard()
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
    }

    /**
     * Get instance of singleton database object.
     * @return Database object
     */
    public static Database getInstance () {
        if (instance == null) instance = new Database(); // use lazy loading
        return instance;
    }

    /**
     * Add a new user to the database
     * @param user object representing new user to be added to database
     */
    public void addUser(User user) {
        DynamoDBMapper mapper = new DynamoDBMapper(client);
        mapper.load(user);
    }
}
