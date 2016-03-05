package com.nrgentoo.tweeterstream.action;

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

    // --------------------------------------------------------------------------------------------
    //      METHODS
    // --------------------------------------------------------------------------------------------

    void saveSession(TwitterSession session);
}
