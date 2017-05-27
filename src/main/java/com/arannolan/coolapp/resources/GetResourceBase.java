package com.arannolan.coolapp.resources;

import com.arannolan.coolapp.App;
import com.arannolan.coolapp.database.Database;
import com.arannolan.coolapp.utils.Error;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.exception.FacebookGraphException;
import com.restfb.json.JsonArray;
import com.restfb.json.JsonObject;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Abstract class to act as base for other user information GET requests.
 *
 * Query Parameter: accessToken -- Facebook API user access token corresponding to account being created
 *
 * Responds with json of the form
 * { [requested information]
 *   "userId": ID of user
 * }
 *
 * or an error
 * { "error": {
 *     "message": error message,
 *     [additional fields]
 *   }
 * }
 *
 * additional error fields:
 *   "permissions"  -- required permissions missing from access token
 *   "code"         -- error code from Facebook Graph API error message
 *
 */
public abstract class GetResourceBase {
    /**
     * Method to handle GET request for user information.
     *
     * Provides general error handling for user information requests.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getIt(@QueryParam("accessToken") String token) {

        int statusCode;
        JsonObject message;

        try {
            // debug token to validate user access token
            FacebookClient fbClient = new DefaultFacebookClient(App.APP_ACCESS_TOKEN, App.GRAPH_API_VERSION);
            FacebookClient.DebugTokenInfo debugToken = fbClient.debugToken(token);

            // get userId corresponding to userToken
            String userId = debugToken.getUserId();

            // get list of permissions this token has
            List<String> permissions = debugToken.getScopes();

            if (userId == null) {
                // malformed user access token
                statusCode = 400;
                message = Error.generate(Error.INVALID_TOKEN, null);

            } else if (!Database.getInstance().hasUser(userId)) {
                // user with this Id does not exist in database
                statusCode = 400;
                message = Error.generate(Error.USER_NOT_FOUND, null);

            } else if (!permissions.containsAll(requiredPermissions())) {
                // user access token is missing some required permissions
                List<String> missingPermissions = requiredPermissions();
                missingPermissions.removeAll(permissions);

                statusCode = 400;
                message = Error.generate(Error.MISSING_PERMISSIONS,
                        new JsonObject().put("permissions", new JsonArray(missingPermissions)));

            } else {
                statusCode = 200;
                message = processRequest(token).put("userId", userId);

            }

        } catch (FacebookGraphException e) {
            // handle facebook graph api call errors
            statusCode = e.getHttpStatusCode();
            message = Error.generate(e.getErrorMessage(), new JsonObject().put("code", e.getErrorCode()));
        }

        return Response.status(statusCode).entity(message.toString()).build();
    }

    /**
     * Method to create the user information response message for the GET request
     *
     * @param token Facebook user access token corresponding to user
     * @return Information response message in as JsonObject
     */
    protected abstract JsonObject processRequest(String token);

    /**
     * Method to return list of permissions required by the user information GET request
     *
     * @return List of required permissions
     */
    protected abstract List<String> requiredPermissions();
}
