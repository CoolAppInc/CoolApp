package com.arannolan.coolapp.resources;

import com.arannolan.coolapp.App;
import com.arannolan.coolapp.database.Database;
import com.arannolan.coolapp.database.User;
import com.arannolan.coolapp.utils.Error;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.exception.FacebookGraphException;
import com.restfb.json.JsonObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Resource class to handle POST requests to create a new user.
 *
 * Query Parameters: 'access_token' -- Facebook API user access token corresponding to account being created
 *
 * Responds with JSON of the form
 * { "message": "User created"
 *   "user_id": ID of new user
 * }
 *
 * or an error
 * { "error": {
 *     "message": error message,
 *     [additional fields]
 *   }
 * }
 *
 */
@Path("createUser")
public class CreateUserResource {

    /**
     * Method handling HTTP POST requests for user creation.
     *
     * @return Responds with a JSON message
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response getIt(@QueryParam("access_token") String token) {

        int statusCode;
        JsonObject message;

        // ensure that accessToken query string was included
        if (token == null) {
            statusCode = 400;
            message = Error.generate(Error.MISSING_TOKEN, null);
        } else {
            try {
                // debug token to validate user access token
                FacebookClient fbAppClient = new DefaultFacebookClient(App.APP_ACCESS_TOKEN, App.GRAPH_API_VERSION);
                FacebookClient.DebugTokenInfo debugToken = fbAppClient.debugToken(token);

                // check that the debugToken indicates a valid user access token
                if (debugToken.isValid() && debugToken.getUserId() != null) {
                    // get user information from facebook api
                    FacebookClient fbUserClient = new DefaultFacebookClient(token, App.GRAPH_API_VERSION);
                    com.restfb.types.User fbUser = fbUserClient.fetchObject("me", com.restfb.types.User.class);

                    // create new DB user from facebook user information
                    User newUser = new User(fbUser.getId(), fbUser.getFirstName(), fbUser.getLastName());
                    Database.getInstance().addUser(newUser);

                    // respond with user created if successful
                    statusCode = 201;
                    message = new JsonObject()
                            .put("message", "User created")
                            .put("user_id", newUser.getUserId());
                } else {
                    // handle invalid user access token
                    statusCode = 400;
                    message = Error.generate(Error.INVALID_TOKEN, null);
                }

            } catch (FacebookGraphException e) {
                // handle facebook api call errors
                statusCode = e.getHttpStatusCode();
                message = Error.generate(e.getErrorMessage(), new JsonObject().put("code", e.getErrorCode()));
            }
        }

        return Response
                .status(statusCode)
                .entity(message.toString())
                .build();
    }

}
