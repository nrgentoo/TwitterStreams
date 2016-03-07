package com.nrgentoo.tweeterstream.store;

import android.content.SharedPreferences;
import android.text.TextUtils;

import com.hardsoftstudio.rxflux.action.RxAction;
import com.hardsoftstudio.rxflux.dispatcher.Dispatcher;
import com.hardsoftstudio.rxflux.store.RxStore;
import com.hardsoftstudio.rxflux.store.RxStoreChange;
import com.nrgentoo.tweeterstream.action.Actions;
import com.nrgentoo.tweeterstream.action.Keys;
import com.nrgentoo.tweeterstream.common.di.HasComponent;
import com.nrgentoo.tweeterstream.common.di.component.ApplicationComponent;
import com.nrgentoo.tweeterstream.common.di.module.ApplicationModule;
import com.twitter.sdk.android.core.Session;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterSession;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Session store implementation
 */
public class SessionStoreImpl extends RxStore implements SessionStore {

    private static final String KEY_TOKEN = "token";
    private static final String KEY_SECRET = "secret";
    private static final String KEY_ID = "id";

    @Inject
    @Named(ApplicationModule.SESSION_PREFERENCES)
    SharedPreferences sessionPrefs;

    private Session<TwitterAuthToken> session;

    public SessionStoreImpl(Dispatcher dispatcher, HasComponent<ApplicationComponent> hasComponent) {
        super(dispatcher);
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

    @Override
    public void onRxAction(RxAction action) {
        switch (action.getType()) {
            case Actions.SAVE_SESSION:
                // save session to shared preferences
                saveSession(action.get(Keys.PARAM_SESSION));
                break;
            case Actions.LOGOUT:
                // remove session from shared preferences
                onLogout();
                break;
            default:
                return;
        }

        postChange(new RxStoreChange(ID, action));
    }

    /**
     * Save received session to shared preferences
     *
     * @param session received session
     */
    private void saveSession(TwitterSession session) {
        sessionPrefs.edit()
                .putString(KEY_TOKEN, session.getAuthToken().token)
                .putString(KEY_SECRET, session.getAuthToken().secret)
                .putLong(KEY_ID, session.getUserId())
                .apply();
        this.session = session;
    }

    private void onLogout() {
        sessionPrefs.edit()
                .remove(KEY_TOKEN)
                .remove(KEY_SECRET)
                .remove(KEY_ID)
                .apply();
        this.session = null;
    }
}
