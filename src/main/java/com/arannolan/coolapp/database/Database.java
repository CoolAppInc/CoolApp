package com.arannolan.coolapp.database;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.DeleteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.CreateTableResult;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;

import java.util.Map;

/**
 * Singleton class for database access.
 */
public class Database {

    // Database settings:
    public static final String ENDPOINT = "http://localhost:8000";
    public static final String REGION = "us-west-2";
    public static final String ACCESS_KEY = "";
    public static final String SECRET_KEY = "";

    /**
     * User table
     * <p>
     * Fields:
     * String userId     -- primary key
     * String name
     */
    public static final String USER_TABLE_NAME = "Users";

    private static Database instance = null;
    private final AmazonDynamoDB client;

    /**
     * Connect to and initialise DynamoDB.
     */
    private Database(String endpoint, String region, String accessKey, String secretKey) {
        // connect to local DynamoDB
        client = AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, region))
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                .build();
        initTables();
    }

    /**
     * Check that the required tables exist in the database and create if not.
     */
    private void initTables() {
        // check that User table exists, and create if not
        if (!client.listTables().getTableNames().contains(USER_TABLE_NAME)) {
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
     *
     * @return database object
     */
    public static Database getInstance() {
        if (instance == null) {
            // create database with default settings
            instance = new Database(ENDPOINT, REGION, ACCESS_KEY, SECRET_KEY);
        }
        return instance;
    }

    /**
     * Add a new user to the database.
     *
     * @param user object representing new user to be added to database
     */
    public void addUser(User user) {
        DynamoDBMapper mapper = new DynamoDBMapper(client);
        mapper.save(user);
    }

    /**
     * Delete user with given Id
     *
     * @param userId Id of user to be deleted
     */
    public void deleteUser(String userId) {
        Table table = new DynamoDB(client).getTable(USER_TABLE_NAME);
        table.deleteItem("userId", userId);
    }

    /**
     * Check if user with this Id exists in the DB.
     *
     * @param userId
     * @return true if user with this Id exists in the DB, false otherwise
     */
    public boolean hasUser(String userId) {
        return getUser(userId) != null;
    }

    /**
     * Get a user item from the database.
     *
     * @param userId
     * @return User object representing user, or null if not found
     */
    public User getUser(String userId) {
        DynamoDBMapper mapper = new DynamoDBMapper(client);
        return mapper.load(User.class, userId);
    }

    /**
     * Create a database instance with given configuration.
     * Used for testing.
     *
     * @return database object with given config
     */
    public static Database create(String endpoint, String region, String accessKey, String secretKey) {
        instance = new Database(endpoint, region, accessKey, secretKey);
        return instance;
    }

    /**
     * Destroy the old database instance.
     * Used for testing.
     */
    public static void destroy() {
        instance = null;
    }
}
