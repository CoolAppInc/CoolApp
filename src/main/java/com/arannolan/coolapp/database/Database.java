package com.arannolan.coolapp.database;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.CreateTableResult;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;

/**
 * Singleton class for database access.
 */
public class Database {

    public static final String ENDPOINT = "http://localhost:8000";
    public static final String REGION = "us-west-2";

    public static final String ACCESS_KEY = "";
    public static final String SECRET_KEY = "";

    public static final String USER_TABLE_NAME = "Users";

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
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(ENDPOINT, REGION))
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY)))
                .build();

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
     * @return database object
     */
    public static Database getInstance () {
        if (instance == null) instance = new Database(); // use lazy loading
        return instance;
    }

    /**
     * Add a new user to the database.
     * @param user object representing new user to be added to database
     */
    public void addUser(User user) {
        DynamoDBMapper mapper = new DynamoDBMapper(client);
        mapper.save(user);
    }

    /**
     * Check if user with this Id exists in the DB.
     * @param userId
     * @return true if user with this Id exists in the DB, false otherwise
     */
    public boolean hasUser(String userId) {
        Table table = new DynamoDB(client).getTable(USER_TABLE_NAME);
        // try find user with given Id, item is null if not found
        Item item = table.getItem("userId", userId);
        return item != null;
    }
}
