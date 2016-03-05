package com.nrgentoo.tweeterstream.common.di.module;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.nrgentoo.tweeterstream.App;
import com.nrgentoo.tweeterstream.common.di.HasComponent;
import com.nrgentoo.tweeterstream.common.di.component.ApplicationComponent;
import com.nrgentoo.tweeterstream.store.SessionStore;
import com.nrgentoo.tweeterstream.store.SessionStoreImpl;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Application Module
 */
@Module
public class ApplicationModule {

    private App app;

    public ApplicationModule(App application) {
        this.app = application;
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
        return new SessionStoreImpl(app);
    }
}
