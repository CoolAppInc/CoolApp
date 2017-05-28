package com.arannolan.coolapp;

import com.arannolan.coolapp.utils.TestClient;
import com.arannolan.coolapp.utils.TestUsers;
import com.restfb.json.JsonObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for user favourite band GET request.
 */
public class BandResourceTest {

    public static final String PATH = "band";

    /**
     * Test user with no liked bands receives
     * { 'favourite_band': null
     *   'is_valid': false
     *   'user_id': userId
     * }
     */
    @Test
    public void testNoLikedBands() {
        testFavouriteBand(TestUsers.TEST_USER_A, null);
    }

    /**
     * Test user with 50 friends receives
     * { 'favourite_band': 'Queen'
     *   'is_valid': false
     *   'user_id': userId
     * }
     */
    @Test
    public void testLikedBands() {
        testFavouriteBand(TestUsers.PUBILC_USER_ID, "Queen");
    }

    /**
     * Test access token with 'user_likes' permission missing receives 400 Bad Request
     */
    @Test
    public void testMissingLikesPermission() {
        String accessToken = TestUsers.getAccessToken(TestUsers.TEST_USER_D);
        TestClient client = TestClient.getInstance();

        assertEquals(true, client.badRequestCheck(PATH, accessToken));
    }

    /**
     * Test user favourite band
     *
     * @param userId Id of user to be checked
     * @param bandName Expected favourite band, or null if expecting 'is_valid': false
     */
    private void testFavouriteBand(String userId, String bandName) {
        String accessToken = TestUsers.getAccessToken(userId);
        TestClient client = TestClient.getInstance();
        JsonObject message = client.request(PATH, accessToken);

        boolean isValid = bandName != null;

        assertEquals(isValid ? bandName : "null", message.getString("favourite_band"));
        assertEquals(isValid, message.getBoolean("is_valid"));
        assertEquals(userId, message.getString("user_id"));
    }
}
