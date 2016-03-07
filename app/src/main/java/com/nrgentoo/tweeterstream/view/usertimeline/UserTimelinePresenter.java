package com.nrgentoo.tweeterstream.view.usertimeline;

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
 * User timeline presenter
 */
public class UserTimelinePresenter extends AbstractPresenter {

    // --------------------------------------------------------------------------------------------
    //      CONSTRUCTOR
    // --------------------------------------------------------------------------------------------

    public UserTimelinePresenter(HasComponent<ActivityComponent> hasActivityComponent,
                                 TimelineView timelineView) {
        super(hasActivityComponent, timelineView);
    }

    // --------------------------------------------------------------------------------------------
    //      PROTECTED METHODS
    // --------------------------------------------------------------------------------------------

    @Override
    protected void getTimeline() {
        actions.getUserTimeline();
    }

    @Override
    protected void getTimelineUpdates() {
        actions.getUserTimelineUpdates(topId);
    }

    @Override
    protected void getTimelineMore() {
        actions.getUserTimelineMore(bottomId - 1);
    }

    // --------------------------------------------------------------------------------------------
    //      RX FLUX EVENTS
    // --------------------------------------------------------------------------------------------

    @SuppressWarnings("unused")
    public void onEvent(RxStoreChange change) {
        switch (change.getStoreId()) {
            case TimelineStore.ID:
                switch (change.getRxAction().getType()) {
                    case Actions.GET_USER_TIMELINE:
                        List<Tweet> tweets = timelineStore.getUserTimeline();

                        // set tweets
                        timelineView.setItems(tweets);
                        timelineView.hideProgress();

                        // get top and bottom ids
                        this.topId = tweets.get(0).id;
                        this.bottomId = tweets.get(tweets.size() - 1).id;
                        break;
                    case Actions.GET_USER_TIMELINE_UPDATES:
                        tweets = timelineStore.getUserTimelineUpdates();

                        if (!tweets.isEmpty()) {
                            // add tweets
                            timelineView.addItems(true, tweets);

                            // update top id
                            this.topId = tweets.get(0).id;
                        }

                        timelineView.hideProgress();
                        break;
                    case Actions.GET_USER_TIMELINE_MORE:
                        tweets = timelineStore.getUserTimelineMore();

                        if (!tweets.isEmpty()) {
                            // add tweets
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
            case Actions.GET_USER_TIMELINE:
            case Actions.GET_USER_TIMELINE_UPDATES:
            case Actions.GET_USER_TIMELINE_MORE:
                timelineView.hideProgress();
                break;
        }
    }
}
