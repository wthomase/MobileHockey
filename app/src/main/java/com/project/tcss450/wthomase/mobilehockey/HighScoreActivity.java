package com.project.tcss450.wthomase.mobilehockey;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.project.tcss450.wthomase.mobilehockey.model.HighScore;

public class HighScoreActivity extends AppCompatActivity implements HighScoreListFragment.OnListFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);

        if (savedInstanceState == null || getSupportFragmentManager().findFragmentById(R.id.list_highscore_fragment) == null) {
            HighScoreListFragment highScoreListFragment = new HighScoreListFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.high_score_activity_fragment_container, highScoreListFragment)
                    .commit();
        }

    }

    @Override
    public void onListFragmentInteraction(HighScore item) { }

    public void sendHighScores(View view) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out these high scores!");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

}
