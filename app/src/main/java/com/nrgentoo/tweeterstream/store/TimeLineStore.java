package com.nrgentoo.tweeterstream.store;

import android.support.annotation.Nullable;

import com.twitter.sdk.android.core.models.Tweet;

import java.util.List;

/**
 * Interface for timeline
 */
public interface TimelineStore {

    String ID = "TimelineStore";

    List<Tweet> getHomeTimeline();

    List<Tweet> getHomeTimelineUpdates();

    List<Tweet> getHomeTimelineMore();

    List<Tweet> getUserTimeline();

    List<Tweet> getUserTimelineUpdates();

    List<Tweet> getUserTimelineMore();
}
