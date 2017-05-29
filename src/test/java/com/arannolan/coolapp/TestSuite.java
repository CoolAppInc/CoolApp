package com.arannolan.coolapp;

import com.arannolan.coolapp.database.Database;
import com.arannolan.coolapp.testutils.TestClient;
import com.arannolan.coolapp.testutils.TestUsers;
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
        BandResourceTest.class,
        CreateUserResourceTest.class
})
public class TestSuite {


    /**
     * Test users to be removed from database before testing begins
     */
    private static final String[] REMOVE_USERS = { TestUsers.TEST_USER_E };

    /**
     * Test users to be added to database before testing begins
     */
    private static final String[] ADD_USERS = {
            TestUsers.TEST_USER_A,
            TestUsers.TEST_USER_B,
            TestUsers.TEST_USER_C,
            TestUsers.TEST_USER_D,
            TestUsers.TEST_USER_F,
            TestUsers.PUBILC_USER_ID
    };

    /**
     * Initialise test users in database and start HTTP server and create client.
     */
    @BeforeClass
    public static void setUp() {

        // Connect to database
        Database.create("http://localhost:8000", Database.REGION, "", "");

        // initialise test users
        TestUsers.initDatabaseTestUsers(REMOVE_USERS, ADD_USERS);
        TestUsers.fetchAccessTokens();

        // start grizzly server
        TestClient.getInstance();
    }

    /**
     * Stop test server, and database cleanup.
     */
    @AfterClass
    public static void tearDown() {

        // Remove all test users from database
        Database database = Database.getInstance();
        for (String user: REMOVE_USERS) database.deleteUser(user);
        for (String user: ADD_USERS) database.deleteUser(user);

        // Shutdown test server
        TestClient.getInstance().stop();
    }
}
