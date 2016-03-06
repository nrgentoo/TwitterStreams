package com.nrgentoo.tweeterstream.view.main;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

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

    MainAdapter adapter;

    // --------------------------------------------------------------------------------------------
    //      UI REFERENCES
    // --------------------------------------------------------------------------------------------

    ViewPager viewPager;

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
        }

        setContentView(R.layout.activity_main);

        // inflate views
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        adapter = new MainAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
    }
}
