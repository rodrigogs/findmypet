package com.sedentary.findmypet.fragments;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sedentary.findmypet.R;
import com.sedentary.findmypet.adapters.PetGridAdapter;
import com.sedentary.findmypet.base.providers.pet.FindMyPetProvider;
import com.sedentary.findmypet.base.providers.pet.PetProvider;
import com.sedentary.findmypet.base.providers.pet.models.Pet;
import com.sedentary.findmypet.base.utils.ThreadUtils;
import com.sedentary.findmypet.dialogfragments.LoadingDetailDialogFragment;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PetListFragment extends Fragment implements PetProvider.Callback, LoadingDetailDialogFragment.Callback {

    public static final String EXTRA_ARGS = "extra_args";
    public static final String EXTRA_MODE = "extra_mode";

    public static final String DIALOG_LOADING_DETAIL = "DIALOG_LOADING_DETAIL";
    public static final int LOADING_DIALOG_FRAGMENT = 1;

    private PetGridAdapter mAdapter;
    private GridLayoutManager mLayoutManager;
    private Integer mColumns = 2, mRetries = 0;

    //overrides the default loading message
    private int mLoadingMessage = R.string.loading_data;

    private Boolean mIsAttached;
    private State mState = State.UNINITIALISED;
    private Mode mode;

    public enum Mode {
        NORMAL, SEARCH
    }

    private enum State {
        UNINITIALISED, LOADING, SEARCHING, LOADING_PAGE, LOADED, LOADING_DETAIL
    }

    private ArrayList<Pet> mItems = new ArrayList<>();

    private boolean mEndOfListReached = false;

    private int mFirstVisibleItem, mVisibleItemCount, mTotalItemCount = 0, mLoadingTreshold = mColumns * 3, mPreviousTotal = 0;

    private PetProvider mProvider;
    private int mPage = 1;
    private PetProvider.Filters mFilters = new PetProvider.Filters();

    @InjectView(R.id.progressOverlay)
    LinearLayout progressOverlay;
    @InjectView(R.id.recyclerView)
    RecyclerView recyclerView;
    @InjectView(R.id.emptyView)
    TextView emptyView;
    @InjectView(R.id.progress_textview)
    TextView progressTextView;

    //todo: a better way to passing a provider to this fragment
    public static PetListFragment newInstance(Mode mode, int provider) {
        PetListFragment frag = new PetListFragment();
        Bundle args = new Bundle();
        args.putInt(EXTRA_ARGS, provider);
        args.putSerializable(EXTRA_MODE, mode);
        frag.setArguments(args);
        return frag;
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        mLoadingMessage = R.string.loading_data;
	}

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pet, container, false);
	}

	@Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);

        mColumns = getResources().getInteger(R.integer.overview_cols);
        mLoadingTreshold = mColumns * 3;
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(getActivity(), mColumns);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setOnScrollListener(mScrollListener);
        //adapter should only ever be created once on fragment initialise.
        mAdapter = new PetGridAdapter(getActivity(), mItems, mColumns);
        mAdapter.setOnItemClickListener(mOnItemClickListener);
        recyclerView.setAdapter(mAdapter);
	}

	@Override
	public void onDetach() {
		super.onDetach();
        mIsAttached = false;
	}

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mIsAttached = true;
    }

    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //get the provider type and create a provider
        int providerType = getArguments().getInt(EXTRA_ARGS);
        mode = (Mode) getArguments().getSerializable(EXTRA_MODE);
        switch (providerType) {
            case 0:
                mProvider = new FindMyPetProvider();
                break;
            default:
                throw new IllegalArgumentException("No provider set");

        }
        if (mode == Mode.SEARCH) emptyView.setText(getString(R.string.no_search_results));

        //don't load initial data in search mode
        if (mode != Mode.SEARCH && mAdapter.getItemCount() == 0) {
            mProvider.getList(mFilters, this);/* fetch new items */
            setState(State.LOADING);
        } else updateUI();
    }

    @Override public void onPause() {
        super.onPause();
        mProvider.cancel();
    }

    @Override public PetProvider getProvider() {
        return mProvider;
    }

    @Override
    public void onDetailLoadFailure() {

    }

    @Override
    public void onDetailLoadSuccess(Pet item, int paletteColor) {

    }

    /**
     * Responsible for updating the UI based on the state of this fragment
     */
    private void updateUI() {
        if (!isAdded()) return;

        //animate recyclerview to full alpha
        //		if (recyclerView.getAlpha() != 1.0f)
        //			recyclerView.animate().alpha(1.0f).setDuration(100).start();

        //update loading message based on state
        switch (mState) {
            case LOADING_DETAIL:
                mLoadingMessage = R.string.loading_details;
                break;
            case SEARCHING:
                mLoadingMessage = R.string.searching;
                break;
            default:
                int providerMessage = mProvider.getLoadingMessage();
                mLoadingMessage = providerMessage > 0 ? providerMessage : R.string.loading_data;
                break;
        }

        switch (mState) {
            case LOADING_DETAIL:
            case SEARCHING:
            case LOADING:
                if (mAdapter.isLoading()) mAdapter.removeLoading();
                //show the progress bar
                recyclerView.setVisibility(View.VISIBLE);
                //				recyclerView.animate().alpha(0.5f).setDuration(500).start();
                emptyView.setVisibility(View.GONE);
                progressOverlay.setVisibility(View.VISIBLE);
                break;
            case LOADED:
                if (mAdapter.isLoading()) mAdapter.removeLoading();
                progressOverlay.setVisibility(View.GONE);
                boolean hasItems = mItems.size() > 0;
                //show either the recyclerview or the empty view
                recyclerView.setVisibility(hasItems ? View.VISIBLE : View.INVISIBLE);
                emptyView.setVisibility(hasItems ? View.GONE : View.VISIBLE);
                break;
            case LOADING_PAGE:
                //add a loading view to the adapter
                if (!mAdapter.isLoading()) mAdapter.addLoading();
                emptyView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                break;
        }
        updateLoadingMessage();
    }

    private void updateLoadingMessage() {
        progressTextView.setText(R.string.loading_data);
    }

//    @DebugLog
    private void setState(State state) {
        if (mState == state) return;//do nothing
        mState = state;
        updateUI();
    }

    public void triggerSearch(String searchQuery) {
        if (!isAdded()) return;
        if (null == mAdapter) return;

        mEndOfListReached = false;

        mItems.clear();
        mAdapter.clearItems();//clear out adapter

        if (searchQuery.equals("")) {
            setState(State.LOADED);
            return; //don't do a search for empty queries
        }

        setState(State.SEARCHING);
        mFilters.keywords = searchQuery;
        mFilters.page = 1;
        mPage = 1;
        mProvider.getList(mFilters, this);
    }

    @Override
//    @DebugLog
    public void onSuccess(final ArrayList<Pet> items) {
        mItems.clear();
        if (null != items) mItems.addAll(items);

        //fragment may be detached, so we dont want to update the UI
        if (!isAdded()) return;

        mEndOfListReached = false;

        mPage = mPage + 1;
        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.setItems(items);
                mPreviousTotal = mTotalItemCount = mAdapter.getItemCount();
                setState(State.LOADED);
            }
        });
    }

    @Override
//    @DebugLog
    public void onFailure(Exception e) {
        if(!mIsAttached) {
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mAdapter == null) {
                        return;
                    }

                    mAdapter.removeLoading();
                    setState(State.LOADED);
                }
            });
        } else if (e.getMessage() != null && e.getMessage().equals(getString(R.string.pets_error))) {
            mEndOfListReached = true;
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mAdapter == null) {
                        return;
                    }

                    mAdapter.removeLoading();
                    setState(State.LOADED);
                }
            });
        } else {
            e.printStackTrace();
//            LogUtils.e(e.getMessage());
            if (mRetries > 1) {
                ThreadUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), R.string.unknown_error, Toast.LENGTH_SHORT).show();
                        setState(State.LOADED);
                    }
                });
            } else {
                mProvider.getList(null, this);
            }
            mRetries++;
        }
    }

    private PetGridAdapter.OnItemClickListener mOnItemClickListener = new PetGridAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(final View view, final Pet item, final int position) {
            /**
             * We shouldn't really be doing the palette loading here without any ui feedback,
             * but it should be really quick
             */
            RecyclerView.ViewHolder holder = recyclerView.getChildViewHolder(view);
            if (holder instanceof PetGridAdapter.ViewHolder) {
                ImageView coverImage = ((PetGridAdapter.ViewHolder) holder).getCoverImage();

                if (coverImage.getDrawable() == null) {
                    showLoadingDialog(item.name, -1);
                    return;
                }

                Bitmap cover = ((BitmapDrawable) coverImage.getDrawable()).getBitmap();
                Palette.generateAsync(cover, 5, new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                        int vibrantColor = palette.getVibrantColor(-1);
                        int paletteColor;
                        if (vibrantColor == -1) {
                            paletteColor = palette.getMutedColor(getResources().getColor(R.color.primary));
                        } else {
                            paletteColor = vibrantColor;
                        }
                        showLoadingDialog(item.name, paletteColor);
                    }
                });
            } else showLoadingDialog(item.name, -1);

        }
    };

    private RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            mVisibleItemCount = mLayoutManager.getChildCount();
            mTotalItemCount = mLayoutManager.getItemCount();
            mFirstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();

            if (mState == State.LOADING_PAGE) {
                if (mTotalItemCount > mPreviousTotal) {
                    mPreviousTotal = mTotalItemCount;
                    mPreviousTotal = mTotalItemCount = mLayoutManager.getItemCount();
                    setState(State.LOADED);
                }
            }

            if (!mEndOfListReached && !(mState == State.LOADING_PAGE) && (mTotalItemCount - mVisibleItemCount) <= (mFirstVisibleItem +
                    mLoadingTreshold)) {
                PetProvider.Filters filters = mFilters;
                filters.page = mPage;
                mProvider.getList(mItems, filters, PetListFragment.this);

                mFilters = filters;

                mPreviousTotal = mTotalItemCount = mLayoutManager.getItemCount();
                setState(State.LOADING_PAGE);
            }
        }
    };

    private void showLoadingDialog(String itemId, int paletteColor) {
        LoadingDetailDialogFragment loadingFragment = LoadingDetailDialogFragment.newInstance(itemId, paletteColor);
        loadingFragment.setTargetFragment(PetListFragment.this, LOADING_DIALOG_FRAGMENT);
        loadingFragment.show(getFragmentManager(), DIALOG_LOADING_DETAIL);
    }
}
