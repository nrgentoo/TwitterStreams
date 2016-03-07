package com.nrgentoo.tweeterstream.view.hometimeline;

import com.nrgentoo.tweeterstream.common.di.HasComponent;
import com.nrgentoo.tweeterstream.common.di.component.ActivityComponent;
import com.nrgentoo.tweeterstream.view.abstracttimeline.AbstractTimelineFragment;
import com.nrgentoo.tweeterstream.view.abstracttimeline.TimelinePresenter;

/**
 * Fragment with home timeline
 */
public class HomeTimeLineFragment extends AbstractTimelineFragment {

    @Override
    protected TimelinePresenter createPresenter() {
        return new HomeTimelinePresenter((HasComponent<ActivityComponent>) getActivity(), this);
    }
}
