package com.arannolan.coolapp;

import com.arannolan.coolapp.testutils.TestClient;
import com.arannolan.coolapp.utils.Error;
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
     * Test request without query parameter 'access_token'.
     */
    @Test
    public void missingTokenTest() {
        TestClient client = TestClient.getInstance();
        JsonObject message = client.getRequest(PATH, null);
        assertEquals(Error.MISSING_TOKEN, message.getJsonObject("error").getString("message"));
    }

    /**
     * Test request with empty query parameter 'access_token'.
     */
    @Test
    public void emptyTokenTest() {
        TestClient client = TestClient.getInstance();
        JsonObject message = client.getRequest(PATH, "");
        assertEquals(Error.MISSING_TOKEN, message.getJsonObject("error").getString("message"));
    }

    /**
     * Test request with malformed access token.
     */
    @Test
    public void invalidTokenTest() {
        TestClient client = TestClient.getInstance();
        JsonObject message = client.getRequest(PATH, "invalid");
        assertEquals(Error.INVALID_TOKEN, message.getJsonObject("error").getString("message"));
    }
}
