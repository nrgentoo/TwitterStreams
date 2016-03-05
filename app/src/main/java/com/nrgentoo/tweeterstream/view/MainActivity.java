package com.nrgentoo.tweeterstream.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.nrgentoo.tweeterstream.R;
import com.nrgentoo.tweeterstream.store.SessionStore;

import javax.inject.Inject;

public class MainActivity extends AbstractActivity {

    // --------------------------------------------------------------------------------------------
    //      FIELDS
    // --------------------------------------------------------------------------------------------

    @Inject
    SessionStore sessionStore;

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
    }
}
