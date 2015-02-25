package com.sedentary.findmypet.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;

import com.sedentary.findmypet.R;
import com.sedentary.findmypet.adapters.PreferencesListAdapter;
import com.sedentary.findmypet.base.preferences.PrefItem;
import com.sedentary.findmypet.base.preferences.Prefs;
import com.sedentary.findmypet.base.utils.PrefUtils;
import com.sedentary.findmypet.base.utils.ResourceUtil;
import com.sedentary.findmypet.dialogfragments.ColorPickerDialogFragment;
import com.sedentary.findmypet.dialogfragments.NumberPickerDialogFragment;
import com.sedentary.findmypet.dialogfragments.StringArraySelectorDialogFragment;
import com.sedentary.findmypet.utils.ToolbarUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;

public class PreferencesActivity extends BaseActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

	private List<Object> mPrefItems = new ArrayList<>();
	private LinearLayoutManager mLayoutManager;

	@InjectView(R.id.toolbar)
    Toolbar toolbar;
	@InjectView(R.id.recyclerview)
    RecyclerView recyclerView;
	@InjectView(R.id.rootLayout)
	ViewGroup rootLayout;

	public static Intent startActivity(Activity activity) {
		Intent intent = new Intent(activity, PreferencesActivity.class);
		activity.startActivity(intent);
		return intent;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_preferences);
		setSupportActionBar(toolbar);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle(R.string.settings);

		ToolbarUtils.updateToolbarHeight(this, toolbar);

		mLayoutManager = new LinearLayoutManager(this);
		recyclerView.setLayoutManager(mLayoutManager);

		PrefUtils.getPrefs(this).registerOnSharedPreferenceChangeListener(this);

		refreshItems();
	}



	@Override
	protected void onDestroy() {
		super.onDestroy();
		PrefUtils.getPrefs(this).unregisterOnSharedPreferenceChangeListener(this);
	}

	private void refreshItems() {
		mPrefItems = new ArrayList<>();
		mPrefItems.add(getResources().getString(R.string.general));

		mPrefItems.add(new PrefItem(this, R.drawable.ic_prefs_default_view, R.string.default_view, Prefs.DEFAULT_VIEW, 0,
				new PrefItem.OnClickListener() {
					@Override
					public void onClick(final PrefItem item) {
//						String[] items = {getString(R.string.title_movies), getString(R.string.title_shows)};
                        String[] items = { "Test 1", "Test 2" };

						openListSelectionDialog(item.getTitle(), items, StringArraySelectorDialogFragment.SINGLE_CHOICE,
								(int) item.getValue(), new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int position) {
										item.saveValue(position);
										dialog.dismiss();
									}
								});
					}
				}));

		mPrefItems.add(new PrefItem(this, R.drawable.ic_launcher, R.string.color, Prefs.COLOR, Color.WHITE,
				new PrefItem.OnClickListener() {
					@Override
					public void onClick(final PrefItem item) {
						Bundle args = new Bundle();
						args.putString(NumberPickerDialogFragment.TITLE, item.getTitle());
						args.putInt(NumberPickerDialogFragment.DEFAULT_VALUE, (int) item.getValue());

						ColorPickerDialogFragment dialogFragment = new ColorPickerDialogFragment();
						dialogFragment.setArguments(args);
						dialogFragment.setOnResultListener(new ColorPickerDialogFragment.ResultListener() {
							@Override
							public void onNewValue(int value) {
								item.saveValue(value);
							}
						});
						dialogFragment.show(getFragmentManager(), "pref_fragment");
					}
				}));

		if (recyclerView.getAdapter() != null && mLayoutManager != null) {
			int position = mLayoutManager.findFirstVisibleItemPosition();
			View v = mLayoutManager.findViewByPosition(position);
			recyclerView.setAdapter(new PreferencesListAdapter(mPrefItems));
			if (v != null) {
				int offset = v.getTop();
				mLayoutManager.scrollToPositionWithOffset(position, offset);
			} else {
				mLayoutManager.scrollToPosition(position);
			}
		} else {
			recyclerView.setAdapter(new PreferencesListAdapter(mPrefItems));
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		refreshItems();
		toolbar.setMinimumHeight((int) ResourceUtil.getAttributeDimension(this, this.getTheme(), R.attr.actionBarSize));
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (isUseChangeablePref(key)) {
			refreshItems();
		}
	}

	private boolean isUseChangeablePref(String key) {
		boolean b = false;
		for (Object item : mPrefItems) {
			if (item instanceof PrefItem) {
				PrefItem pref = (PrefItem) item;
				if (pref.getPrefKey().equals(key))
					b = true;
			}
		}
		return b;
	}

	private void openListSelectionDialog(String title, String[] items, int mode, int defaultPosition,
			DialogInterface.OnClickListener onClickListener) {
		if (mode == StringArraySelectorDialogFragment.NORMAL) {
			StringArraySelectorDialogFragment.show(getSupportFragmentManager(), title, items, defaultPosition, onClickListener);
		} else if (mode == StringArraySelectorDialogFragment.SINGLE_CHOICE) {
			StringArraySelectorDialogFragment.showSingleChoice(getSupportFragmentManager(), title, items, defaultPosition, onClickListener);
		}
	}
}
