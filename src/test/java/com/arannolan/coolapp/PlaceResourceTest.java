package com.arannolan.coolapp;

import com.arannolan.coolapp.testutils.TestClient;
import com.arannolan.coolapp.testutils.TestUsers;
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
     * Test access token with 'user_tagged_places' permission missing receives 400 Bad Request
     */
    @Test
    public void testMissingTaggedPlacesPermission() {
        String accessToken = TestUsers.getAccessToken(TestUsers.TEST_USER_D);
        TestClient client = TestClient.getInstance();

        assertEquals(true, client.badGetRequest(PATH, accessToken));
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
