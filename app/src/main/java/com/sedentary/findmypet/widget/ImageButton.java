package com.sedentary.findmypet.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.sedentary.findmypet.utils.CheatSheet;

public class ImageButton extends android.widget.ImageButton {

    public ImageButton(Context context) {
        super(context);
    }

    public ImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setContentDescription(CharSequence contentDesc) {
        super.setContentDescription(contentDesc);
        if (contentDesc.length() > 0)
            CheatSheet.setup(this, contentDesc);
    }

}
