package com.nrgentoo.tweeterstream.view;

import android.support.v7.app.AppCompatActivity;

import com.nrgentoo.tweeterstream.App;
import com.nrgentoo.tweeterstream.common.di.HasComponent;
import com.nrgentoo.tweeterstream.common.di.component.ActivityComponent;
import com.nrgentoo.tweeterstream.common.di.component.DaggerActivityComponent;
import com.nrgentoo.tweeterstream.common.di.module.ActivityModule;

/**
 * Abstract activity
 */
abstract public class AbstractActivity extends AppCompatActivity
        implements HasComponent<ActivityComponent> {

    // --------------------------------------------------------------------------------------------
    //      FIELDS
    // --------------------------------------------------------------------------------------------

    private ActivityComponent activityComponent;

    // --------------------------------------------------------------------------------------------
    //      HAS COMPONENT INTERFACE
    // --------------------------------------------------------------------------------------------

    @Override
    public ActivityComponent getComponent() {
        if (activityComponent == null) {
            activityComponent = DaggerActivityComponent.builder()
                    .activityModule(new ActivityModule(this))
                    .applicationComponent(App.getInstance().getComponent())
                    .build();
        }

        return activityComponent;
    }
}
