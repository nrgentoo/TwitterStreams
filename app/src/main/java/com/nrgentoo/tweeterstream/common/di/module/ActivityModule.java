package com.nrgentoo.tweeterstream.common.di.module;

import android.app.Activity;

import dagger.Module;

/**
 * Activity Module
 */
@Module
public class ActivityModule {

    private Activity activity;

    public ActivityModule(Activity activity) {
        this.activity = activity;
    }
}
