package com.nrgentoo.tweeterstream.view.abstracttimeline;

/**
 * Timeline presenter
 */
public interface TimelinePresenter {

    void onCreate();

    void onDestroy();

    void getUpdates();

    void loadMore();
}
