package com.arannolan.coolapp.resources;

import com.arannolan.coolapp.App;
import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.json.JsonObject;

import javax.ws.rs.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Resource class to handle GET requests for information on users favourite place.
 *
 * Path: '/place'
 *
 * Query Parameters: 'access_token' -- Facebook API user access token corresponding to account being created
 *
 * Required permissions: 'user_tagged_places'
 *
 * Responds with json of the form
 * { "favourite_place": Location
 *   "is_valid": true/false       -- false if user has not been tagged anywhere
 *   "userId": ID of user
 * }
 * or an error response (see super class for description)
 *
 */
@Path("place")
public class PlaceResource extends GetResourceBase {

    /**
     * Method to fetch users favourite place.
     *
     * @param token Facebook user access token corresponding to user
     * @return Json object containing favourite place
     */
    @Override
    protected JsonObject processRequest(String token) {
        FacebookClient fbClient = new DefaultFacebookClient(token, App.GRAPH_API_VERSION);

        JsonObject result;
        // fetch music details and get oldest liked artist
        Connection<JsonObject> places = fbClient.fetchConnection("me/tagged_places", JsonObject.class);
        String favouritePlace = getFavouritePlace(places);

        JsonObject message;
        if (favouritePlace == null) {
            message = new JsonObject()
                    .put("favourite_place", JsonObject.NULL)
                    .put("is_valid", false);
        } else {
            message = new JsonObject()
                    .put("favourite_place", favouritePlace)
                    .put("is_valid", true);
        }
        return message;
    }

    /**
     * Method to return list of permissions required by user favourite place GET request.
     *
     * @return List of required permissions
     */
    @Override
    protected List<String> requiredPermissions() {
        String[] permissions = {"user_tagged_places"};
        return Arrays.asList(permissions);
    }

    /**
     * Get the location where user is tagged the most.
     * This is considered to be their favourite place.
     *
     * @param places Tagged places
     * @return Favourite place, or null if not tagged anywhere
     */
    private String getFavouritePlace(Connection<JsonObject> places) {
        Map<String, Integer> map = new HashMap<>();

        // count occurrences of each location
        for (JsonObject place: places.getData()) {
            String name = place.getJsonObject("place").getString("name");
            int count = map.getOrDefault(name, 0) + 1;
            map.put(name, count);
        }

        // find location that occurs the most
        String placeName = null;
        int maxCount = 0;
        for (Map.Entry<String, Integer> entry: map.entrySet()) {
            int count = entry.getValue();
            if (count > maxCount) {
                maxCount = count;
                placeName = entry.getKey();
            }
        }

        return placeName;
    }
}
