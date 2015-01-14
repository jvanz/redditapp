package com.vanzstuff.readdit.fragments;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vanzstuff.readdit.FeedsAdapter;
import com.vanzstuff.readdit.Logger;
import com.vanzstuff.redditapp.R;
import com.vanzstuff.readdit.data.ReadditContract;

import android.net.Uri;

/**
 * Fragment that encapsulate all logic to show a list with all post acquire from a given Uri
 */
public class FeedsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,FeedsAdapter.ItemSelectedListener {

    private static final int POST_INIT_CURSOR_LOADER = 0;
    private static final String ARG_URI = "arg_uri";

    /** RecyclerView responsable to show all post */
    private RecyclerView mRecyclerView;
    /** Activity listener */
    private CallBack mCallback;

    /**
     * Factory method to build a new FeedFragment
     * @param postUri uri used to load the posts
     * @return new FeedsFragment instance
     */
    public static final FeedsFragment newInstance(Uri postUri){
        Bundle args = new Bundle(1);
        args.putString(ARG_URI, postUri.toString());
        FeedsFragment fragment = new FeedsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (CallBack) activity;
        }catch (ClassCastException e){
            throw new ClassCastException("The activity " + activity.getClass().getCanonicalName() + "must implement " + CallBack.class.getCanonicalName());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_post_list, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.fragment_post_list_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return v;
    }

    @Override
    public void onStop() {
        super.onStop();
        getLoaderManager().getLoader(POST_INIT_CURSOR_LOADER).stopLoading();
    }

    @Override
    public void onStart() {
        super.onStart();
        getLoaderManager().initLoader(POST_INIT_CURSOR_LOADER, getArguments(), this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getLoaderManager().destroyLoader(POST_INIT_CURSOR_LOADER);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Logger.d("URI loaded: " + getArguments().getString(ARG_URI));
        return new CursorLoader(getActivity(), Uri.parse(args.getString(ARG_URI)), null, null, null, ReadditContract.Post.COLUMN_DATE);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mRecyclerView.swapAdapter(new FeedsAdapter(data, this), false);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        loader.stopLoading();
        mRecyclerView.swapAdapter(null, false);
    }

    @Override
    public void onPostClicked(long postId) {
        mCallback.onItemSelected(postId);
    }

    /**
     * Interface to enable communication between the fragment and the activity
     */
    public static interface CallBack {

        /**
         * Method called when an item is clicked
         * @param postID ID of the post selected
         */
        public void onItemSelected(long postID);
    }
}