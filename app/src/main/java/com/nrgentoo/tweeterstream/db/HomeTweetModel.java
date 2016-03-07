package com.nrgentoo.tweeterstream.db;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Table with tweets from home timeline
 */
@Table(database = TwitterTimelineDB.class)
public class HomeTweetModel extends BaseModel {

    @PrimaryKey
    long id;

    @Column
    long createdAt;

    @Column
    String jsonData;

    public HomeTweetModel(long id, long createdAt, String jsonData) {
        this.id = id;
        this.createdAt = createdAt;
        this.jsonData = jsonData;
    }

    public HomeTweetModel() {
        // empty constructor
    }

    public long getId() {
        return id;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public String getJsonData() {
        return jsonData;
    }
}
