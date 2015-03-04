package com.sedentary.findmypet.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.sedentary.findmypet.R;
import com.sedentary.findmypet.fragments.AboutFragment;
import com.sedentary.findmypet.utils.ToolbarUtils;

import butterknife.InjectView;

public class AboutActivity extends BaseActivity implements AboutFragment.OnFragmentInteractionListener {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    public static Intent startActivity(Activity activity) {
        Intent intent = new Intent(activity, AboutActivity.class);
        activity.startActivity(intent);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_about);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.about);

        ToolbarUtils.updateToolbarHeight(this, toolbar);
    }
}
