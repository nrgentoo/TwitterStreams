package com.nrgentoo.tweeterstream.common.di.module;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.nrgentoo.tweeterstream.App;
import com.nrgentoo.tweeterstream.action.Actions;
import com.nrgentoo.tweeterstream.action.ActionsCreator;
import com.nrgentoo.tweeterstream.action.UpdateTimelineService;
import com.nrgentoo.tweeterstream.common.TwitterRxFlux;
import com.nrgentoo.tweeterstream.common.di.HasComponent;
import com.nrgentoo.tweeterstream.common.di.component.ApplicationComponent;
import com.nrgentoo.tweeterstream.network.TwitterApi;
import com.nrgentoo.tweeterstream.store.SessionStore;
import com.nrgentoo.tweeterstream.store.SessionStoreImpl;
import com.nrgentoo.tweeterstream.store.TimelineStore;
import com.nrgentoo.tweeterstream.store.TimelineStoreImpl;

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
    private SessionStore sessionStore;

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
        if (sessionStore == null) {
            sessionStore = new SessionStoreImpl(rxFlux.getDispatcher(), app);
        }
        return sessionStore;
    }

    @Singleton
    @Provides
    EventBus provideEventBus() {
        return EventBus.getDefault();
    }

    @Singleton
    @Provides
    Actions provideActions() {
        return new ActionsCreator(app, rxFlux.getDispatcher(), rxFlux.getSubscriptionManager());
    }

    @Singleton
    @Provides
    TwitterApi provideTwitterApi() {
        return new TwitterApi(sessionStore.getSession());
    }

    @Singleton
    @Provides
    TimelineStore provideTimelineStore() {
        return new TimelineStoreImpl(rxFlux.getDispatcher());
    }

    @Singleton
    @Provides
    UpdateTimelineService provideUpdateTimelineService() {
        return new UpdateTimelineService(app);
    }
}
