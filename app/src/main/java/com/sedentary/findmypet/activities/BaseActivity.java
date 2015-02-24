package com.sedentary.findmypet.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.sedentary.findmypet.R;


public class BaseActivity extends ActionBarActivity {

    protected Handler mHandler;

    protected void onCreate(Bundle savedInstanceState, int layoutId) {
        super.onCreate(savedInstanceState);
        setContentView(layoutId);
        mHandler = new Handler(getMainLooper());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     *
     */
    protected void onHomePressed() {
        Intent upIntent = NavUtils.getParentActivityIntent(this);
        if (upIntent != null && NavUtils.shouldUpRecreateTask(this, upIntent)) {
            // This activity is NOT part of this app's task, so create a new task
            // when navigating up, with a synthesized back stack.
            TaskStackBuilder.create(this)
                    // Add all of this activity's parents to the back stack
                    .addNextIntentWithParentStack(upIntent)
                            // Navigate up to the closest parent
                    .startActivities();
        } else {
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onHomePressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
