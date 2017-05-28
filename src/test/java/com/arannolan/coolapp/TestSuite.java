package com.arannolan.coolapp;

import com.arannolan.coolapp.database.Database;
import com.arannolan.coolapp.utils.TestClient;
import com.arannolan.coolapp.utils.TestUsers;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Test suite to setup database and start HTTP server and create client before tests are run.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        PopularResourceTest.class,
        BandResourceTest.class
})
public class TestSuite {

    /**
     * Initialise test users in database and start HTTP server and create client.
     */
    @BeforeClass
    public static void setUp() {

        // Connect to database
        Database.create("http://localhost:8000", Database.REGION, "", "");

        // initialise test users
        String[] removeUsers = {};
        String[] addUsers = {
                TestUsers.TEST_USER_A,
                TestUsers.TEST_USER_B,
                TestUsers.TEST_USER_C,
                TestUsers.TEST_USER_D,
                TestUsers.PUBILC_USER_ID
        };
        TestUsers.initDatabaseTestUsers(removeUsers, addUsers);
        TestUsers.fetchAccessTokens();

        // start grizzly server
        TestClient.getInstance();
    }

    /**
     * Stop test server.
     */
    @AfterClass
    public static void tearDown() {
        TestClient.getInstance().stop();
    }
}
