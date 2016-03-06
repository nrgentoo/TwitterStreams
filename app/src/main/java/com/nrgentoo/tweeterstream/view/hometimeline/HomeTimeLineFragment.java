package com.nrgentoo.tweeterstream.view.hometimeline;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nrgentoo.tweeterstream.R;
import com.nrgentoo.tweeterstream.common.di.HasComponent;
import com.nrgentoo.tweeterstream.common.di.component.ActivityComponent;
import com.nrgentoo.tweeterstream.view.TimelineAdapter;
import com.twitter.sdk.android.core.models.Tweet;

import java.util.List;

/**
 * Fragment with home timeline
 */
public class HomeTimeLineFragment extends Fragment implements HomeView {

    // --------------------------------------------------------------------------------------------
    //      FIELDS
    // --------------------------------------------------------------------------------------------

    private TimelineAdapter adapter;
    private TimelinePresenter timelinePresenter;

    // --------------------------------------------------------------------------------------------
    //      UI REFERENCES
    // --------------------------------------------------------------------------------------------

    private RecyclerView rv_timeline;

    // --------------------------------------------------------------------------------------------
    //      LIFECYCLE
    // --------------------------------------------------------------------------------------------

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_timeline, container, false);

        // inflate views
        rv_timeline = (RecyclerView) view.findViewById(R.id.rv_timeline);
        adapter = new TimelineAdapter(rv_timeline);
        rv_timeline.setAdapter(adapter);

        // create presenter
        timelinePresenter = new TimelinePresenterImpl((HasComponent<ActivityComponent>) getActivity(), this);
        timelinePresenter.onCreate();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        timelinePresenter.onDestroy();
    }

    // --------------------------------------------------------------------------------------------
    //      HOME VIEW INTERFACE
    // --------------------------------------------------------------------------------------------

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void addItems(boolean atTop, List<Tweet> tweets) {

    }

    @Override
    public void setItems(List<Tweet> tweets) {
        adapter.setTweets(tweets);
    }

    @Override
    public void showMessage(String message) {

    }
}
