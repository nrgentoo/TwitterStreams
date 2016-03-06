package com.nrgentoo.tweeterstream.view.main;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.nrgentoo.tweeterstream.R;
import com.nrgentoo.tweeterstream.action.Actions;
import com.nrgentoo.tweeterstream.store.SessionStore;
import com.nrgentoo.tweeterstream.view.AbstractActivity;
import com.nrgentoo.tweeterstream.view.LoginActivity;

import javax.inject.Inject;

public class MainActivity extends AbstractActivity {

    // --------------------------------------------------------------------------------------------
    //      FIELDS
    // --------------------------------------------------------------------------------------------

    @Inject
    SessionStore sessionStore;

    private MainAdapter adapter;

    // --------------------------------------------------------------------------------------------
    //      UI REFERENCES
    // --------------------------------------------------------------------------------------------

    private ViewPager viewPager;
    private TabLayout tab_layout;

    // --------------------------------------------------------------------------------------------
    //      LIFECYCLE
    // --------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // inject
        getComponent().inject(this);

        // check session
        if (sessionStore.getSession() == null) {
            // launch login activity
            LoginActivity.launch(this);
            return;
        }

        setContentView(R.layout.activity_main);

        // inflate views
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        adapter = new MainAdapter(getSupportFragmentManager(), getResources());
        viewPager.setAdapter(adapter);

        tab_layout = (TabLayout) findViewById(R.id.tab_layout);
        tab_layout.setupWithViewPager(viewPager);

        // set tool bar as action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }
}
