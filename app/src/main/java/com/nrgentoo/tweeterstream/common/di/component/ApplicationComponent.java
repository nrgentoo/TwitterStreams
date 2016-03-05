package com.nrgentoo.tweeterstream.common.di.component;

import com.nrgentoo.tweeterstream.App;
import com.nrgentoo.tweeterstream.action.Actions;
import com.nrgentoo.tweeterstream.common.di.module.ApplicationModule;
import com.nrgentoo.tweeterstream.store.SessionStore;
import com.nrgentoo.tweeterstream.store.SessionStoreImpl;

import javax.inject.Singleton;

import dagger.Component;
import de.greenrobot.event.EventBus;

/**
 * Application Component
 */
@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    // expose to sub-graphs
    SessionStore sessionStore();
    EventBus eventBus();
    Actions actions();

    // injections
    void inject(SessionStoreImpl sessionStore);

    void inject(App app);
}
