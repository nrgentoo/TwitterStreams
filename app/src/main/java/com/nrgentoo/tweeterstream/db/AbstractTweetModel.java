package com.nrgentoo.tweeterstream.db;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Abstract tweet model
 */
abstract public class AbstractTweetModel extends BaseModel {

    @PrimaryKey
    long id;

    @Column
    long createdAt;

    @Column
    String jsonData;

    public AbstractTweetModel(long id, long createdAt, String jsonData) {
        this.id = id;
        this.createdAt = createdAt;
        this.jsonData = jsonData;
    }

    public AbstractTweetModel() {
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
