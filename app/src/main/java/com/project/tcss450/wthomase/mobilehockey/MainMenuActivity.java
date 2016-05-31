package com.project.tcss450.wthomase.mobilehockey;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.project.tcss450.wthomase.mobilehockey.authenticate.RegisterActivity;
import com.project.tcss450.wthomase.mobilehockey.authenticate.SignInActivity;

/**
 * Class used to handle the Main Menu Activity.
 * Creates intents in order to change Activities when buttons are clicked in the activity_main_menu.
 */
public class MainMenuActivity extends AppCompatActivity {

    /**
     * A String representing the user that is currently logged in.
     */
    public static String userLogged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Opens the Activity to sign in.
     * @param view
     */
    public void goToSignInActivity(View view) {
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
    }

    /**
     * Opens the Activity to register.
     * @param view
     */
    public void goToRegisterActivity(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    /**
     * Opens the Activity to go to the login menu.
     * @param view
     */
    public void goToLoginMenuActivity(View view) {
        Intent intent = new Intent(this, LoginMenuActivity.class);
        startActivity(intent);
    }
}
