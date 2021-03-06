package com.nrgentoo.tweeterstream.view.abstracttimeline;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nrgentoo.tweeterstream.R;
import com.nrgentoo.tweeterstream.view.TimelineAdapter;
import com.twitter.sdk.android.core.models.Tweet;

import java.util.List;

/**
 * Abstract timeline fragment
 */
abstract public class AbstractTimelineFragment extends Fragment implements TimelineView {

    // --------------------------------------------------------------------------------------------
    //      FIELDS
    // --------------------------------------------------------------------------------------------

    private TimelineAdapter adapter;
    private TimelinePresenter timelinePresenter;
    private boolean refreshing;

    // --------------------------------------------------------------------------------------------
    //      UI REFERENCES
    // --------------------------------------------------------------------------------------------

    private RecyclerView rv_timeline;
    private SwipeRefreshLayout swipeRefreshLayout;

    // --------------------------------------------------------------------------------------------
    //      LIFECYCLE
    // --------------------------------------------------------------------------------------------

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_timeline, container, false);

        // inflate views
        rv_timeline = (RecyclerView) view.findViewById(R.id.rv_timeline);
        adapter = new TimelineAdapter(rv_timeline);
        rv_timeline.setAdapter(adapter);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);

        // create presenter
        timelinePresenter = createPresenter();
        timelinePresenter.onCreate();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        timelinePresenter.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();

        // set listeners
        swipeRefreshLayout.setOnRefreshListener(timelinePresenter::getUpdates);
        adapter.setOnLoadMoreListener(timelinePresenter::loadMore);
    }

    @Override
    public void onPause() {
        super.onPause();

        // clear listeners
        swipeRefreshLayout.setOnRefreshListener(null);
        adapter.setOnLoadMoreListener(null);
    }

    // --------------------------------------------------------------------------------------------
    //      TIMELINE VIEW INTERFACE
    // --------------------------------------------------------------------------------------------

    @Override
    public void showProgress() {
        refreshing = true;

        swipeRefreshLayout.post(() -> {
            if (refreshing && !swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(true);
            }
        });
    }

    @Override
    public void hideProgress() {
        refreshing = false;

        swipeRefreshLayout.post(() -> {
            swipeRefreshLayout.setRefreshing(false);
        });

        adapter.hideProgress();
    }

    @Override
    public void addItems(boolean atTop, List<Tweet> tweets) {
        adapter.addTweets(atTop, tweets);
        if (atTop) {
            rv_timeline.post(() -> rv_timeline.smoothScrollToPosition(0));
        }
    }

    @Override
    public void setItems(List<Tweet> tweets) {
        adapter.setTweets(tweets);
    }

    @Override
    public void showMessage(String message) {

    }

    // --------------------------------------------------------------------------------------------
    //      PROTECTED METHODS
    // --------------------------------------------------------------------------------------------

    abstract protected TimelinePresenter createPresenter();
}
