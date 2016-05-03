package com.project.tcss450.wthomase.mobilehockey;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.project.tcss450.wthomase.mobilehockey.authenticate.SignInActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

// Temporary class to test submission of high scores to the database.

public class SubmitHighScoreActivity extends AppCompatActivity {

    private final String SUBMIT_HIGHSCORE_URL =
            "http://cssgate.insttech.washington.edu/~wthomase/addHighscore.php?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_high_score);

    }

    public void buttonRespondSubmitHighScores(View view) {
        if (MainMenuActivity.userLogged == null || MainMenuActivity.userLogged.length() <= 0) {
            Toast.makeText(this, "You are logged in as a Guest.",
                    Toast.LENGTH_LONG).show();
        } else {
            EditText score = (EditText) findViewById(R.id.edittext_enter_highscore_test);
            String URL = buildHighScoreURL(view, MainMenuActivity.userLogged, score.getText().toString());
            submitHighScore(URL);
        }
    }

    public void submitHighScore(String URL) {
        SubmitHighScoreTask task = new SubmitHighScoreTask();
        task.execute(new String[]{URL.toString()});
    }

    public void sendHighScores(View view) {
        Intent sendIntent = new Intent();
        EditText score = (EditText) findViewById(R.id.edittext_enter_highscore_test);
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "I just scored " + score.getText().toString() + " in MobileHockey!");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }


    private String buildHighScoreURL(View v, String userid, String score) {

        StringBuilder sb = new StringBuilder(SUBMIT_HIGHSCORE_URL);

        try {

            sb.append("email=");
            sb.append(userid);

            sb.append("&highscore=");
            sb.append(score);

            Log.i("Generated Login URL:", sb.toString());

        }
        catch(Exception e) {
            Toast.makeText(v.getContext(), "Something wrong with the url" + e.getMessage(), Toast.LENGTH_LONG)
                    .show();
        }
        return sb.toString();
    }

    private class SubmitHighScoreTask extends AsyncTask<String, Void, String> {


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
                if (status.equals("success")) {
                    Toast.makeText(getApplicationContext(), "High Score submitted successfully!"
                            , Toast.LENGTH_LONG)
                            .show();
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to submit: "
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
