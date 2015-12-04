package me.nereo.multiimageselector;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;


import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yangfeng on 15-10-31.
 */
public class AppActivity extends AppCompatActivity {
    @Bind(R.id.activity_toolbar)
    Toolbar mToolbar;

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        ButterKnife.bind(this);

        mToolbar.setTitle(getTitle());
        setSupportActionBar(mToolbar);

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

    protected boolean isHomeAsUpEnabled() {
        return true;
    }
}
