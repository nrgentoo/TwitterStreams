package com.nrgentoo.tweeterstream.view.main;

import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.nrgentoo.tweeterstream.R;
import com.nrgentoo.tweeterstream.view.hometimeline.HomeTimeLineFragment;
import com.nrgentoo.tweeterstream.view.usertimeline.UserTimelineFragment;

/**
 * Main view pager adapter
 */
public class MainAdapter extends FragmentStatePagerAdapter {

    private static final int COUNT = 2;
    Resources resources;

    // --------------------------------------------------------------------------------------------
    //      CONSTRUCTOR
    // --------------------------------------------------------------------------------------------

    public MainAdapter(FragmentManager fm, Resources resources) {
        super(fm);
        this.resources = resources;
    }

    // --------------------------------------------------------------------------------------------
    //      PUBLIC METHODS
    // --------------------------------------------------------------------------------------------

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new HomeTimeLineFragment();
            case 1:
                return new UserTimelineFragment();
            default:
                throw new IllegalArgumentException("Index out of bounds: position" + position +
                        " of size " + COUNT);
        }
    }

    @Override
    public int getCount() {
        return COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return resources.getString(R.string.title_home_timeline);
            case 1:
                return resources.getString(R.string.title_user_timeline);
            default:
                throw new IllegalArgumentException("Index out of bounds: position" + position +
                        " of size " + COUNT);
        }
    }
}
