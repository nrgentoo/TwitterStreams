package com.nrgentoo.tweeterstream.action;

/**
 * Keys
 */
public interface Keys {

    // --------------------------------------------------------------------------------------------
    //      PARAM KEYS
    // --------------------------------------------------------------------------------------------

    String PARAM_SESSION = "param_session";
    String PARAM_COUNT = "param_count";
    String PARAM_SINCE_ID = "since_id";
    String PARAM_MAX_ID = "max_id";

    // --------------------------------------------------------------------------------------------
    //      RESULT KEYS
    // --------------------------------------------------------------------------------------------

    String RESULT_GET_HOME_TIMELINE = "result_get_home_timeline";
    String RESULT_GET_HOME_TIMELINE_UPDATES = "result_get_home_timeline_updates";
    String RESULT_GET_HOME_TIMELINE_MORE = "result_get_home_timeline_more";
    String RESULT_GET_USER_TIMELINE = "result_get_user_timeline";
    String RESULT_GET_USER_TIMELINE_UPDATES = "result_get_user_timeline_updates";
    String RESULT_GET_USER_TIMELINE_MORE = "result_get_user_timeline_more";
    String PARAM_FROM_DB = "param_from_db";
}
