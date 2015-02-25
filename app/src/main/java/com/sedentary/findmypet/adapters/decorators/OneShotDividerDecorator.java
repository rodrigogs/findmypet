package com.sedentary.findmypet.adapters.decorators;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.sedentary.findmypet.R;

public class OneShotDividerDecorator extends RecyclerView.ItemDecoration {

	private Drawable mDivider;

	private static final int[] ATTRS = new int[]{android.R.attr.listDivider};
	private int mPosition;
    private int mSpacing;

	public OneShotDividerDecorator(Context context, int position) {
		final TypedArray a = context.obtainStyledAttributes(ATTRS);
		mDivider = a.getDrawable(0);
        mSpacing = context.getResources().getDimensionPixelSize(R.dimen.list_divider_spacing);
		a.recycle();
		mPosition = position;
	}

	@Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
		super.onDraw(c, parent, state);
		final int left = parent.getPaddingLeft();
		final int right = parent.getWidth() - parent.getPaddingRight();

		//only draw the divider at the specified position
		final View child = parent.getChildAt(mPosition);
		if (null == child) return;

		final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
		final int top = child.getBottom() + params.bottomMargin;
		final int bottom = top + mDivider.getIntrinsicHeight();
		mDivider.setBounds(left, top, right, bottom);
		mDivider.draw(c);
	}

	@Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
		super.getItemOffsets(outRect, view, parent, state);
        if(parent.getChildPosition(view) == mPosition)
            outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
	}
}