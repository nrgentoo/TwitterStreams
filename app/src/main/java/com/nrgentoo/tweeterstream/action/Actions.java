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

    // --------------------------------------------------------------------------------------------
    //      METHODS
    // --------------------------------------------------------------------------------------------

    void saveSession(TwitterSession session);

    void getTimeLine(@Nullable Long sinceId, @Nullable Long maxId);
}
