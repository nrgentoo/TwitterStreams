package com.nrgentoo.tweeterstream.action;

import android.support.annotation.Nullable;

import com.twitter.sdk.android.core.Session;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterSession;

/**
 * All application actions
 */
public interface Actions {

    // --------------------------------------------------------------------------------------------
    //      ACTION STRINGS
    // --------------------------------------------------------------------------------------------

    String SAVE_SESSION = "save_session";
    String GET_HOME_TIMELINE = "get_home_timeline";
    String GET_HOME_TIMELINE_UPDATES = "get_home_timeline_updates";
    String GET_HOME_TIMELINE_MORE = "get_home_timeline_more";
    String GET_USER_TIMELINE = "get_user_timeline";
    String GET_USER_TIMELINE_UPDATES = "get_user_timeline_updates";
    String GET_USER_TIMELINE_MORE = "get_user_timeline_more";

    // --------------------------------------------------------------------------------------------
    //      METHODS
    // --------------------------------------------------------------------------------------------

    void saveSession(TwitterSession session);

    void getHomeTimeline();

    void getHomeTimelineUpdates(long sinceId);

    void getHomeTimelineMore(long maxId);

    void getUserTimeline();

    void getUserTimelineUpdates(long sinceId);

    void getUserTimelineMore(long maxId);
}
