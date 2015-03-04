/*
 * This file is part of Popcorn Time.
 *
 * Popcorn Time is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Popcorn Time is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Popcorn Time. If not, see <http://www.gnu.org/licenses/>.
 */

package com.sedentary.findmypet.adapters;

import android.app.Activity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sedentary.findmypet.R;
import com.sedentary.findmypet.base.providers.pet.models.Pet;
import com.sedentary.findmypet.base.utils.AnimUtils;
import com.sedentary.findmypet.base.utils.PixelUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PetGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private int mItemWidth, mItemHeight, mMargin, mColumns;
	private ArrayList<OverviewItem> mItems = new ArrayList<>();
	//	private ArrayList<Media> mData = new ArrayList<>();
	private PetGridAdapter.OnItemClickListener mItemClickListener;
	final int NORMAL = 0, LOADING = 1;

	public PetGridAdapter(Activity activity, ArrayList<Pet> items, Integer columns) {
		mColumns = columns;

		int screenWidth = PixelUtils.getScreenWidth(activity);
		mItemWidth = (screenWidth / columns);
		mItemHeight = (int) ((double) mItemWidth / 0.677);
		mMargin = PixelUtils.getPixelsFromDp(activity, 2);

		setItems(items);
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v;
		switch (viewType) {
			case LOADING:
				v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_overview_griditem_loading, parent, false);
				return new PetGridAdapter.LoadingHolder(v);
			case NORMAL:
			default:
				v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_overview_griditem, parent, false);
				return new PetGridAdapter.ViewHolder(v);
		}
	}

	@Override
	public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
		int double_margin = mMargin * 2;
		int top_margin = (position < mColumns) ? mMargin * 2 : mMargin;

		GridLayoutManager.LayoutParams layoutParams = (GridLayoutManager.LayoutParams) viewHolder.itemView.getLayoutParams();
		layoutParams.height = mItemHeight;
		layoutParams.width = mItemWidth;
		if (position % mColumns == 0) {
			layoutParams.setMargins(double_margin, top_margin, mMargin, mMargin);
		} else if (position % mColumns == mColumns - 1) {
			layoutParams.setMargins(mMargin, top_margin, double_margin, mMargin);
		} else {
			layoutParams.setMargins(mMargin, top_margin, mMargin, mMargin);
		}
		viewHolder.itemView.setLayoutParams(layoutParams);

		if (getItemViewType(position) == NORMAL) {
			final ViewHolder petViewHolder = (ViewHolder) viewHolder;
			final OverviewItem overviewItem = getItem(position);
			Pet item = overviewItem.pet;

			petViewHolder.title.setVisibility(View.GONE);
			petViewHolder.title.setText(item.name.toUpperCase(Locale.getDefault()));
			if (overviewItem.imageError) {
				AnimUtils.fadeIn(petViewHolder.title);
			}

//			if (item.image != null && !item.image.equals("")) {
//				Picasso.with(petViewHolder.coverImage.getContext()).load(item.image)
//						.resize(mItemWidth, mItemHeight)
//						.into(petViewHolder.coverImage, new Callback() {
//                            @Override
//                            public void onSuccess() {
//                                overviewItem.imageError = false;
//                            }
//
//                            @Override
//                            public void onError() {
//                                overviewItem.imageError = true;
//                                if (((ViewHolder) viewHolder).title.getVisibility() != View.VISIBLE)
//                                    AnimUtils.fadeIn(petViewHolder.title);
//                            }
//                        });
//			}
		}
	}

	@Override
	public int getItemCount() {
		return mItems.size();
	}

	@Override
	public int getItemViewType(int position) {
		if (getItem(position).isLoadingItem) {
			return LOADING;
		}
		return NORMAL;
	}

	public OverviewItem getItem(int position) {
		if (position<0 || mItems.size()<=position)return null;
		return mItems.get(position);
	}

	public void setOnItemClickListener(PetGridAdapter.OnItemClickListener listener) {
		mItemClickListener = listener;
	}

//	@DebugLog
	public void removeLoading() {
		if (getItemCount() <= 0) return;
		OverviewItem item = mItems.get(getItemCount() - 1);
		if (item.isLoadingItem) {
			mItems.remove(getItemCount() - 1);
			notifyDataSetChanged();
		}
	}

//	@DebugLog
	public void addLoading() {
		OverviewItem item = null;
		if (getItemCount() != 0) {
			item = mItems.get(getItemCount() - 1);
		}

		if (getItemCount() == 0 || (item != null && !item.isLoadingItem)) {
			mItems.add(new OverviewItem(true));
			notifyDataSetChanged();
		}
	}

//	@DebugLog
	public boolean isLoading() {
		if (getItemCount()<=0)return false;
		return getItemViewType(getItemCount() - 1) == LOADING;
	}

//	@DebugLog
	public void setItems(ArrayList<Pet> items) {
        // Clear items
		mItems.clear();
        // Add new items, if available
		if (null != items) {
			for (Pet item : items) {
				mItems.add(new OverviewItem(item));
			}
		}
		notifyDataSetChanged();
	}

	public void clearItems() {
		mItems.clear();
		notifyDataSetChanged();
	}

	public interface OnItemClickListener {
		public void onItemClick(View v, Pet item, int position);
	}

	public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

		View itemView;
        @InjectView(R.id.focusOverlay)
        View focusOverlay;
		@InjectView(R.id.coverImage)
		ImageView coverImage;
		@InjectView(R.id.title)
		TextView title;

        private View.OnFocusChangeListener mOnFocusChangeListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                focusOverlay.setVisibility(hasFocus ? View.VISIBLE : View.INVISIBLE);
            }
        };

		public ViewHolder(View itemView) {
			super(itemView);
			ButterKnife.inject(this, itemView);
			this.itemView = itemView;
			itemView.setOnClickListener(this);
			coverImage.setMinimumHeight(mItemHeight);

            itemView.setOnFocusChangeListener(mOnFocusChangeListener);
		}

		public ImageView getCoverImage() {
			return coverImage;
		}

		@Override
		public void onClick(View view) {
			if (mItemClickListener != null) {
				int position = getPosition();
				Pet item = getItem(position).pet;
				mItemClickListener.onItemClick(view, item, position);
			}
		}

	}

	class LoadingHolder extends RecyclerView.ViewHolder {

		View itemView;

		public LoadingHolder(View itemView) {
			super(itemView);
			this.itemView = itemView;
			itemView.setMinimumHeight(mItemHeight);
		}

	}

	class OverviewItem {
		Pet pet;
		boolean imageError = false;
		boolean isLoadingItem = false;

		OverviewItem(Pet pet) {
			this.pet = pet;
		}

		OverviewItem(boolean loading) {
			this.isLoadingItem = loading;
		}
	}

}
