package com.arannolan.coolapp.resources;

import com.restfb.*;
import com.restfb.json.JsonObject;

import javax.ws.rs.Path;
import java.util.Arrays;
import java.util.List;

/**
 * Resource class to handle GET requests for information on users favourite band.
 *
 * Path: '/band'
 *
 * Query Parameters: 'access_token' -- Facebook API user access token corresponding to account being created
 *
 * Required permissions: 'user_likes'
 *
 * Responds with json of the form
 * { "favourite_band": Band
 *   "is_valid": true/false       -- false if user has no favourite band
 *   "userId": ID of user
 * }
 * or an error response (see super class for description)
 *
 */
@Path("band")
public class BandResource extends GetResourceBase {

    /**
     * Method to fetch users favourite band.
     *
     * @param token Facebook user access token corresponding to user
     * @return Json object containing favourite band
     */
    @Override
    protected JsonObject processRequest(String token) {
        FacebookClient fbClient = new DefaultFacebookClient(token, Version.VERSION_2_8);

        // fetch music details and get band which was liked longest ago
        Connection<JsonObject> bands = fbClient.fetchConnection("me/music", JsonObject.class, Parameter.with("date_format", "U"));
        String favouriteBand = getFavouriteBand(bands);

        JsonObject message;
        if (favouriteBand == null) {
            message = new JsonObject()
                    .put("favourite_band", JsonObject.NULL)
                    .put("is_valid", false);
        } else {
            message = new JsonObject()
                    .put("favourite_band", favouriteBand)
                    .put("is_valid", true);
        }
        return message;
    }

    /**
     * Method to return list of permissions required by user favourite band GET request.
     *
     * @return List of required permissions
     */
    @Override
    protected List<String> requiredPermissions() {
        String[] permissions = {"user_likes"};
        return Arrays.asList(permissions);
    }

    /**
     * Get the name of the band which was liked longest ago.
     * This is considered to be the users favourite band.
     *
     * @param bands Liked bands
     * @return name of favourite band, or null if no liked bands
     */
    private String getFavouriteBand(Connection<JsonObject> bands) {
        String bandName = null;
        long minTime = Long.MAX_VALUE;

        for (JsonObject band: bands.getData()) {
            long time = band.getLong("created_time");

            if (time < minTime) {
                minTime = time;
                bandName = band.getString("name");
            }
        }

        return bandName;
    }
}
