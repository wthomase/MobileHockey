package com.project.tcss450.wthomase.mobilehockey;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.project.tcss450.wthomase.mobilehockey.gameengine.GameEngine;

/**
 * Class used to handle the Login Menu Activity.
 * Creates intents in order to change Activities when buttons are clicked in the activity_login_menu.
 */
public class LoginMenuActivity extends AppCompatActivity {

    public static String mostRecentHighScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_menu);
        TextView logged = (TextView) findViewById(R.id.current_logged_user);
        SharedPreferences mSharedPreferences = getSharedPreferences(getString(R.string.LOGIN_PREFS)
                , Context.MODE_PRIVATE);

        String curLogged = mSharedPreferences.getString(getString(R.string.USERNAME_KEY), "Guest");
        logged.setText("Logged in as: " + curLogged);
    }

    /**
     * Opens the Activity for a New Game.
     * @param view
     */
    public void switchToNewGameActivity(View view) {
        Intent intent = new Intent(this, GameEngine.class);
        startActivity(intent);
    }

    /**
     * Opens the Activity for the High Scores.
     * @param view
     */
    public void switchToHighScoreActivity(View view) {
        Intent intent = new Intent(this, HighScoreActivity.class);
        startActivity(intent);
    }

    /**
     * Helper method to logout the currently logged in user on a button click.
     * @param view
     */
    public void logoutUser(View view) {
        SharedPreferences sharedPreferences =
                getSharedPreferences(getString(R.string.LOGIN_PREFS), Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(getString(R.string.LOGGEDIN), false)
                .commit();
        sharedPreferences.edit().remove(getString(R.string.USERNAME_KEY)).apply();

        Toast.makeText(getApplicationContext(), "Logged out successfully.",
                Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, MainMenuActivity.class);
        startActivity(intent);
    }
}
