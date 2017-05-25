package com.arannolan.coolapp;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

/**
 * Class to represent a User in DynamoDB.
 */
@DynamoDBTable(tableName = "Users")
public class User {

    private long userId; // facebook user ID
    private String firstName;
    private String lastName;

    @DynamoDBHashKey
    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    @DynamoDBAttribute
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    @DynamoDBAttribute
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

}
