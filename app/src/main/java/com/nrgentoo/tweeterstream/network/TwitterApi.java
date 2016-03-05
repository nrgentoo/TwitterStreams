package com.nrgentoo.tweeterstream.network;

import com.twitter.sdk.android.core.AuthToken;
import com.twitter.sdk.android.core.Session;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;

/**
 * Twitter API Client
 */
public class TwitterApi extends TwitterApiClient {

    public TwitterApi(Session session) {
        super(session);
    }
}
