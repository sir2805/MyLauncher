package com.example.pc.mylauncher;

/**
 * Created by PC on 02.04.2017.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/** A RelativeLayout that will always be square -- same width and height,
 * where the height is based off the width. */
public class SquareLinearLayout extends LinearLayout {

    public SquareLinearLayout(Context context) {
        super(context);
    }

    public SquareLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

//    @TargetApi(VERSION_CODES.LOLLIPOP)
//    public SquareLinearLayout(Context context, AttributeSet attrs,         int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Set a square layout.
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

}