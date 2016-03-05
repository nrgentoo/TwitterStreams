package com.nrgentoo.tweeterstream.common;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.hardsoftstudio.rxflux.dispatcher.Dispatcher;
import com.hardsoftstudio.rxflux.dispatcher.RxBus;
import com.hardsoftstudio.rxflux.dispatcher.RxViewDispatch;
import com.hardsoftstudio.rxflux.util.SubscriptionManager;

/**
 * Rx flux main class
 */
public class TwitterRxFlux implements Application.ActivityLifecycleCallbacks {

    // --------------------------------------------------------------------------------------------
    //      FIELDS
    // --------------------------------------------------------------------------------------------

    private static TwitterRxFlux INSTANCE;
    private final RxBus rxBus;
    private final Dispatcher dispatcher;
    private final SubscriptionManager subscriptionManager;
    private final RxViewDispatch applicationViewDispatch;
    private int activityCounter;

    // --------------------------------------------------------------------------------------------
    //      CONSTRUCTOR
    // --------------------------------------------------------------------------------------------

    private <T extends Application & RxViewDispatch>TwitterRxFlux(T application) {
        this.rxBus = RxBus.getInstance();
        this.dispatcher = Dispatcher.getInstance(rxBus);
        this.subscriptionManager = SubscriptionManager.getInstance();
        this.applicationViewDispatch = application;
        activityCounter = 0;
        application.registerActivityLifecycleCallbacks(this);
    }

    public static <T extends Application & RxViewDispatch> TwitterRxFlux init(T application) {
        if (INSTANCE != null) throw new IllegalStateException("Init was already called");
        return INSTANCE = new TwitterRxFlux(application);
    }

    // --------------------------------------------------------------------------------------------
    //      PUBLIC METHODS
    // --------------------------------------------------------------------------------------------

    public static void shutdown() {
        if (INSTANCE == null) return;
        INSTANCE.subscriptionManager.clear();
        INSTANCE.dispatcher.unregisterAll();
    }

    /**
     * @return the instance of the RxBus in case you want to reused for something else
     */
    public RxBus getRxBus() {
        return rxBus;
    }

    /**
     * @return the instance of the dispatcher
     */
    public Dispatcher getDispatcher() {
        return dispatcher;
    }

    /**
     * @return the instance of the subscription manager in case you want to reuse for something else
     */
    public SubscriptionManager getSubscriptionManager() {
        return subscriptionManager;
    }

    // --------------------------------------------------------------------------------------------
    //      ACTIVITY LIFECYCLE CALLBACKS
    // --------------------------------------------------------------------------------------------

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        activityCounter++;
        applicationViewDispatch.onRxStoresRegister();
        dispatcher.registerRxStore(applicationViewDispatch);
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        applicationViewDispatch.onRxStoresRegister();
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        activityCounter--;

        if (activityCounter == 0) {
            TwitterRxFlux.shutdown();
        }
    }
}
