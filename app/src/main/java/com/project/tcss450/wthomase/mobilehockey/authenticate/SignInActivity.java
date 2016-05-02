package com.project.tcss450.wthomase.mobilehockey.authenticate;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.project.tcss450.wthomase.mobilehockey.LoginFragment;
import com.project.tcss450.wthomase.mobilehockey.LoginMenuActivity;
import com.project.tcss450.wthomase.mobilehockey.MainMenuActivity;
import com.project.tcss450.wthomase.mobilehockey.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SignInActivity extends AppCompatActivity implements LoginFragment.LoginInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, new LoginFragment() )
                .commit();

        setContentView(R.layout.activity_sign_in);
    }

    public void login(String userid, String pwd, String URL) {
        LoginUserTask task = new LoginUserTask();
        task.execute(new String[]{URL.toString()});
    }

    public void onSuccess() {
        final EditText text = (EditText) findViewById(R.id.edittext_login_username);
        MainMenuActivity.userLogged = text.getText().toString();
        Intent intent = new Intent(this, LoginMenuActivity.class);
        startActivity(intent);
        finish();
    }

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
                    response = "Unable to add user, Reason: "
                            + e.getMessage();
                } finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
            }
            return response;
        }


        /**
         * It checks to see if there was a problem with the URL(Network) which is when an
         * exception is caught. It tries to call the parse Method and checks to see if it was successful.
         * If not, it displays the exception.
         *
         * @param result
         */
        @Override
        protected void onPostExecute(String result) {
            // Something wrong with the network or the URL.
            try {
                JSONObject jsonObject = new JSONObject(result);
                String status = (String) jsonObject.get("result");
                String email = (String) jsonObject.get("email");
                if (status.equals("success")) {
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
