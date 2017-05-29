package com.arannolan.coolapp;

import com.arannolan.coolapp.database.Database;
import com.arannolan.coolapp.database.User;
import com.arannolan.coolapp.resources.CreateUserResource;
import com.arannolan.coolapp.testutils.TestClient;
import com.arannolan.coolapp.testutils.TestUsers;
import com.restfb.json.JsonObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests create user POST request.
 */
public class CreateUserResourceTest {

    public static final String PATH = "createUser";

    /**
     * Test request without query parameter 'access_token' receives '400 Bad Request'.
     */
    @Test
    public void missingTokenTest() {
        TestClient client = TestClient.getInstance();
        assertEquals(true, client.badPostRequest(PATH, null));
    }

    /**
     * Test request with malformed access token receives '400 Bad Request'.
     */
    @Test
    public void invalidTokenTest() {
        TestClient client = TestClient.getInstance();
        assertEquals(true, client.badPostRequest(PATH, "invalid"));
    }

    /**
     * Test creation of user that is not already in database.
     */
    @Test
    public void createNewUserTest() {
        createUserTest(TestUsers.TEST_USER_E, "Test User E");
    }

    /**
     * Test creation/overwrite of user that is already in database.
     */
    @Test
    public void overwriteUserTest() {
        createUserTest(TestUsers.TEST_USER_F, "Test User F");
    }

    /**
     * Make create user request, and test that it is correctly stored to database.
     *
     * @param userId Id of created user
     * @param expectedName Name that should be stored in database for user
     */
    private void createUserTest(String userId, String expectedName) {
        // get user access token
        String accessToken = TestUsers.getAccessToken(userId);

        // make create user request
        TestClient client = TestClient.getInstance();
        JsonObject message = client.postRequest(PATH, accessToken);

        // check for expected response
        assertEquals(CreateUserResource.CREATED_MSG, message.getString("message"));
        assertEquals(userId, message.getString("user_id"));

        // check that database contains new user with correct name
        Database database = Database.getInstance();
        boolean hasUser = database.hasUser(userId);
        assertEquals(true, hasUser);
        if (hasUser) {
            User user = database.getUser(userId);
            assertEquals(expectedName, user.getName());
        }
    }
}
