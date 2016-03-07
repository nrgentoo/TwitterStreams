package com.nrgentoo.tweeterstream.action;

import com.google.gson.Gson;
import com.hardsoftstudio.rxflux.action.RxAction;
import com.hardsoftstudio.rxflux.action.RxActionCreator;
import com.hardsoftstudio.rxflux.dispatcher.Dispatcher;
import com.hardsoftstudio.rxflux.util.SubscriptionManager;
import com.nrgentoo.tweeterstream.common.di.HasComponent;
import com.nrgentoo.tweeterstream.common.di.component.ApplicationComponent;
import com.nrgentoo.tweeterstream.db.HomeTweetModel;
import com.nrgentoo.tweeterstream.db.HomeTweetModel_Table;
import com.nrgentoo.tweeterstream.network.TwitterApi;
import com.nrgentoo.tweeterstream.store.TimelineStore;
import com.raizlabs.android.dbflow.runtime.TransactionManager;
import com.raizlabs.android.dbflow.runtime.transaction.process.ProcessModelInfo;
import com.raizlabs.android.dbflow.runtime.transaction.process.SaveModelTransaction;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Tweet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import dagger.Lazy;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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
            // get from db or api
            tweetsObservable = Observable.defer(() -> {
                List<HomeTweetModel> tweetModels = SQLite.select()
                        .from(HomeTweetModel.class)
                        .orderBy(HomeTweetModel_Table.id, false) // order by id, descending
                        .queryList();

                if (!tweetModels.isEmpty()) {
                    // get from local db
                    Gson gson = new Gson();

                    // action retrieved from db
                    action.getData().put(Keys.PARAM_FROM_DB, true);

                    return Observable.from(tweetModels)
                            .flatMap(homeTweetModel -> Observable
                                    .just(gson.fromJson(homeTweetModel.getJsonData(), Tweet.class)))
                            .toList();
                } else {
                    // get from api
                    return apiLazy.get().getCustomService().getHomeTimeline(TWEETS_COUNT, null,
                            null, false, false, true, true);
                }
            });
        }

        addRxAction(action, tweetsObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tweets -> {
                    // insert to db
                    insertHomeTweets(tweets);

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
                    // insert to db
                    insertHomeTweets(tweets);

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
                    // insert to db
                    insertHomeTweets(tweets);

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

    @Override
    public void logout() {
        final RxAction action = newRxAction(LOGOUT);

        // post action
        postRxAction(action);
    }

    /**
     * Insert tweets to local db
     *
     * @param tweets list of new tweets
     */
    private static void insertHomeTweets(List<Tweet> tweets) {
        // insert to db
        List<HomeTweetModel> homeTweetModels = new ArrayList<>();
        Gson gson = new Gson();

        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d HH:mm:ss zz yyyy");

        for (Tweet tweet : tweets) {
            try {
                long dateMillis = sdf.parse(tweet.createdAt).getTime();

                String tweetJson = gson.toJson(tweet);
                HomeTweetModel homeTweetModel = new HomeTweetModel(tweet.id, dateMillis,
                        tweetJson);
                homeTweetModels.add(homeTweetModel);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        // add transaction
        SaveModelTransaction<HomeTweetModel> transaction =
                new SaveModelTransaction<>(ProcessModelInfo.withModels(homeTweetModels));
        TransactionManager.getInstance()
                .addTransaction(transaction);
    }
}
