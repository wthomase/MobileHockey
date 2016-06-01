package com.project.tcss450.wthomase.mobilehockey;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class EndGameFragment extends DialogFragment {


    public EndGameFragment() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.share_scores_text)
                .setPositiveButton(R.string.share, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Go to the SubmitHighScoreActivity
                        Intent intent = new Intent(getActivity(), SubmitHighScoreActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(R.string.return_to_menu, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Return to the MainMenu
                        Intent intent = new Intent(getActivity(), LoginMenuActivity.class);
                        startActivity(intent);
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

}
