package com.sedentary.findmypet.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sedentary.findmypet.R;
import com.sedentary.findmypet.fragments.NavigationDrawerFragment;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;

public class NavigationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private OnItemClickListener mItemClickListener;
    private final List<NavigationDrawerFragment.NavDrawerItem> mItems;
    private Context mContext;
    final int HEADER = 0, ITEM = 1;
    final int mNormalColor, mCheckedColor, mCheckedBackgroundRes, mNormalBackgroundRes;
    private Callback mCallback;

    public NavigationAdapter(@NonNull Context context, @NonNull Callback callback,
                             List<NavigationDrawerFragment.NavDrawerItem> items) {
        mContext = context;
        mItems = items;
        mCallback = callback;
        mNormalColor = context.getResources().getColor(R.color.nav_drawer_deselected);
        mCheckedColor = context.getResources().getColor(R.color.primary);
        mNormalBackgroundRes = R.drawable.selectable_nav_background;
        mCheckedBackgroundRes = R.color.nav_drawer_selected_bg;
    }


    @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        switch (viewType) {
            case HEADER:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.nav_drawer_header, parent, false);
                return new HeaderHolder(v);
            case ITEM:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_icon_singleline_item, parent, false);
                return new ItemRowHolder(v);
        }
        return null;
    }

    @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);
        switch (type) {
            case HEADER:
                onBindHeaderViewHolder((HeaderHolder) holder, position);
                break;
            case ITEM:
                onBindItemViewHolder((ItemRowHolder) holder, position);
                break;
        }

    }

    private void onBindHeaderViewHolder(HeaderHolder holder, int position) {
        //do nothing for now
        holder.mBackgroundImageView.setBackgroundResource(R.color.primary_dark);
        holder.mProfileImageView.setVisibility(View.VISIBLE);
        holder.mProfileImageView.setImageResource(R.drawable.ic_launcher);
    }

    private void onBindItemViewHolder(ItemRowHolder viewHolder, int position) {
        NavigationDrawerFragment.NavDrawerItem item = getItem(position);

        viewHolder.title.setText(item.getTitle());

        boolean isSelected = (getCorrectPosition(position)) == mCallback.getSelectedPosition();
        viewHolder.title.setTextColor(isSelected ? mCheckedColor : mNormalColor);
        viewHolder.itemView.setBackgroundResource(isSelected ? mCheckedBackgroundRes : mNormalBackgroundRes);
        //		vh.itemView.setBackgroundResource(isSelected ? R.color.nav_drawer_highlight : 0);

        if (item.getIcon() > 0) {
            viewHolder.icon.setImageResource(item.getIcon());
            if (isSelected) viewHolder.icon.setColorFilter(mCheckedColor, PorterDuff.Mode.SRC_IN);
            else viewHolder.icon.setColorFilter(mNormalColor, PorterDuff.Mode.SRC_IN);
        }


    }

    @Override public int getItemCount() {
        return mItems.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mItemClickListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position).isHeader) {
            return HEADER;
        }
        return ITEM;
    }


    public interface OnItemClickListener {
        public void onItemClick(View v, NavigationDrawerFragment.NavDrawerItem item, int position);
    }

    public NavigationDrawerFragment.NavDrawerItem getItem(int position) {
        if (position < 0 || mItems.size() <= position) return null;
        return mItems.get(position);
    }

    /**
     * Accounts for non Navigation Item rows and returns a position minus the correct offset
     *
     * @param position
     *
     * @return
     */
    public int getCorrectPosition(int position) {
        return position - 1;
    }

    public class ItemRowHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @InjectView(android.R.id.icon)
        ImageView icon;
        @InjectView(android.R.id.text1)
        TextView title;

        public ItemRowHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mItemClickListener != null) {
                int position = getPosition();
                NavigationDrawerFragment.NavDrawerItem item = getItem(position);
                mItemClickListener.onItemClick(view, item, position);
            }
        }
    }

    public static class HeaderHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.bg_imageview)
        ImageView mBackgroundImageView;
        @InjectView(R.id.profile_imageview)
        CircleImageView mProfileImageView;
        @InjectView(R.id.title_textview)
        TextView mTitleTextView;
        @InjectView(R.id.subtitle_textview)
        TextView mSubtitleTextView;

        public HeaderHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

    public interface Callback {
        int getSelectedPosition();
    }
}
