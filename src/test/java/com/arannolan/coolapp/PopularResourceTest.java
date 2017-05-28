package com.arannolan.coolapp;

import com.arannolan.coolapp.testutils.TestClient;
import com.arannolan.coolapp.testutils.TestUsers;
import com.restfb.json.JsonObject;

import org.junit.*;

import static org.junit.Assert.assertEquals;

/**
 * Tests for user popularity GET request.
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
     */
    @Test
    public void testMissingFriendsPermission() {
        String accessToken = TestUsers.getAccessToken(TestUsers.TEST_USER_D);
        TestClient client = TestClient.getInstance();

        assertEquals(true, client.badRequestCheck(PATH, accessToken));
    }

    /**
     * Test user popularity
     *
     * @param userId Id of user to be checked
     * @param isPopular Expected response
     */
    private void testFriends(String userId, boolean isPopular) {
        String accessToken = TestUsers.getAccessToken(userId);
        TestClient client = TestClient.getInstance();
        JsonObject message = client.request(PATH, accessToken);

        assertEquals(isPopular, message.getBoolean("is_popular"));
        assertEquals(userId, message.getString("user_id"));
    }
}
