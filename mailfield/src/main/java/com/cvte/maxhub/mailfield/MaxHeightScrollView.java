package com.cvte.maxhub.mailfield;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

import static com.cvte.maxhub.mailfield.TagView.dpToPx;

/**
 * @author heronghu
 * @email heronghu@cvte.com
 * @date 2018/4/19
 * @description:
 */

public class MaxHeightScrollView extends ScrollView {

    private int mMaxHeight = dpToPx(getContext(), 104f);

    public MaxHeightScrollView(Context context) {
        this(context, null, 0);
    }

    public MaxHeightScrollView(Context context, AttributeSet attrs) {
        this (context, attrs, 0);
    }

    public MaxHeightScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setMaxHeight(int maxHeightDp) {
        mMaxHeight = dpToPx(getContext(), maxHeightDp);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(mMaxHeight, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (getMeasuredHeight() >= mMaxHeight) {
            getParent().requestDisallowInterceptTouchEvent(true);
        } else {
            getParent().requestDisallowInterceptTouchEvent(false);
        }
        return super.onInterceptTouchEvent(event);
    }

}
