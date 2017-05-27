package com.arannolan.coolapp.resources;

import com.arannolan.coolapp.App;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.json.JsonObject;

import javax.ws.rs.Path;
import java.util.Arrays;
import java.util.List;

/**
 * Resource class to handle GET requests for information on users popularity.
 *
 * Path: '/popular'
 *
 * Query Parameters: 'access_token' -- Facebook API user access token corresponding to account being created
 *
 * Required permissions: 'user_friends'
 *
 * Responds with json of the form
 * { "is_popular": true/false
 *   "userId": ID of user
 * }
 * or an error response (see super class for description)
 * 
 */
@Path("popular")
public class PopularResource extends GetResourceBase {

    /**
     * Method to get user popularity information message.
     *
     * @param token Facebook user access token corresponding to user
     * @return Json object containing popularity information
     */
    @Override
    protected JsonObject processRequest(String token) {
        FacebookClient fbClient = new DefaultFacebookClient(token, App.GRAPH_API_VERSION);

        // retrieve friends details and get total number of friends
        JsonObject friends = fbClient.fetchObject("me/friends", JsonObject.class);
        int numberOfFriends = friends.getJsonObject("summary").getInt("total_count");
        // if more than 50 friends, then user is considered popular
        boolean isPopular = numberOfFriends > 50;

        JsonObject message = new JsonObject().put("is_popular", isPopular);
        return message;
    }

    /**
     * Method to return list of permissions required by user popularity GET request.
     *
     * @return List of required permissions
     */
    @Override
    protected List<String> requiredPermissions() {
        String[] permissions = {"user_friends"};
        return Arrays.asList(permissions);
    }
}
