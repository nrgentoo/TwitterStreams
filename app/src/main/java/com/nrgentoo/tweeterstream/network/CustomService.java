package com.nrgentoo.tweeterstream.network;

import android.support.annotation.Nullable;

import com.twitter.sdk.android.core.models.Tweet;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

/**
 * Custom Twitter Service
 */
public interface CustomService {

    @GET("/1.1/statuses/home_timeline.json")
    Observable<List<Tweet>> getHomeTimeline(
            @Query("count") @Nullable Integer count,
            @Query("since_id") @Nullable Long sinceId,
            @Query("max_id") @Nullable Long maxId,
            @Query("trim_user") @Nullable Boolean trimUser,
            @Query("exclude_replies") @Nullable Boolean excludeReplies,
            @Query("contributor_details") @Nullable Boolean contributorDetails,
            @Query("include_entities") @Nullable Boolean includeEntites);
}
