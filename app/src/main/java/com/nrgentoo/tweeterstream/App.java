package com.nrgentoo.tweeterstream;

import android.app.Application;

import com.hardsoftstudio.rxflux.action.RxError;
import com.hardsoftstudio.rxflux.dispatcher.RxViewDispatch;
import com.hardsoftstudio.rxflux.store.RxStoreChange;
import com.nrgentoo.tweeterstream.common.di.HasComponent;
import com.nrgentoo.tweeterstream.common.di.component.ApplicationComponent;
import com.nrgentoo.tweeterstream.common.di.component.DaggerApplicationComponent;
import com.nrgentoo.tweeterstream.common.di.module.ApplicationModule;
import com.nrgentoo.tweeterstream.store.SessionStore;
import com.nrgentoo.tweeterstream.store.SessionStoreImpl;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import io.fabric.sdk.android.Fabric;

/**
 * Application class
 */
public class App extends Application implements HasComponent<ApplicationComponent>, RxViewDispatch {

    // --------------------------------------------------------------------------------------------
    //      FIELDS
    // --------------------------------------------------------------------------------------------

    private static App INSTANCE;

    @Inject
    EventBus eventBus;

    @Inject
    SessionStore sessionStore;

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "39o3m8YZ8TuR3VEjZnXDhNKCX";
    private static final String TWITTER_SECRET = "cDqpQ5cqUu6AF3qwKFBhGElJDsEg8CrMsOVomsICWNf6UDhppY";

    private ApplicationComponent applicationComponent;

    // --------------------------------------------------------------------------------------------
    //      PUBLIC METHODS
    // --------------------------------------------------------------------------------------------

    public static App getInstance() {
        return INSTANCE;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;

        // inject
        getComponent().inject(this);

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
    }

    @Override
    public ApplicationComponent getComponent() {
        if (applicationComponent == null) {
            applicationComponent = DaggerApplicationComponent.builder()
                    .applicationModule(new ApplicationModule(this))
                    .build();
        }

        return applicationComponent;
    }

    // --------------------------------------------------------------------------------------------
    //      RX VIEW DISPATCH
    // --------------------------------------------------------------------------------------------

    @Override
    public void onRxStoreChanged(RxStoreChange change) {
        // dispatch event with change
        eventBus.post(change);
    }

    @Override
    public void onRxError(RxError error) {
        // dispatch event with error
        eventBus.post(error);
    }

    @Override
    public void onRxViewRegistered() {

    }

    @Override
    public void onRxViewUnRegistered() {

    }

    @Override
    public void onRxStoresRegister() {
        // register stores
        ((SessionStoreImpl) sessionStore).register();
    }
}
