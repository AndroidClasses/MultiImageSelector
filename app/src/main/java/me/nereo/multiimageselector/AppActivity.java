package me.nereo.multiimageselector;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;


import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

/**
 * Created by yangfeng on 15-10-31.
 */
public class AppActivity extends AppCompatActivity {
    @Bind(R.id.activity_toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerEventBus();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        ButterKnife.bind(this);

        setTitle(getTitle());

        ActionBar ab = getSupportActionBar();
        if (null != ab) {
            ab.setDisplayHomeAsUpEnabled(isHomeAsUpEnabled());
            ab.setDisplayShowHomeEnabled(true);
//            ab.setHomeButtonEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);

        if (null != mToolbar) {
            mToolbar.setTitle(getTitle());
            setSupportActionBar(mToolbar);
        }
    }

    protected boolean isHomeAsUpEnabled() {
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterEventBus();
    }

    // override this method and return true to employ event bus, then
    // provide override to any onEvent to make event happy.
    protected boolean isEventBusEnabled() {
        return false;
    }

    @Subscribe
    public void onEvent(Object event) {
        // do nothing here, just make event bus happy, need to implement in derived class
    }

    private void registerEventBus() {
        if (isEventBusEnabled()) {
            EventBus.getDefault().register(this);
        }
    }

    private void unregisterEventBus() {
        if (isEventBusEnabled()) {
            EventBus.getDefault().unregister(this);
        }
    }

    protected void postBusEvent(Object busEvent) {
        EventBus.getDefault().post(busEvent);
    }
}
