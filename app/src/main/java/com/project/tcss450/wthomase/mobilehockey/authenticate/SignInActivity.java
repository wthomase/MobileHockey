package com.project.tcss450.wthomase.mobilehockey.authenticate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.project.tcss450.wthomase.mobilehockey.LoginFragment;
import com.project.tcss450.wthomase.mobilehockey.LoginMenuActivity;
import com.project.tcss450.wthomase.mobilehockey.MainMenuActivity;
import com.project.tcss450.wthomase.mobilehockey.R;
import com.project.tcss450.wthomase.mobilehockey.data.UserInfoDB;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Class used to log existing users into the app.
 */
public class SignInActivity extends AppCompatActivity implements LoginFragment.LoginInteractionListener {

    private static SharedPreferences mSharedPreferences;
    private UserInfoDB mUserInfoDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSharedPreferences = getSharedPreferences(getString(R.string.LOGIN_PREFS)
                , Context.MODE_PRIVATE);

        if (mSharedPreferences.getBoolean(getString(R.string.LOGGEDIN), false)) {
            Toast.makeText(getApplicationContext(), "Detected user already logged in."
                    , Toast.LENGTH_LONG)
                    .show();
            Intent i = new Intent(this, LoginMenuActivity.class);
            startActivity(i);
            finish();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new LoginFragment())
                    .commit();

            setContentView(R.layout.activity_sign_in);
        }
    }

    /**
     * Logs an existing user into the app using the userid and pwd they entered.
     * @param userid the user's email
     * @param pwd the user's password
     * @param URL
     */
    public void login(String userid, String pwd, String URL) {
        LoginUserTask task = new LoginUserTask();
        task.execute(new String[]{URL.toString()});
    }

    /**
     * Called when a login is successful.
     * Opens the LoginMenuActivity.
     */
    public void onSuccess() {
        final EditText text = (EditText) findViewById(R.id.edittext_login_username);
        final EditText password = (EditText) findViewById(R.id.edittext_login_password);
        MainMenuActivity.userLogged = text.getText().toString();
        mSharedPreferences
                .edit()
                .putBoolean(getString(R.string.LOGGEDIN), true)
                .commit();
        mSharedPreferences
                .edit()
                .putString(getString(R.string.USERNAME_KEY), text.getText().toString())
                .commit();

        if (mUserInfoDB == null) {
            mUserInfoDB = new UserInfoDB(SignInActivity.this);
        }
        mUserInfoDB.insertUser(text.toString(), password.toString());
        Intent intent = new Intent(this, LoginMenuActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Class used to handle existing users logging in.
     */
    private class LoginUserTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            HttpURLConnection urlConnection = null;
            for (String url : urls) {
                try {
                    URL urlObject = new URL(url);
                    urlConnection = (HttpURLConnection) urlObject.openConnection();

                    InputStream content = urlConnection.getInputStream();

                    BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                    String s = "";
                    while ((s = buffer.readLine()) != null) {
                        response += s;
                    }

                } catch (Exception e) {
                    // What is this for?
                    response = "Unable to add user, Reason: "
                            + e.getMessage();
                } finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            // Something wrong with the network or the URL.
            try {
                JSONObject jsonObject = new JSONObject(result);
                String status = (String) jsonObject.get("result");
                if (status.equals("success")) {
                    String email = (String) jsonObject.get("email");
                    Toast.makeText(getApplicationContext(), email + " logged in successfully!"
                            , Toast.LENGTH_LONG)
                            .show();
                    onSuccess();
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to login: "
                                    + jsonObject.get("error")
                            , Toast.LENGTH_LONG)
                            .show();
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Something wrong with the data: " +
                        e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

}
