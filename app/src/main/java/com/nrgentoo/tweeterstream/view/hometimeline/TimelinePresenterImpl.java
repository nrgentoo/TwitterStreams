package com.nrgentoo.tweeterstream.view.hometimeline;

import android.support.v4.app.FragmentActivity;

import com.hardsoftstudio.rxflux.action.RxError;
import com.hardsoftstudio.rxflux.store.RxStoreChange;
import com.nrgentoo.tweeterstream.action.Actions;
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
        actions.getTimeLine(null, null);
        homeView.showProgress();
    }

    @Override
    public void onDestroy() {
        // unregister event bus
        eventBus.unregister(this);

        homeView = null;
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
                        List<Tweet> tweets = timelineStore.getHomeTimeline(null, null);
                        homeView.setItems(tweets);
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
        }
    }
}
