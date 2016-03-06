package com.nrgentoo.tweeterstream.view.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.nrgentoo.tweeterstream.view.hometimeline.HomeTimeLineFragment;

/**
 * Main view pager adapter
 */
public class MainAdapter extends FragmentStatePagerAdapter {

    private static final int COUNT = 1;

    // --------------------------------------------------------------------------------------------
    //      CONSTRUCTOR
    // --------------------------------------------------------------------------------------------

    public MainAdapter(FragmentManager fm) {
        super(fm);
    }

    // --------------------------------------------------------------------------------------------
    //      PUBLIC METHODS
    // --------------------------------------------------------------------------------------------

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new HomeTimeLineFragment();
            default:
                throw new IllegalArgumentException("Index out of bounds: position" + position +
                        " of size " + COUNT);
        }
    }

    @Override
    public int getCount() {
        return COUNT;
    }
}
