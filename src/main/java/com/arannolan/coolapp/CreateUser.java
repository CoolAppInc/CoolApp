package com.arannolan.coolapp;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Version;
import com.restfb.exception.FacebookOAuthException;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

/**
 * Root resource (exposed at "createUser" path)
 */
@Path("createUser")
public class CreateUser {

    /**
     * Method handling HTTP POST requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @POST
    public Response getIt(@QueryParam("userToken") String userToken) {

        if (userToken == null) {
            return Response.status(400).entity("Bad Request: Query parameter 'userToken' missing").build();
        }

        FacebookClient fbClient = new DefaultFacebookClient(userToken, Version.VERSION_2_8);
        User newUser = new User();
        try {
            com.restfb.types.User fbUser = fbClient.fetchObject("me", com.restfb.types.User.class);

            // create new DB user from facebook user information
            newUser.setUserId(Long.parseUnsignedLong(fbUser.getId()));
            newUser.setFirstName(fbUser.getFirstName());
            newUser.setLastName(fbUser.getLastName());
            Database.getInstance().addUser(newUser);

        } catch (FacebookOAuthException e) {
            return Response.status(400).entity("Bad Request: " + e.getErrorMessage()).build();
        }

        return Response.status(201).entity("User created: " + Long.toUnsignedString(newUser.getUserId())).build();
    }
}
