package com.nrgentoo.tweeterstream.store;

import com.twitter.sdk.android.core.Session;

/**
 * Session store
 */
public interface SessionStore {

    String ID = "SessionStore";

    Session getSession();
}
