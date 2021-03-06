package com.sedentary.findmypet.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.sedentary.findmypet.R;
import com.sedentary.findmypet.base.Constants;
import com.sedentary.findmypet.base.preferences.Prefs;
import com.sedentary.findmypet.base.utils.IntentUtils;
import com.sedentary.findmypet.base.utils.PrefUtils;
import com.sedentary.findmypet.fragments.AboutFragment;
import com.sedentary.findmypet.fragments.NavigationDrawerFragment;
import com.sedentary.findmypet.fragments.PetListFragment;
import com.sedentary.findmypet.utils.ToolbarUtils;
import com.sedentary.findmypet.widget.ScrimInsetsFrameLayout;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import butterknife.InjectView;

public class DashboardActivity extends BaseActivity implements NavigationDrawerFragment.Callbacks, AboutFragment.OnFragmentInteractionListener {

    @InjectView(R.id.toolbar) Toolbar mToolbar;
    @InjectView(R.id.navigation_drawer_container) ScrimInsetsFrameLayout mNavigationDrawerContainer;
    NavigationDrawerFragment mNavigationDrawerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_dashboard);

        setSupportActionBar(mToolbar);

        ToolbarUtils.updateToolbarHeight(this, mToolbar);

        // Set up the drawer
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.primary_dark));

        mNavigationDrawerFragment =
                (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer_fragment);

        mNavigationDrawerFragment.initialise(mNavigationDrawerContainer, drawerLayout);

        if (null != savedInstanceState) return; //dont reselect item if saved state exists
        int providerId = PrefUtils.get(this, Prefs.DEFAULT_VIEW, 0);
        mNavigationDrawerFragment.selectItem(providerId);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
				/* Override default {@link com.sedentary.findmypet.activities.BaseActivity } behaviour */
                return false;
            case R.id.action_settings:
                //start the preferences activity
                startActivity(new Intent(this, PreferencesActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        String tag = Integer.toString(position);

//		Fragment fragment = mFragmentCache.get(position);
        Fragment fragment = fragmentManager.findFragmentByTag(tag);
        if (null == fragment) {
            fragment = PetListFragment.newInstance(PetListFragment.Mode.NORMAL, position); //create new fragment instance
        }
        fragmentManager.beginTransaction().replace(R.id.container, fragment, tag).commit();
    }
}
