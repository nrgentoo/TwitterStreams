package com.nrgentoo.tweeterstream.db;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Table with tweets from home timeline
 */
@Table(database = TwitterTimelineDB.class)
public class UserTweetModel extends BaseModel {

    @PrimaryKey
    long id;

    @Column
    long createdAt;

    @Column
    String jsonData;
}
