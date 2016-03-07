package com.nrgentoo.tweeterstream.view.usertimeline;

import com.nrgentoo.tweeterstream.common.di.HasComponent;
import com.nrgentoo.tweeterstream.common.di.component.ActivityComponent;
import com.nrgentoo.tweeterstream.view.abstracttimeline.AbstractTimelineFragment;
import com.nrgentoo.tweeterstream.view.abstracttimeline.TimelinePresenter;

/**
 * User timeline fragment
 */
public class UserTimelineFragment extends AbstractTimelineFragment {
    @Override
    protected TimelinePresenter createPresenter() {
        return new UserTimelinePresenter((HasComponent<ActivityComponent>) getActivity(), this);
    }
}
