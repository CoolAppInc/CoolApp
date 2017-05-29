package com.arannolan.coolapp;

import com.arannolan.coolapp.testutils.TestClient;
import com.arannolan.coolapp.testutils.TestUsers;
import com.arannolan.coolapp.utils.Error;
import com.restfb.json.JsonObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for user favourite place GET request.
 *
 * To be run as part of TestSuit
 */
public class PlaceResourceTest {

    public static final String PATH = "place";

    /**
     * Test user not tagged anywhere receives
     * { 'favourite_place': null
     *   'is_valid': false
     *   'user_id': userId
     * }
     */
    @Test
    public void testNoTaggedPlaces() {
        testFavouritePlace(TestUsers.TEST_USER_A, null);
    }

    /**
     * Test user who is tagged receives
     * { 'favourite_place': 'Wicklow, Ireland'
     *   'is_valid': true
     *   'user_id': userId
     * }
     */
    @Test
    public void testLikedBands() {
        testFavouritePlace(TestUsers.TEST_USER_C, "Wicklow, Ireland");
    }

    /**
     * Test access token with 'user_tagged_places' permission missing.
     */
    @Test
    public void testMissingTaggedPlacesPermission() {
        String accessToken = TestUsers.getAccessToken(TestUsers.TEST_USER_D);
        TestClient client = TestClient.getInstance();

        // Make request
        JsonObject message = client.getRequest(PATH, accessToken).getJsonObject("error");

        // Check error message
        String errorMsg = message.getString("message");
        assertEquals(Error.MISSING_PERMISSIONS, errorMsg);

        // Check missing permissions
        String permission = message.getJsonArray("permissions").getString(0);
        assertEquals("user_tagged_places", permission);
    }

    /**
     * Test user favourite place
     *
     * @param userId Id of user to be checked
     * @param placeName Expected favourite place, or null if expecting 'is_valid': false
     */
    private void testFavouritePlace(String userId, String placeName) {
        String accessToken = TestUsers.getAccessToken(userId);
        TestClient client = TestClient.getInstance();
        JsonObject message = client.getRequest(PATH, accessToken);

        boolean isValid = placeName != null;

        assertEquals(isValid ? placeName : "null", message.getString("favourite_place"));
        assertEquals(isValid, message.getBoolean("is_valid"));
        assertEquals(userId, message.getString("user_id"));
    }
}
