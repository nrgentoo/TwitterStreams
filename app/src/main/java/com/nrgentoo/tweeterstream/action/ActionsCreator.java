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
import com.nrgentoo.tweeterstream.store.TimelineStore;
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

    @Inject
    TimelineStore timelineStore;

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
    public void getHomeTimeline() {
        final RxAction action = newRxAction(GET_HOME_TIMELINE,
                Keys.PARAM_COUNT, TWEETS_COUNT);
        if (hasRxAction(action)) return;

        Observable<List<Tweet>> tweetsObservable;

        if (timelineStore.getHomeTimeline() != null) {
            // return from memory
            tweetsObservable = Observable.just(timelineStore.getHomeTimeline());
        } else {
            // get from api
            tweetsObservable = apiLazy.get().getCustomService().getHomeTimeline(TWEETS_COUNT, null,
                    null, false, false, true, true);
        }

        addRxAction(action, tweetsObservable
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

    @Override
    public void getHomeTimelineMore(long maxId) {
        final RxAction action = newRxAction(GET_HOME_TIMELINE_MORE,
                Keys.PARAM_MAX_ID, maxId);
        if (hasRxAction(action)) return;

        addRxAction(action, apiLazy.get().getCustomService().getHomeTimeline(200, null, maxId,
                false, false, true, true)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tweets -> {
                    // post action with result
                    action.getData().put(Keys.RESULT_GET_HOME_TIMELINE_MORE, tweets);
                    postRxAction(action);
                    removeRxAction(action);
                }, throwable -> {
                    // post error
                    postError(action, throwable);
                    removeRxAction(action);
                }));
    }

    @Override
    public void getUserTimeline() {
        final RxAction action = newRxAction(GET_USER_TIMELINE);
        if (hasRxAction(action)) return;

        Observable<List<Tweet>> tweetsObservable;

        if (timelineStore.getHomeTimeline() != null) {
            // return from memory
            tweetsObservable = Observable.just(timelineStore.getUserTimeline());
        } else {
            // get from api
            tweetsObservable = apiLazy.get().getCustomService().getUserTimeline(null, null,
                    TWEETS_COUNT, null, null, false, false, true, true);
        }

        addRxAction(action, tweetsObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tweets -> {
                    action.getData().put(Keys.RESULT_GET_USER_TIMELINE, tweets);
                    postRxAction(action);
                    removeRxAction(action);
                }, throwable -> {
                    // post error
                    postError(action, throwable);
                    removeRxAction(action);
                }));
    }

    @Override
    public void getUserTimelineUpdates(long sinceId) {
        final RxAction action = newRxAction(GET_USER_TIMELINE_UPDATES,
                Keys.PARAM_SINCE_ID, sinceId);
        if (hasRxAction(action)) return;

        addRxAction(action, updateTimelineService.getUserTimelineUpdates(sinceId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tweets -> {
                    // post action with result
                    action.getData().put(Keys.RESULT_GET_USER_TIMELINE_UPDATES, tweets);
                    postRxAction(action);
                    removeRxAction(action);
                }, throwable -> {
                    // post error
                    postError(action, throwable);
                    removeRxAction(action);
                }));
    }

    @Override
    public void getUserTimelineMore(long maxId) {
        final RxAction action = newRxAction(GET_USER_TIMELINE_MORE,
                Keys.PARAM_MAX_ID, maxId);
        if (hasRxAction(action)) return;

        addRxAction(action, apiLazy.get().getCustomService().getUserTimeline(null, null, 200, null,
                maxId, false, false, true, true)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tweets -> {
                    // post action with result
                    action.getData().put(Keys.RESULT_GET_USER_TIMELINE_MORE, tweets);
                    postRxAction(action);
                    removeRxAction(action);
                }, throwable -> {
                    // post error
                    postError(action, throwable);
                    removeRxAction(action);
                }));
    }
}
