package com.project.tcss450.wthomase.mobilehockey;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;


/**
 * EndGameDialog is used to display a DialogFragment at the end of a game.
 *
 * It queries the user as to whether they'd like to return to the Main Menu or
 * transition to the SubmitHighScoreActivity.
 */
public class EndGameDialog extends DialogFragment {


    /**
     * Required empty public constructor.
     */
    public EndGameDialog() {
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
