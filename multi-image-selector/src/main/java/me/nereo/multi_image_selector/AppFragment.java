package me.nereo.multi_image_selector;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

/**
 * Created by yangfeng on 15-12-8.
 */
public class AppFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerEventBus();
    }

    @Override
    public void onDestroy() {
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
