package com.nrgentoo.tweeterstream.store;

import android.support.annotation.Nullable;

import com.hardsoftstudio.rxflux.action.RxAction;
import com.hardsoftstudio.rxflux.dispatcher.Dispatcher;
import com.hardsoftstudio.rxflux.store.RxStore;
import com.hardsoftstudio.rxflux.store.RxStoreChange;
import com.nrgentoo.tweeterstream.action.Actions;
import com.nrgentoo.tweeterstream.action.Keys;
import com.twitter.sdk.android.core.models.Tweet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link TimelineStore} implementation
 */
public class TimelineStoreImpl extends RxStore implements TimelineStore {

    private static final String EMPTY_KEY = "empty_key";

    Map<String, List<Tweet>> homeTimelineMap = new HashMap<>();
    List<Tweet> homeTimelineUpdates;

    public TimelineStoreImpl(Dispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public List<Tweet> getHomeTimeline(@Nullable Long sinceId, @Nullable Long maxId) {
        return homeTimelineMap.get(getKey(sinceId, maxId));
    }

    @Override
    public List<Tweet> getHomeTimelineUpdates() {
        return homeTimelineUpdates;
    }

    @Override
    public void onRxAction(RxAction action) {
        switch (action.getType()) {
            case Actions.GET_HOME_TIMELINE:
                Long sinceId = action.get(Keys.PARAM_SINCE_ID);
                Long maxId = action.get(Keys.PARAM_MAX_ID);
                List<Tweet> tweets = action.get(Keys.RESULT_GET_HOME_TIMELINE);

                // put received tweets to the map
                homeTimelineMap.put(getKey(sinceId, maxId), tweets);
                break;
            case Actions.GET_HOME_TIMELINE_UPDATES:
                homeTimelineUpdates = action.get(Keys.RESULT_GET_HOME_TIMELINE_UPDATES);
                break;
            default:
                return;
        }

        postChange(new RxStoreChange(ID, action));
    }

    private String getKey(@Nullable Long sinceId, @Nullable Long maxId) {
        if (sinceId == null && maxId == null) {
            return EMPTY_KEY;
        } else {
            return "since_id: " + sinceId + "; max_id: " + maxId;
        }
    }
}
