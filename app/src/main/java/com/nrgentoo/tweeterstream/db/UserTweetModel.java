package com.nrgentoo.tweeterstream.db;

import com.raizlabs.android.dbflow.annotation.Table;

/**
 * Table with tweets from home timeline
 */
@Table(database = TwitterTimelineDB.class)
public class UserTweetModel extends AbstractTweetModel {

    public UserTweetModel(long id, long createdAt, String jsonData) {
        super(id, createdAt, jsonData);
    }

    public UserTweetModel() {
    }
}
