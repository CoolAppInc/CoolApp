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
 * Tests errors common to resources extended from GetResourceBase.
 *
 * PopularResource used for testing purposes
 *
 * To be run as part of TestSuit
 */
public class GetResourceBaseTest {

    public static final String PATH = "popular";

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
}
