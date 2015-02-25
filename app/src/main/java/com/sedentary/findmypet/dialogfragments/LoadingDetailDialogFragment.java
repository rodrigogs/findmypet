package com.sedentary.findmypet.dialogfragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sedentary.findmypet.R;
import com.sedentary.findmypet.base.providers.pet.PetProvider;
import com.sedentary.findmypet.base.providers.pet.models.Pet;
import com.sedentary.findmypet.base.utils.ThreadUtils;

import java.util.ArrayList;

public class LoadingDetailDialogFragment extends DialogFragment {

	private Callback mCallback;

	public static final String EXTRA_ITEM_ID = "item_id";
	public static final String EXTRA_PALETTE_COLOR = "palette_color";
	private PetProvider mProvider;
    private Boolean mSavedInstanceState = false;

	public static LoadingDetailDialogFragment newInstance(String itemId, int paletteColor) {
		LoadingDetailDialogFragment frag = new LoadingDetailDialogFragment();
		Bundle args = new Bundle();
		args.putString(EXTRA_ITEM_ID, itemId);
		args.putInt(EXTRA_PALETTE_COLOR, paletteColor);
		frag.setArguments(args);
		return frag;
	}

	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	 * on create view
	 * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return LayoutInflater.from(new ContextThemeWrapper(getActivity(), R.style.Theme_PopcornTime)).inflate(R.layout
				.fragment_loading_detail, container, false);
	}

	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        mSavedInstanceState = false;
		setStyle(STYLE_NO_FRAME, R.style.Theme_Dialog_Transparent);
	}

	@Override public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);
		if (null != mProvider) mProvider.cancel();
	}

	@Override public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (null == getTargetFragment()) throw new IllegalArgumentException("target fragment must be set");
		if (getTargetFragment() instanceof Callback) mCallback = (Callback) getTargetFragment();
		else throw new IllegalArgumentException("target fragment must implement callbacks");
	}

	@Override public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		String itemId = getArguments().getString(EXTRA_ITEM_ID);
		final int paletteColor = getArguments().getInt(EXTRA_PALETTE_COLOR);
		mProvider = mCallback.getProvider();

		mProvider.getDetail(itemId, new PetProvider.Callback() {
					@Override
					public void onSuccess(ArrayList<Pet> items) {
						if (!isAdded()) return;
						if (items.size() <= 0) return;

						final Pet item = items.get(0);
						ThreadUtils.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mCallback.onDetailLoadSuccess(item, paletteColor);
                                if (!mSavedInstanceState) dismiss();
                            }
                        });

					}

					@Override
					public void onFailure(Exception e) {
						if (!e.getMessage().equals("Canceled")) {
							e.printStackTrace();
							ThreadUtils.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									mCallback.onDetailLoadFailure();
                                    if(!mSavedInstanceState) dismiss();
								}
							});
						}
					}
				}

		);
	}

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mSavedInstanceState = true;
    }

    public interface Callback {
		PetProvider getProvider();

		void onDetailLoadFailure();

		void onDetailLoadSuccess(Pet item, int paletteColor);
	}


}
