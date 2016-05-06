package com.project.tcss450.wthomase.mobilehockey.authenticate;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.project.tcss450.wthomase.mobilehockey.MainMenuActivity;
import com.project.tcss450.wthomase.mobilehockey.R;
import com.project.tcss450.wthomase.mobilehockey.RegisterFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Class used to Register (create) new Users to this app. It will make sure that an email entered
 * (for the username) is in the correct form and also checks to see if a password entered has the
 * required number of characters.
 */
public class RegisterActivity extends AppCompatActivity implements RegisterFragment.RegisterInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_register_container, new RegisterFragment() )
                .commit();

        setContentView(R.layout.activity_register);
    }

    /**
     * Registers a new user using the userid and pwd that they entered.
     * @param userid is the email the user will use to login
     * @param pwd is the password the user will use to login
     * @param URL
     */
    public void register(String userid, String pwd, String URL) {
        AddUserTask task = new AddUserTask();
        task.execute(new String[]{URL.toString()});

        // Takes you back to the previous fragment by popping the current fragment out.
        getSupportFragmentManager().popBackStackImmediate();


        Intent intent = new Intent(this, MainMenuActivity.class);
        startActivity(intent);
        finish();

    }

    /**
     * Class used to add a new user to the database.
     */
    private class AddUserTask extends AsyncTask<String, Void, String> {

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
                    Toast.makeText(getApplicationContext(), "User successfully added!"
                            , Toast.LENGTH_LONG)
                            .show();
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to add: "
                                    + jsonObject.get("error")
                            , Toast.LENGTH_LONG)
                            .show();
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Something wrong with the data" +
                        e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }




}