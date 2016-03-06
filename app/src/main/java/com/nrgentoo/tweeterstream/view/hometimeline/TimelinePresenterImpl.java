package com.nrgentoo.tweeterstream.view.hometimeline;

import com.hardsoftstudio.rxflux.action.RxError;
import com.hardsoftstudio.rxflux.store.RxStoreChange;
import com.nrgentoo.tweeterstream.action.Actions;
import com.nrgentoo.tweeterstream.common.di.HasComponent;
import com.nrgentoo.tweeterstream.common.di.component.ActivityComponent;
import com.nrgentoo.tweeterstream.store.TimelineStore;
import com.nrgentoo.tweeterstream.view.abstracttimeline.TimelinePresenter;
import com.nrgentoo.tweeterstream.view.abstracttimeline.TinelineView;
import com.twitter.sdk.android.core.models.Tweet;

import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

/**
 * Timeline presenter implementation
 */
public class TimelinePresenterImpl implements TimelinePresenter {

    // --------------------------------------------------------------------------------------------
    //      FIELDS
    // --------------------------------------------------------------------------------------------

    private TinelineView tinelineView;

    @Inject
    EventBus eventBus;

    @Inject
    Actions actions;

    @Inject
    TimelineStore timelineStore;

    long topId;
    long bottomId;

    // --------------------------------------------------------------------------------------------
    //      CONSTRUCTOR
    // --------------------------------------------------------------------------------------------

    public TimelinePresenterImpl(HasComponent<ActivityComponent> hasActivityComponent,
                                 TinelineView tinelineView) {
        // inject
        hasActivityComponent.getComponent().inject(this);

        this.tinelineView = tinelineView;
    }

    // --------------------------------------------------------------------------------------------
    //      PUBLIC METHODS
    // --------------------------------------------------------------------------------------------

    @Override
    public void onCreate() {
        // register event bus
        eventBus.register(this);

        // get tweets
        actions.getHomeTimeline();
        tinelineView.showProgress();
    }

    @Override
    public void onDestroy() {
        // unregister event bus
        eventBus.unregister(this);

        tinelineView = null;
    }

    @Override
    public void getUpdates() {
        actions.getHomeTimelineUpdates(topId);
    }

    @Override
    public void loadMore() {
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
                        tinelineView.setItems(tweets);

                        // get top and bottom ids
                        this.topId = tweets.get(0).id;
                        this.bottomId = tweets.get(tweets.size() - 1).id;

                        tinelineView.hideProgress();
                        break;
                    case Actions.GET_HOME_TIMELINE_UPDATES:
                        // timeline updates received
                        tweets = timelineStore.getHomeTimelineUpdates();
                        tinelineView.addItems(true, tweets);
                        tinelineView.hideProgress();

                        // update top id
                        this.topId = tweets.get(0).id;
                        break;
                    case Actions.GET_HOME_TIMELINE_MORE:
                        // more timeline data received, append to the end of list
                        tweets = timelineStore.getHomeTimelineMore();
                        tinelineView.addItems(false, tweets);
                        tinelineView.hideProgress();

                        // update bottom id
                        this.bottomId = tweets.get(tweets.size() - 1).id;
                        break;
                }
                break;
        }
    }

    @SuppressWarnings("unused")
    public void onEvent(RxError error) {
        switch (error.getAction().getType()) {
            case Actions.GET_HOME_TIMELINE:
                tinelineView.hideProgress();
                break;
            case Actions.GET_HOME_TIMELINE_UPDATES:
                tinelineView.hideProgress();
                break;
            case Actions.GET_HOME_TIMELINE_MORE:
                tinelineView.hideProgress();
                break;
        }
    }
}
