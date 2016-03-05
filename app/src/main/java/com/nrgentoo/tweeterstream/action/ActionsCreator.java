package com.nrgentoo.tweeterstream.action;

import com.hardsoftstudio.rxflux.action.RxAction;
import com.hardsoftstudio.rxflux.action.RxActionCreator;
import com.hardsoftstudio.rxflux.dispatcher.Dispatcher;
import com.hardsoftstudio.rxflux.util.SubscriptionManager;
import com.twitter.sdk.android.core.TwitterSession;

import rx.Observable;

/**
 * Actions creator
 */
public class ActionsCreator extends RxActionCreator implements Actions {

    // --------------------------------------------------------------------------------------------
    //      CONSTRUCTOR
    // --------------------------------------------------------------------------------------------

    public ActionsCreator(Dispatcher dispatcher, SubscriptionManager manager) {
        super(dispatcher, manager);
    }

    // --------------------------------------------------------------------------------------------
    //      METHODS
    // --------------------------------------------------------------------------------------------

    @Override
    public void saveSession(TwitterSession session) {
        final RxAction action = newRxAction(SAVE_SESSION, Keys.PARAM_SESSION, session);
        if (hasRxAction(action)) return;

        addRxAction(action, Observable.just(session)
                .subscribe(twitterSession -> {
                    // post action to store
                    postRxAction(action);
                    removeRxAction(action);
                }, throwable -> {
                    // post error
                    postError(action, throwable);
                    removeRxAction(action);
                }));
    }
}
