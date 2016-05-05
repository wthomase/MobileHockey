package com.project.tcss450.wthomase.mobilehockey;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.project.tcss450.wthomase.mobilehockey.model.HighScore;

import java.util.List;

/**
 *
 */
public class MyHighScoreRecyclerViewAdapter extends RecyclerView.Adapter<MyHighScoreRecyclerViewAdapter.ViewHolder> {

    /**
     * List of HighScore objects.
     */
    private final List<HighScore> mValues;

    /**
     * A listener for HighScoreListFragment when the list is interacted with.
     */
    private final HighScoreListFragment.OnListFragmentInteractionListener mListener;

    /**
     * Constructor for the HighScoreListFragment class.
     * @param items
     * @param listener
     */
    public MyHighScoreRecyclerViewAdapter(List<HighScore> items, HighScoreListFragment.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    /**
     * Sets the Activity view to display fragment_highscore.
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_highscore, parent, false);
        return new ViewHolder(view);
    }

    /**
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).getmUserId());
        holder.mContentView.setText(mValues.get(position).getmHighScore());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    /**
     * Returns the number of highscores in mValues, the list of high scores.
     * @return the number of highscores in mValues
     */
    @Override
    public int getItemCount() {
        return mValues.size();
    }

    /**
     *
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        /**
         * An instance of View
         */
        public final View mView;

        /**
         * An instance of TextView to display the user's email
         */
        public final TextView mIdView;

        /**
         * An instance of TextView to display the user's high score
         */
        public final TextView mContentView;

        /**
         * An instance of HighScore
         */
        public HighScore mItem;

        /**
         *
         * @param view
         */
        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        /**
         * Returns mContentView as a String.
         * @return
         */
        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
