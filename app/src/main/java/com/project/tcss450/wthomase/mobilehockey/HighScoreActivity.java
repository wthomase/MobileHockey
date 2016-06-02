package com.project.tcss450.wthomase.mobilehockey;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.project.tcss450.wthomase.mobilehockey.model.HighScore;

/**
 * Class used to represent the activity_high_score view.
 */
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


}
