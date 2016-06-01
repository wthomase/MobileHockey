package com.project.tcss450.wthomase.mobilehockey;

import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;


public class TestUserAccounts extends ActivityInstrumentationTestCase2<MainMenuActivity> {

    private Solo solo;

    public TestUserAccounts() {
        super(MainMenuActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation(), getActivity());
    }

    @Override
    public void tearDown() throws Exception {
        //tearDown() is run after a test case has finished.
        //finishOpenedActivities() will finish all the activities that have been opened during the test execution.
        solo.finishOpenedActivities();

    }

    public void testSuccessfulLoad() {
        boolean loginButton = solo.searchText("Login");
        boolean registerButton = solo.searchText("Register");
        boolean both = (loginButton && registerButton);
        assertTrue("Main Menu loaded successfully.", both);
    }

    public void testLoginButton() {
        solo.clickOnView(getActivity().findViewById(R.id.main_menu_go_to_login));
        boolean successfulTransition = solo.searchText("Enter username here");
        assertTrue("Login button transitioned successfully.", successfulTransition);
    }

    public void testRegisterButton() {
        solo.clickOnView(getActivity().findViewById(R.id.main_menu_go_to_register));
        boolean success = solo.searchText("Enter desired password here");
        assertTrue("Register button transitioned successfully.", success);
    }

    public void testLoginWithTestAccount() {
        solo.clickOnView(getActivity().findViewById(R.id.main_menu_go_to_login));
        solo.enterText(0, "testingAgain@gmail.com");
        solo.enterText(1, "testing12345");
        solo.clickOnButton("Login");
        boolean success = solo.searchText("New Game");
        solo.clickOnButton("Logout");
        assertTrue("Successfully logged in with test account.", success);
    }

    public void testLoginAndHighScoreLoad() {
        solo.clickOnView(getActivity().findViewById(R.id.main_menu_go_to_login));
        solo.enterText(0, "testingAgain@gmail.com");
        solo.enterText(1, "testing12345");
        solo.clickOnButton("Login");
        solo.clickOnButton("High Scores");
        boolean success = solo.searchText("High Scores");
        solo.goBack();
        solo.clickOnButton("Logout");
        assertTrue("Successfully logged in with test account and viewed high scores.", success);
    }

    public void testFailedRegister() {
        solo.clickOnView(getActivity().findViewById(R.id.main_menu_go_to_register));
        solo.enterText(0, "e");
        solo.enterText(1, "passwordislongenoughbutusernameisnt");
        solo.clickOnButton("Register");
        boolean success = solo.searchText("Not registered?");
        assertTrue("Failed to login successfully.", success);
    }
}
