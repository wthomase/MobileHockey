package com.project.tcss450.wthomase.mobilehockey.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HighScore implements Serializable, Comparable<HighScore> {

    private String mUserId;
    private String mHighScore;

    public static final String ID = "email",HIGHSCORE = "highscore";

    public HighScore(String id, String highScore) {
        mUserId = id;
        mHighScore = highScore;
    }

    public int compareTo(HighScore other) {
        return Integer.parseInt(other.getmHighScore()) - Integer.parseInt(mHighScore);
    }

    public String getmHighScore() {
        return mHighScore;
    }

    public void setmHighScore(String mHighScore) {
        this.mHighScore = mHighScore;
    }

    public String getmUserId() {
        return mUserId;
    }

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
