package com.nrgentoo.tweeterstream.db;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Application database
 */
@Database(name = TwitterTimelineDB.NAME, version = TwitterTimelineDB.VERSION)
public class TwitterTimelineDB {

    public static final String NAME = "TwitterTimeline";

    public static final int VERSION = 1;
}
