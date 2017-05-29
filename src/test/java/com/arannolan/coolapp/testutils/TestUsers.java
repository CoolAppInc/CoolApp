package com.arannolan.coolapp.testutils;

import com.arannolan.coolapp.App;
import com.arannolan.coolapp.database.Database;
import com.arannolan.coolapp.database.User;
import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.json.JsonArray;
import com.restfb.json.JsonObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Util class to handle test user data.
 */
public class TestUsers {

    private static Map<String, String> accessTokens;

    /**
     * User has:
     * No friends           -- not popular
     * No liked bands       -- no favourite band
     * No tagged places     -- no favourite place
     */
    public static final String TEST_USER_A = "102578557005471";

    /**
     * User has:
     * 50 friends           -- not popular
     */
    public static final String TEST_USER_B = "109846136273135";

    /**
     * User has:
     * 51 friends           -- is popular
     * Several liked places -- has favourite place: 'Wicklow, Ireland'
     */
    public static final String TEST_USER_C = "110422246215301";

    /**
     * User has:
     * No permissions       -- 400 bad request
     */
    public static final String TEST_USER_D = "103199216941555";

    /**
     * To be added as new user to database
     */
    public static final String TEST_USER_E = "108921803033250";

    /**
     * To overwrite old user information in database with
     * firstName = "Test"
     * lastName = "F"
     */
    public static final String TEST_USER_F = "102242187039650";

    /**
     * User has:
     * Several liked bands  -- has favourite band: 'Queen'
     */
    public static final String PUBILC_USER_ID = "104498163478112";
    /**
     * long-lived access token for public account
     * expires: Thursday, July 27, 2017 5:42:13 PM GMT+01:00 DST
     *
     * NOTE: This is a workaround for testing the 'favourite band' get request.
     * Facebook has changed how page categories work, so it is currently impossible
     * to create a page that shows as under 'music' when like. Test users cannot like
     * public pages so it is necessary to use a public account for this test.
     */
    public static final String PUBILC_USER_TOKEN =
            "EAAbJDUgoAiMBAMpZAVVv9Eep3Bca4RlthcP7mCgoZCMkyHum8kZCVUTK3IB" +
            "BVfY4mffnpZAEuJIfraacKRsscuL8veoGhsbuARyX3TsmOHAfKEgmoQjjkdS" +
            "DKqiGvFIuZAtTMtLZBGPJMWzLPYVGlT2CQ2QuZAIspRBVjZCIOIRGYAZDZD";

    /**
     * Fetch test user access tokens from Facebook Graph API.
     */
    public static void fetchAccessTokens() {

        // get data on all test users
        FacebookClient fbClient = new DefaultFacebookClient(App.APP_ACCESS_TOKEN, App.GRAPH_API_VERSION);
        Connection<JsonObject> testUserData = fbClient
                .fetchConnection(App.APP_ID + "/accounts/test-users", JsonObject.class);

        // store any with access tokens available
        accessTokens = new HashMap<>();
        for (List<JsonObject> testUserPage: testUserData) {
            for (JsonObject testUser: testUserPage) {
                if (testUser.has("access_token")) {
                    String id = testUser.getString("id");
                    String token = testUser.getString("access_token");
                    accessTokens.put(id, token);
                }
            }
        }

        //


        // add public user test account, for 'favourite band' get request test
        accessTokens.put(PUBILC_USER_ID, PUBILC_USER_TOKEN);
    }


    /**
     * Get access token for given test user Id.
     *
     * note: fetchAccessTokens() should be called before calling this method.
     *
     * @param userId Id of test user
     * @return Access token of test user, or null if not found
     */
    public static String getAccessToken(String userId) {
        String result = null;
        if (accessTokens != null) {
            result = accessTokens.get(userId);
        }
        return result;
    }

    /**
     * Add and remove appropriate test users from database, using dummy name.
     */
    public static void initDatabaseTestUsers(String[] removeUsers, String[] addUsers) {
        Database database = Database.getInstance();

        for (String userId: removeUsers) {
            database.deleteUser(userId);
        }

        for (String userId: addUsers) {
            database.addUser(new User(userId, "Agent Smith"));
        }
    }
}
