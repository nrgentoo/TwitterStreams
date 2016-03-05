package com.nrgentoo.tweeterstream.store;

import android.content.SharedPreferences;
import android.text.TextUtils;

import com.nrgentoo.tweeterstream.common.di.HasComponent;
import com.nrgentoo.tweeterstream.common.di.component.ApplicationComponent;
import com.nrgentoo.tweeterstream.common.di.module.ApplicationModule;
import com.twitter.sdk.android.core.AuthToken;
import com.twitter.sdk.android.core.Session;
import com.twitter.sdk.android.core.TwitterAuthToken;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Session store implementation
 */
public class SessionStoreImpl implements SessionStore {

    private static final String KEY_TOKEN = "token";
    private static final String KEY_SECRET = "secret";
    private static final String KEY_ID = "id";

    @Inject
    @Named(ApplicationModule.SESSION_PREFERENCES)
    SharedPreferences sessionPrefs;

    private Session<TwitterAuthToken> session;

    public SessionStoreImpl(HasComponent<ApplicationComponent> hasComponent) {
        hasComponent.getComponent().inject(this);
    }

    @Override
    public Session getSession() {
        if (session == null) {
            // get from shared preferences
            String token = sessionPrefs.getString(KEY_TOKEN, null);
            String secret = sessionPrefs.getString(KEY_SECRET, null);
            long id = sessionPrefs.getLong(KEY_ID, -1);

            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(secret) && id > 0) {
                TwitterAuthToken authToken = new TwitterAuthToken(token, secret);
                session = new Session<>(authToken, id);
            }
        }

        return session;
    }
}
