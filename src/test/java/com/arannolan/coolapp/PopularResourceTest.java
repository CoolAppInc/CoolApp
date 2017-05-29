package com.arannolan.coolapp;

import com.arannolan.coolapp.testutils.TestClient;
import com.arannolan.coolapp.testutils.TestUsers;
import com.arannolan.coolapp.utils.Error;
import com.restfb.json.JsonObject;

import org.junit.*;

import static org.junit.Assert.assertEquals;

/**
 * Tests for user popularity GET request.
 *
 * To be run as part of TestSuit
 */
public class PopularResourceTest {

    public static final String PATH = "popular";

    /**
     * Test user with no friends receives message with
     * { "is_popular": false
     *   "user_id: userId
     * }
     */
    @Test
    public void testNoFriends() {
        testFriends(TestUsers.TEST_USER_A, false);
    }

    /**
     * Test user with 50 friends message with
     * { "is_popular": false
     *   "user_id: userId
     * }
     * */
    @Test
    public void testFiftyFriends() {
        testFriends(TestUsers.TEST_USER_B, false);
    }

    /**
     * Test user with 51 friends message with
     * { "is_popular": true
     *   "user_id: userId
     * }
     */
    @Test
    public void testFiftyOneFriends() {
        testFriends(TestUsers.TEST_USER_C, true);
    }

    /**
     * Test access token with 'user_friends' permission missing receives 400 Bad Request
     *
     * Note: commented out as Facebook Graph API is being buggy and not allowing me to create a
     * test user without 'user_friends' permission.
     */
//    @Test
//    public void testMissingFriendsPermission() {
//        String accessToken = TestUsers.getAccessToken(TestUsers.TEST_USER_D);
//        TestClient client = TestClient.getInstance();
//
//        // Make request
//        JsonObject message = client.getRequest(PATH, accessToken).getJsonObject("error");
//
//        // Check error message
//        String errorMsg = message.getString("message");
//        assertEquals(Error.MISSING_PERMISSIONS, errorMsg);
//
//        // Check missing permissions
//        String permission = message.getJsonArray("permissions").getString(0);
//        assertEquals("user_friends", permission);
//    }

    /**
     * Test user popularity
     *
     * @param userId Id of user to be checked
     * @param isPopular Expected response
     */
    private void testFriends(String userId, boolean isPopular) {
        String accessToken = TestUsers.getAccessToken(userId);
        TestClient client = TestClient.getInstance();
        JsonObject message = client.getRequest(PATH, accessToken);

        assertEquals(isPopular, message.getBoolean("is_popular"));
        assertEquals(userId, message.getString("user_id"));
    }
}
