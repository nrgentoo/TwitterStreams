package com.nrgentoo.tweeterstream.common.di.component;

import com.nrgentoo.tweeterstream.common.di.module.ApplicationModule;
import com.nrgentoo.tweeterstream.store.SessionStore;
import com.nrgentoo.tweeterstream.store.SessionStoreImpl;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Application Component
 */
@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    // expose to sub-graphs
    SessionStore sessionStore();

    // injections
    void inject(SessionStoreImpl sessionStore);
}
