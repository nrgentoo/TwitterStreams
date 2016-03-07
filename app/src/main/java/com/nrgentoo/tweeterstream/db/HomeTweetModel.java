package com.nrgentoo.tweeterstream.db;

import com.raizlabs.android.dbflow.annotation.Table;

/**
 * Table with tweets from home timeline
 */
@Table(database = TwitterTimelineDB.class)
public class HomeTweetModel extends AbstractTweetModel {

    public HomeTweetModel(long id, long createdAt, String jsonData) {
        super(id, createdAt, jsonData);
    }

    public HomeTweetModel() {
    }
}
