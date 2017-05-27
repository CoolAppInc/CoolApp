package com.arannolan.coolapp.utils;

import com.restfb.json.JsonObject;

/**
 * Util class for generating error messages.
 */
public class Error {
    public static final String USER_NOT_FOUND = "User does not exist";
    public static final String INVALID_TOKEN = "Malformed user access token";
    public static final String MISSING_PERMISSIONS = "Missing required permissions";
    public static final String MISSING_TOKEN = "Query parameter 'access_token' missing from request";

    /**
     * Generate an error with given message and additional information
     *
     * @param message Primary error message
     * @param additional Extra error information
     * @return Json object representing error message
     */
    public static JsonObject generate(String message, JsonObject additional) {
        if (additional == null) {
            additional = new JsonObject();
        }
        additional.put("message", message);
        JsonObject response = new JsonObject().put("error", additional);
        return response;
    }

}
