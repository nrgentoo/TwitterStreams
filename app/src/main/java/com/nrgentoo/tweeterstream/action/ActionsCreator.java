package com.nrgentoo.tweeterstream.action;

import com.google.gson.Gson;
import com.hardsoftstudio.rxflux.action.RxAction;
import com.hardsoftstudio.rxflux.action.RxActionCreator;
import com.hardsoftstudio.rxflux.dispatcher.Dispatcher;
import com.hardsoftstudio.rxflux.util.SubscriptionManager;
import com.nrgentoo.tweeterstream.common.di.HasComponent;
import com.nrgentoo.tweeterstream.common.di.component.ApplicationComponent;
import com.nrgentoo.tweeterstream.db.AbstractTweetModel;
import com.nrgentoo.tweeterstream.db.HomeTweetModel;
import com.nrgentoo.tweeterstream.db.HomeTweetModel_Table;
import com.nrgentoo.tweeterstream.db.UserTweetModel;
import com.nrgentoo.tweeterstream.db.UserTweetModel_Table;
import com.nrgentoo.tweeterstream.network.TwitterApi;
import com.nrgentoo.tweeterstream.store.TimelineStore;
import com.raizlabs.android.dbflow.runtime.TransactionManager;
import com.raizlabs.android.dbflow.runtime.transaction.process.ProcessModelInfo;
import com.raizlabs.android.dbflow.runtime.transaction.process.SaveModelTransaction;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.property.IProperty;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.User;

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
            tweetsObservable = getTweetsFromDbOrApi(HomeTweetModel.class, HomeTweetModel_Table.id,
                    action, apiLazy.get().getCustomService().getHomeTimeline(TWEETS_COUNT, null,
                            null, false, false, true, true));
        }

        addRxAction(action, tweetsObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tweets -> {
                    // insert to db
                    insertHomeTweets(tweets, HomeTweetModel::new);

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
                    insertHomeTweets(tweets, HomeTweetModel::new);

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
                    insertHomeTweets(tweets, HomeTweetModel::new);

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
            // get from db or api
            tweetsObservable = getTweetsFromDbOrApi(UserTweetModel.class, UserTweetModel_Table.id,
                    action, apiLazy.get().getCustomService().getUserTimeline(null, null,
                            TWEETS_COUNT, null, null, false, false, true, true));
        }

        addRxAction(action, tweetsObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tweets -> {
                    // insert to db
                    insertHomeTweets(tweets, UserTweetModel::new);

                    // post action with result
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
                    // insert to db
                    insertHomeTweets(tweets, UserTweetModel::new);

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
                    // insert to db
                    insertHomeTweets(tweets, UserTweetModel::new);

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

    // --------------------------------------------------------------------------------------------
    //      PRIVATE METHODS
    // --------------------------------------------------------------------------------------------

    /**
     * Get tweets from db or api
     *
     * @param klass model class
     * @param idProperty sort property
     * @param action calling action
     * @param tweetsAPIObservable api tweets observable
     * @param <T> model type
     * @return tweets observable from db or api
     */
    private static <T extends AbstractTweetModel> Observable<List<Tweet>> getTweetsFromDbOrApi(
            Class<T> klass,
            IProperty idProperty,
            RxAction action,
            Observable<List<Tweet>> tweetsAPIObservable) {
        return Observable.defer(() -> {
            List<T> tweetModels = SQLite.select()
                    .from(klass)
                    .orderBy(idProperty, false)
                    .queryList();

            if (!tweetModels.isEmpty()) {
                // get from local db
                Gson gson = new Gson();

                // action retrieved from db
                action.getData().put(Keys.PARAM_FROM_DB, true);

                return Observable.from(tweetModels)
                        .flatMap(tweetModel -> Observable
                                .just(gson.fromJson(tweetModel.getJsonData(), Tweet.class)))
                        .toList();
            } else {
                // get from api
                return tweetsAPIObservable;
            }
        });
    }

    /**
     * Insert tweets to local db
     *
     * @param tweets list of new tweets
     */
    private static void insertHomeTweets(List<Tweet> tweets, TweetModelBuilder builder) {
        // insert into db
        List<AbstractTweetModel> tweetModels = new ArrayList<>();
        Gson gson = new Gson();

        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d HH:mm:ss zz yyyy");

        for (Tweet tweet : tweets) {
            try {
                long dateMillis = sdf.parse(tweet.createdAt).getTime();

                String tweetJson = gson.toJson(tweet);
                AbstractTweetModel tweetModel = builder.createTweetModel(tweet.id, dateMillis,
                        tweetJson);
                tweetModels.add(tweetModel);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        // add transaction
        SaveModelTransaction<AbstractTweetModel> transaction =
                new SaveModelTransaction<>(ProcessModelInfo.withModels(tweetModels));
        TransactionManager.getInstance()
                .addTransaction(transaction);
    }

    private interface TweetModelBuilder {

        AbstractTweetModel createTweetModel(long id, long dateMillis, String tweetJson);
    }
}
