package com.project.tcss450.wthomase.mobilehockey;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * SubmitHighScoreActivity is the Activity that the GameEngine switches to if the user selects
 * "Share" in the EndGameDialog popup. It contains Sharing and Submission options for high scores
 * and allows the user to return to the main menu.
 */

public class SubmitHighScoreActivity extends AppCompatActivity {

    /**
     * URL of the php file that interacts with the database that holds the high scores.
     */
    private final String SUBMIT_HIGHSCORE_URL =
            "http://cssgate.insttech.washington.edu/~wthomase/addHighscore.php?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_high_score);

    }

    /**
     * After clicking the SUBMIT HIGHSCORE button:
     * If the user is logged in as a Guest, they will see a Toast saying that they are logged in as a Guest.
     * This means that they are unable to save high scores. If they are logged in as a non-guest user, submitHighScore()
     * will be called to save their high score to the database.
     * @param view
     */
    public void buttonRespondSubmitHighScores(View view) {
        if (MainMenuActivity.userLogged == null || MainMenuActivity.userLogged.length() <= 0) {
            Toast.makeText(this, "You are logged in as a Guest.",
                    Toast.LENGTH_LONG).show();
        } else {
            String URL = buildHighScoreURL(view, MainMenuActivity.userLogged, LoginMenuActivity.mostRecentHighScore);
            submitHighScore(URL);
        }
    }

    /**
     * Switches us from the HighScore Activity to the main menu. Used for when the user
     * clicks on the "MainMenu" button.
     * @param view
     */
    public void returnToMainMenu(View view) {
        Intent intent = new Intent(this, LoginMenuActivity.class);
        startActivity(intent);
    }

    /**
     * Saves the high score to the database.
     * @param URL
     */
    public void submitHighScore(String URL) {
        SubmitHighScoreTask task = new SubmitHighScoreTask();
        task.execute(new String[]{URL.toString()});
    }

    /**
     * Opens the user's messaging application to send the cu
     * @param view
     */
    public void sendHighScores(View view) {
        Intent sendIntent = new Intent();
        String highScore = LoginMenuActivity.mostRecentHighScore;
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "I just scored " + highScore + " in MobileHockey!");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    /**
     * Tries to submit a new high score using the userid and score provided. If there is an exception,
     * a Toast will display alerting the user that there was something wrong with the URL.
     * @param v the view
     * @param userid the user's email
     * @param score the user's score
     * @return
     */
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

    /**
     * Class in which the submission of high scores are handled.
     */
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
