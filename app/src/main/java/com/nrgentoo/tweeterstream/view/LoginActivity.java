package com.nrgentoo.tweeterstream.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.hardsoftstudio.rxflux.action.RxError;
import com.hardsoftstudio.rxflux.store.RxStoreChange;
import com.nrgentoo.tweeterstream.R;
import com.nrgentoo.tweeterstream.action.Actions;
import com.nrgentoo.tweeterstream.store.SessionStore;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

/**
 * Login activity
 */
public class LoginActivity extends AbstractActivity {

    public static void launch(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS |
                Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    // --------------------------------------------------------------------------------------------
    //      FIELDS
    // --------------------------------------------------------------------------------------------

    @Inject
    Actions actions;

    @Inject
    EventBus eventBus;

    // --------------------------------------------------------------------------------------------
    //      UI REFERENCES
    // --------------------------------------------------------------------------------------------

    TwitterLoginButton loginButton;

    // --------------------------------------------------------------------------------------------
    //      LIFECYCLE
    // --------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // inject
        getComponent().inject(this);

        // register event bus
        eventBus.register(this);

        setContentView(R.layout.activity_login);

        // inflate views
        loginButton = (TwitterLoginButton) findViewById(R.id.login_button);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // unregister event bus
        eventBus.unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // set listeners
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                actions.saveSession(result.data);
            }

            @Override
            public void failure(TwitterException e) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // pass the activity result to the Login button
        loginButton.onActivityResult(requestCode, resultCode, data);
    }

    // --------------------------------------------------------------------------------------------
    //      RX FLUX EVENTS
    // --------------------------------------------------------------------------------------------

    @SuppressWarnings("unused")
    public void onEvent(RxStoreChange change) {
        switch (change.getStoreId()) {
            case SessionStore.ID:
                // login complete, return back to the MainActivity
                finish();

                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
        }
    }

    @SuppressWarnings("unused")
    public void onEvent(RxError error) {

    }
}
