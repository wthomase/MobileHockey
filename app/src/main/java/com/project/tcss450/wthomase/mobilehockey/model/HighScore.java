package com.project.tcss450.wthomase.mobilehockey.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;


/**
 * The HighScore class holds the all of the data required for a high score,
 * including the username and the score. It also implements Comparable to allow
 * sorting of high score lists using Collections.sort().
 */

public class HighScore implements Serializable, Comparable<HighScore> {

    /** Stores the username for the HighScore. */
    private String mUserId;
    /** Stores the score for the HighScore. */
    private String mHighScore;

    /** Stores the ID and HIGHSCORE values corresponding to the PHP file on the server. */
    public static final String ID = "email",HIGHSCORE = "highscore";

    /** Constructs a new HighScore using the given id and high score. */
    public HighScore(String id, String highScore) {
        mUserId = id;
        mHighScore = highScore;
    }

    /** Compares this high score to the given high score.
     *  @return whether this high score is higher than the given high score.
     */
    public int compareTo(HighScore other) {
        return Integer.parseInt(other.getmHighScore()) - Integer.parseInt(mHighScore);
    }

    /** Getter method for the high score.
     *  @return the score itself for this high score.
     */
    public String getmHighScore() {
        return mHighScore;
    }

    /** Setter method for the high score. */
    public void setmHighScore(String mHighScore) {
        this.mHighScore = mHighScore;
    }

    /** Getter method for the username.
     * @return the username for this high score.
     */
    public String getmUserId() {
        return mUserId;
    }

    /** Setter method for the username. */
    public void setmUserId(String mUserId) {
        this.mUserId = mUserId;
    }

    /**
     * Parses the json string, returns an error message if unsuccessful.
     * Returns course list if success.
     * @param highscoreJSON
     * @return reason or null if successful.
     */
    public static String parseCourseJSON(String highscoreJSON, List<HighScore> highScoreList) {
        String reason = null;
        if (highscoreJSON != null) {
            try {
                JSONArray arr = new JSONArray(highscoreJSON);

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    HighScore course = new HighScore(obj.getString(HighScore.ID), obj.getString(HighScore.HIGHSCORE));
                    highScoreList.add(course);
                }

                Collections.sort(highScoreList);
            } catch (JSONException e) {
                reason =  "Unable to parse data, Reason: " + e.getMessage();
            }

        }
        return reason;
    }

}
