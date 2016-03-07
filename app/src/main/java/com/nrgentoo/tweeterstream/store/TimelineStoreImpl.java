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
    List<Tweet> homeTimelineMore;
    List<Tweet> userTimeline;
    List<Tweet> userTimelineUpdates;
    List<Tweet> userTimelineMore;

    boolean isHomeTimelineGotFromDB = false;

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
    public List<Tweet> getHomeTimelineMore() {
        return homeTimelineMore;
    }

    @Override
    public List<Tweet> getUserTimeline() {
        return userTimeline;
    }

    @Override
    public List<Tweet> getUserTimelineUpdates() {
        return userTimelineUpdates;
    }

    @Override
    public List<Tweet> getUserTimelineMore() {
        return userTimelineMore;
    }

    @Override
    public boolean isHomeTimelineGotFromDB() {
        return isHomeTimelineGotFromDB;
    }

    @Override
    public void onRxAction(RxAction action) {
        switch (action.getType()) {
            case Actions.GET_HOME_TIMELINE:
                // store received tweets
                homeTimeline = new ArrayList<>();
                homeTimeline.addAll(action.get(Keys.RESULT_GET_HOME_TIMELINE));

                // set flag
                if (action.getData().containsKey(Keys.PARAM_FROM_DB)) {
                    isHomeTimelineGotFromDB = action.get(Keys.PARAM_FROM_DB);
                } else {
                    isHomeTimelineGotFromDB = false;
                }
                break;
            case Actions.GET_HOME_TIMELINE_UPDATES:
                // save timeline updates
                homeTimelineUpdates = action.get(Keys.RESULT_GET_HOME_TIMELINE_UPDATES);
                // push new tweets to homeTimeline
                homeTimeline.addAll(0, homeTimelineUpdates);
                break;
            case Actions.GET_HOME_TIMELINE_MORE:
                // save result to homeTimelineMore
                homeTimelineMore = action.get(Keys.RESULT_GET_HOME_TIMELINE_MORE);
                // append new tweets to homeTimeline
                homeTimeline.addAll(homeTimelineMore);
                break;
            case Actions.GET_USER_TIMELINE:
                // store received tweets
                userTimeline = new ArrayList<>();
                userTimeline.addAll(action.get(Keys.RESULT_GET_USER_TIMELINE));
                break;
            case Actions.GET_USER_TIMELINE_UPDATES:
                // save timeline updates
                userTimelineUpdates = action.get(Keys.RESULT_GET_USER_TIMELINE_UPDATES);
                // push new tweets to userTimeline
                userTimeline.addAll(0, userTimelineUpdates);
                break;
            case Actions.GET_USER_TIMELINE_MORE:
                // save result
                userTimelineMore = action.get(Keys.RESULT_GET_USER_TIMELINE_MORE);
                // append to userTimeline
                userTimeline.addAll(userTimelineMore);
                break;
            case Actions.LOGOUT:
                onLogout();
                break;
            default:
                return;
        }

        postChange(new RxStoreChange(ID, action));
    }

    private void onLogout() {
        // clear timelines
        homeTimeline = null;
        homeTimelineUpdates = null;
        homeTimelineMore = null;
        userTimeline = null;
        userTimelineUpdates = null;
        userTimelineMore = null;
    }
}
