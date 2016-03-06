package com.nrgentoo.tweeterstream.view.hometimeline;

import com.hardsoftstudio.rxflux.action.RxError;
import com.hardsoftstudio.rxflux.store.RxStoreChange;
import com.nrgentoo.tweeterstream.action.Actions;
import com.nrgentoo.tweeterstream.common.di.HasComponent;
import com.nrgentoo.tweeterstream.common.di.component.ActivityComponent;
import com.nrgentoo.tweeterstream.store.TimelineStore;
import com.nrgentoo.tweeterstream.view.abstracttimeline.AbstractPresenter;
import com.nrgentoo.tweeterstream.view.abstracttimeline.TimelineView;
import com.twitter.sdk.android.core.models.Tweet;

import java.util.List;

/**
 * Timeline presenter implementation
 */
public class HomeTimelinePresenter extends AbstractPresenter {

    // --------------------------------------------------------------------------------------------
    //      CONSTRUCTOR
    // --------------------------------------------------------------------------------------------

    public HomeTimelinePresenter(HasComponent<ActivityComponent> hasActivityComponent,
                                 TimelineView timelineView) {
        super(hasActivityComponent, timelineView);
    }

    // --------------------------------------------------------------------------------------------
    //      PROTECTED METHODS
    // --------------------------------------------------------------------------------------------

    @Override
    protected void getTimeline() {
        actions.getHomeTimeline();
    }

    @Override
    protected void getTimelineUpdates() {
        actions.getHomeTimelineUpdates(topId);
    }

    @Override
    protected void getTimelineMore() {
        actions.getHomeTimelineMore(bottomId - 1);
    }

    // --------------------------------------------------------------------------------------------
    //      RX FLUX EVENTS
    // --------------------------------------------------------------------------------------------

    @SuppressWarnings("unused")
    public void onEvent(RxStoreChange change) {
        switch (change.getStoreId()) {
            case TimelineStore.ID:
                switch (change.getRxAction().getType()) {
                    case Actions.GET_HOME_TIMELINE:
                        List<Tweet> tweets = timelineStore.getHomeTimeline();

                        // initial request, just set tweets
                        timelineView.setItems(tweets);

                        // get top and bottom ids
                        this.topId = tweets.get(0).id;
                        this.bottomId = tweets.get(tweets.size() - 1).id;

                        timelineView.hideProgress();
                        break;
                    case Actions.GET_HOME_TIMELINE_UPDATES:
                        // timeline updates received
                        tweets = timelineStore.getHomeTimelineUpdates();

                        if (!tweets.isEmpty()) {
                            timelineView.addItems(true, tweets);

                            // update top id
                            this.topId = tweets.get(0).id;
                        }

                        timelineView.hideProgress();
                        break;
                    case Actions.GET_HOME_TIMELINE_MORE:
                        // more timeline data received, append to the end of list
                        tweets = timelineStore.getHomeTimelineMore();

                        if (!tweets.isEmpty()) {
                            timelineView.addItems(false, tweets);

                            // update bottom id
                            this.bottomId = tweets.get(tweets.size() - 1).id;
                        }

                        timelineView.hideProgress();
                        break;
                }
                break;
        }
    }

    @SuppressWarnings("unused")
    public void onEvent(RxError error) {
        switch (error.getAction().getType()) {
            case Actions.GET_HOME_TIMELINE:
                timelineView.hideProgress();
                break;
            case Actions.GET_HOME_TIMELINE_UPDATES:
                timelineView.hideProgress();
                break;
            case Actions.GET_HOME_TIMELINE_MORE:
                timelineView.hideProgress();
                break;
        }
    }
}
