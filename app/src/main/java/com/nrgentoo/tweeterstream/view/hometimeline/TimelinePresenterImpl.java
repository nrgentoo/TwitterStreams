package com.nrgentoo.tweeterstream.view.hometimeline;

import com.hardsoftstudio.rxflux.action.RxError;
import com.hardsoftstudio.rxflux.store.RxStoreChange;
import com.nrgentoo.tweeterstream.action.Actions;
import com.nrgentoo.tweeterstream.action.Keys;
import com.nrgentoo.tweeterstream.common.di.HasComponent;
import com.nrgentoo.tweeterstream.common.di.component.ActivityComponent;
import com.nrgentoo.tweeterstream.store.TimelineStore;
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

    private HomeView homeView;

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
                                 HomeView homeView) {
        // inject
        hasActivityComponent.getComponent().inject(this);

        this.homeView = homeView;
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
        homeView.showProgress();
    }

    @Override
    public void onDestroy() {
        // unregister event bus
        eventBus.unregister(this);

        homeView = null;
    }

    @Override
    public void getUpdates() {
        actions.getHomeTimelineUpdates(topId);
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
                        Long sinceId = change.getRxAction().get(Keys.PARAM_SINCE_ID);
                        Long maxId = change.getRxAction().get(Keys.PARAM_MAX_ID);

                        List<Tweet> tweets = timelineStore.getHomeTimeline();

                        if (sinceId == null && maxId == null) {
                            // initial request, just set tweets
                            homeView.setItems(tweets);

                            // get top and bottom ids
                            this.topId = tweets.get(0).id;
                            this.bottomId = tweets.get(tweets.size() - 1).id;
                        } else if (maxId != null){
                            // request for more tweets, add to bottom
                            homeView.addItems(false, tweets);

                            // get bottom id
                            this.bottomId = tweets.get(tweets.size() - 1).id;
                        }

                        homeView.hideProgress();
                        break;
                    case Actions.GET_HOME_TIMELINE_UPDATES:
                        // timeline updates received
                        homeView.addItems(true, timelineStore.getHomeTimelineUpdates());
                        homeView.hideProgress();
                        break;
                }
                break;
        }
    }

    @SuppressWarnings("unused")
    public void onEvent(RxError error) {
        switch (error.getAction().getType()) {
            case Actions.GET_HOME_TIMELINE:
                homeView.hideProgress();
                break;
            case Actions.GET_HOME_TIMELINE_UPDATES:
                homeView.hideProgress();
                break;
        }
    }
}
