package com.nrgentoo.tweeterstream.store;

import com.hardsoftstudio.rxflux.action.RxAction;
import com.hardsoftstudio.rxflux.dispatcher.Dispatcher;
import com.hardsoftstudio.rxflux.store.RxStore;
import com.hardsoftstudio.rxflux.store.RxStoreChange;
import com.nrgentoo.tweeterstream.action.Actions;
import com.nrgentoo.tweeterstream.action.Keys;
import com.twitter.sdk.android.core.models.Tweet;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link TimelineStore} implementation
 */
public class TimelineStoreImpl extends RxStore implements TimelineStore {

    List<Tweet> homeTimeline;
    List<Tweet> homeTimelineUpdates;

    public TimelineStoreImpl(Dispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public List<Tweet> getHomeTimeline() {
        return homeTimeline;
    }

    @Override
    public List<Tweet> getHomeTimelineUpdates() {
        return homeTimelineUpdates;
    }

    @Override
    public void onRxAction(RxAction action) {
        switch (action.getType()) {
            case Actions.GET_HOME_TIMELINE:
                // put received tweets to the map
                homeTimeline = new ArrayList<>();
                homeTimeline.addAll(action.get(Keys.RESULT_GET_HOME_TIMELINE));
                break;
            case Actions.GET_HOME_TIMELINE_UPDATES:
                // save timeline updates
                homeTimelineUpdates = action.get(Keys.RESULT_GET_HOME_TIMELINE_UPDATES);
                // push new tweets to homeTimeline
                homeTimeline.addAll(0, homeTimelineUpdates);
                break;
            default:
                return;
        }

        postChange(new RxStoreChange(ID, action));
    }
}
