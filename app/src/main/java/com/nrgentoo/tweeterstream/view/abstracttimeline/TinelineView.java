package com.nrgentoo.tweeterstream.view.abstracttimeline;

import android.support.annotation.Nullable;

import com.twitter.sdk.android.core.models.Tweet;

import java.util.List;

/**
 * View interface for home timeline
 */
public interface TinelineView {

    void showProgress();

    void hideProgress();

    void addItems(boolean atTop, List<Tweet> tweets);

    void setItems(List<Tweet> tweets);

    void showMessage(String message);
}
