package com.nrgentoo.tweeterstream.action;

import android.support.annotation.Nullable;

import com.hardsoftstudio.rxflux.action.RxAction;
import com.hardsoftstudio.rxflux.action.RxActionCreator;
import com.hardsoftstudio.rxflux.dispatcher.Dispatcher;
import com.hardsoftstudio.rxflux.util.SubscriptionManager;
import com.nrgentoo.tweeterstream.common.di.HasComponent;
import com.nrgentoo.tweeterstream.common.di.component.ApplicationComponent;
import com.nrgentoo.tweeterstream.network.CustomService;
import com.nrgentoo.tweeterstream.network.TwitterApi;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Tweet;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.Lazy;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

/**
 * Actions creator
 */
public class ActionsCreator extends RxActionCreator implements Actions {

    private static final int TWEETS_COUNT = 20;

    // --------------------------------------------------------------------------------------------
    //      FIELDS
    // --------------------------------------------------------------------------------------------

    @Inject
    Lazy<TwitterApi> apiLazy;

    @Inject
    UpdateTimelineService updateTimelineService;

    // --------------------------------------------------------------------------------------------
    //      CONSTRUCTOR
    // --------------------------------------------------------------------------------------------

    public ActionsCreator(HasComponent<ApplicationComponent> hasApplicationComponent,
                          Dispatcher dispatcher, SubscriptionManager manager) {
        super(dispatcher, manager);

        // inject
        hasApplicationComponent.getComponent().inject(this);
    }

    // --------------------------------------------------------------------------------------------
    //      METHODS
    // --------------------------------------------------------------------------------------------

    @Override
    public void saveSession(TwitterSession session) {
        final RxAction action = newRxAction(SAVE_SESSION, Keys.PARAM_SESSION, session);
        if (hasRxAction(action)) return;

        addRxAction(action, Observable.just(session)
                .subscribe(twitterSession -> {
                    // post action to store
                    postRxAction(action);
                    removeRxAction(action);
                }, throwable -> {
                    // post error
                    postError(action, throwable);
                    removeRxAction(action);
                }));
    }

    @Override
    public void getTimeLine(@Nullable Long sinceId, @Nullable Long maxId) {
        final RxAction action = newRxAction(GET_HOME_TIMELINE,
                Keys.PARAM_COUNT, TWEETS_COUNT);

        if (sinceId != null) action.getData().put(Keys.PARAM_SINCE_ID, sinceId);
        if (maxId != null) action.getData().put(Keys.PARAM_MAX_ID, maxId);

        if (hasRxAction(action)) return;

        addRxAction(action, apiLazy.get().getCustomService().getHomeTimeline(TWEETS_COUNT, sinceId,
                maxId, false, false, true, true)
                .delay(3, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tweets -> {
                    // post action with result
                    action.getData().put(Keys.RESULT_GET_HOME_TIMELINE, tweets);
                    postRxAction(action);
                    removeRxAction(action);
                }, throwable -> {
                    // post error
                    postError(action, throwable);
                    removeRxAction(action);
                }));
    }

    @Override
    public void getHomeTimelineUpdates(long sinceId) {
        final RxAction action = newRxAction(GET_HOME_TIMELINE_UPDATES,
                Keys.PARAM_SINCE_ID, sinceId);
        if (hasRxAction(action)) return;

        addRxAction(action, updateTimelineService.getHomeTimelineUpdates(sinceId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tweets -> {
                    // post action with result
                    action.getData().put(Keys.RESULT_GET_HOME_TIMELINE_UPDATES, tweets);
                    postRxAction(action);
                    removeRxAction(action);
                }, throwable -> {
                    // post error
                    postError(action, throwable);
                    removeRxAction(action);
                }));
    }
}
