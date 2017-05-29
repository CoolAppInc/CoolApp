package com.arannolan.coolapp.database;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

/**
 * Class to represent a User in DynamoDB.
 */
@DynamoDBTable(tableName = "Users")
public class User {

    private String userId; // facebook user ID
    private String name;

    @DynamoDBHashKey
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    @DynamoDBAttribute
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    /**
     * Parameter-less constructor needed for mapper
     */
    public User() {}

    /**
     * Convenient User object constructor
     */
    public User(String userId, String name) {
        this.userId = userId;
        this.name = name;
    }
}
