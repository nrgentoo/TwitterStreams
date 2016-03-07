package com.nrgentoo.tweeterstream.view.main;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.hardsoftstudio.rxflux.action.RxError;
import com.hardsoftstudio.rxflux.store.RxStoreChange;
import com.nrgentoo.tweeterstream.R;
import com.nrgentoo.tweeterstream.action.Actions;
import com.nrgentoo.tweeterstream.store.SessionStore;
import com.nrgentoo.tweeterstream.view.AbstractActivity;
import com.nrgentoo.tweeterstream.view.LoginActivity;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class MainActivity extends AbstractActivity {

    // --------------------------------------------------------------------------------------------
    //      FIELDS
    // --------------------------------------------------------------------------------------------

    @Inject
    SessionStore sessionStore;

    @Inject
    Actions actions;

    @Inject
    EventBus eventBus;

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

        // register eventBus
        eventBus.register(this);

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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // unregister eventBus
        eventBus.unregister(this);
    }

    // --------------------------------------------------------------------------------------------
    //      MENU
    // --------------------------------------------------------------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_logout:
                // logout action
                actions.logout();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // --------------------------------------------------------------------------------------------
    //      RX FLUX EVENTS
    // --------------------------------------------------------------------------------------------

    @SuppressWarnings("unused")
    public void onEvent(RxStoreChange change) {
        switch (change.getStoreId()) {
            case SessionStore.ID:
                switch (change.getRxAction().getType()) {
                    case Actions.LOGOUT:
                        // launch login
                        LoginActivity.launch(this);

                        // finish main activity
                        finish();
                        break;
                }
                break;
        }
    }

    @SuppressWarnings("unused")
    public void onEvent(RxError error) {

    }
}
