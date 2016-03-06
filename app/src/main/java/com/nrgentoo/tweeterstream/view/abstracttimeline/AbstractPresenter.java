package com.nrgentoo.tweeterstream.view.abstracttimeline;

import com.nrgentoo.tweeterstream.action.Actions;
import com.nrgentoo.tweeterstream.common.di.HasComponent;
import com.nrgentoo.tweeterstream.common.di.component.ActivityComponent;
import com.nrgentoo.tweeterstream.store.TimelineStore;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

/**
 * Abstract timeline presenter
 */
public abstract class AbstractPresenter implements TimelinePresenter {

    // --------------------------------------------------------------------------------------------
    //      FIELDS
    // --------------------------------------------------------------------------------------------

    protected TimelineView timelineView;

    @Inject
    protected EventBus eventBus;

    @Inject
    protected Actions actions;

    @Inject
    protected TimelineStore timelineStore;

    protected long topId;
    protected long bottomId;

    // --------------------------------------------------------------------------------------------
    //      CONSTRUCTOR
    // --------------------------------------------------------------------------------------------

    public AbstractPresenter(HasComponent<ActivityComponent> hasActivityComponent,
                             TimelineView timelineView) {
        // inject
        hasActivityComponent.getComponent().inject(this);

        this.timelineView = timelineView;
    }

    // --------------------------------------------------------------------------------------------
    //      PUBLIC METHODS
    // --------------------------------------------------------------------------------------------

    @Override
    public void onCreate() {
        // register event bus
        eventBus.register(this);

        // get tweets
        getTimeline();
        timelineView.showProgress();
    }

    @Override
    public void onDestroy() {
        // unregister event bus
        eventBus.unregister(this);

        timelineView = null;
    }

    @Override
    public void getUpdates() {
        getTimelineUpdates();
    }

    @Override
    public void loadMore() {
        getTimelineMore();
    }

    // --------------------------------------------------------------------------------------------
    //      ABSTRACT METHODS
    // --------------------------------------------------------------------------------------------

    abstract protected void getTimeline();

    abstract protected void getTimelineUpdates();

    abstract protected void getTimelineMore();
}
