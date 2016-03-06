package com.nrgentoo.tweeterstream.view.hometimeline;

/**
 * Timeline presenter
 */
public interface TimelinePresenter {

    void onCreate();

    void onDestroy();

    void getUpdates();

    void loadMore();
}
