package com.nrgentoo.tweeterstream.action;

import android.support.annotation.Nullable;

import com.nrgentoo.tweeterstream.common.di.HasComponent;
import com.nrgentoo.tweeterstream.common.di.component.ApplicationComponent;
import com.nrgentoo.tweeterstream.network.CustomService;
import com.nrgentoo.tweeterstream.network.TwitterApi;
import com.twitter.sdk.android.core.models.Tweet;

import java.util.List;

import javax.inject.Inject;

import dagger.Lazy;
import rx.Observable;

/**
 * Service class for recursive timeline update
 */
public class UpdateTimelineService {

    // --------------------------------------------------------------------------------------------
    //      FIELDS
    // --------------------------------------------------------------------------------------------

    @Inject
    Lazy<TwitterApi> apiLazy;

    // --------------------------------------------------------------------------------------------
    //      CONSTRUCTOR
    // --------------------------------------------------------------------------------------------

    public UpdateTimelineService(HasComponent<ApplicationComponent> hasApplicationComponent) {
        hasApplicationComponent.getComponent().inject(this);
    }

    // --------------------------------------------------------------------------------------------
    //      PUBLIC METHODS
    // --------------------------------------------------------------------------------------------

    /**
     * Get home timeline updates
     */
    public Observable<List<Tweet>> getHomeTimelineUpdates(long sinceId) {
        Observable<TimelineResponse> call = createHomeTimelineCall(sinceId, null);
        return flatten(call).toList();
    }

    // --------------------------------------------------------------------------------------------
    //      PRIVATE METHODS
    // --------------------------------------------------------------------------------------------

    /**
     * Recursive retrieving new tweets from since_id
     * @return tweets chained with next observable
     */
    private Observable<TimelineResponse> createHomeTimelineCall(long sinceId, @Nullable Long maxId) {
        CustomService customService = apiLazy.get().getCustomService();
        return customService.getHomeTimeline(200, sinceId,
                maxId, false, false, true, true)
                .flatMap(tweets -> {
                    if (!tweets.isEmpty()) {
                        long newMaxId = tweets.get(tweets.size() - 1).id - 1;
                        return Observable.just(new TimelineResponse(tweets, createHomeTimelineCall(sinceId, newMaxId)));
                    } else {
                        return Observable.just(new TimelineResponse(tweets, null));
                    }
                });
    }

    /**
     * Flatten all recursive responses to one list of tweets
     */
    private Observable<Tweet> flatten(Observable<TimelineResponse> responseObservable) {
        if (responseObservable == null) {
            return Observable.empty();
        } else return responseObservable.flatMap(response -> {
            Observable<TimelineResponse> next = response.getNext();
            List<Tweet> tweets = response.getTweets();
            return Observable.from(tweets).concatWith(flatten(next));
        });
    }

    /**
     * Helper class for chaining observables
     */
    private class TimelineResponse {
        private final List<Tweet> tweets;
        private final Observable<TimelineResponse> next;

        public TimelineResponse(List<Tweet> tweets, Observable<TimelineResponse> next) {
            this.tweets = tweets;
            this.next = next;
        }

        public List<Tweet> getTweets() {
            return tweets;
        }

        public Observable<TimelineResponse> getNext() {
            return next;
        }
    }
}
