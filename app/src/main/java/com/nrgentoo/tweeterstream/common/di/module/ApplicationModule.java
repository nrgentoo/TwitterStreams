package com.nrgentoo.tweeterstream.common.di.module;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.nrgentoo.tweeterstream.App;
import com.nrgentoo.tweeterstream.action.Actions;
import com.nrgentoo.tweeterstream.action.ActionsCreator;
import com.nrgentoo.tweeterstream.common.TwitterRxFlux;
import com.nrgentoo.tweeterstream.common.di.HasComponent;
import com.nrgentoo.tweeterstream.common.di.component.ApplicationComponent;
import com.nrgentoo.tweeterstream.store.SessionStore;
import com.nrgentoo.tweeterstream.store.SessionStoreImpl;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.greenrobot.event.EventBus;

/**
 * Application Module
 */
@Module
public class ApplicationModule {

    private final App app;
    private final TwitterRxFlux rxFlux;

    public ApplicationModule(App application) {
        this.app = application;
        rxFlux = TwitterRxFlux.init(app);
    }

    public static final String SESSION_PREFERENCES = "session";

    @Singleton
    @Provides
    @Named(SESSION_PREFERENCES)
    SharedPreferences provideSessionPrefs() {
        return app.getSharedPreferences(SESSION_PREFERENCES, Context.MODE_PRIVATE);
    }

    @Singleton
    @Provides
    SessionStore provideSessionStore() {
        return new SessionStoreImpl(rxFlux.getDispatcher(), app);
    }

    @Singleton
    @Provides
    EventBus provideEventBus() {
        return EventBus.getDefault();
    }

    @Singleton
    @Provides
    Actions provideActions() {
        return new ActionsCreator(rxFlux.getDispatcher(), rxFlux.getSubscriptionManager());
    }
}
