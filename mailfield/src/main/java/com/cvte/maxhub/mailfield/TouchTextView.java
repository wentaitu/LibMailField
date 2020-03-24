package com.cvte.maxhub.mailfield;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class TouchTextView extends AppCompatTextView {

    private OnClickViewListener mOnClickViewListener;

    public TouchTextView(Context context) {
        this(context, null, 0);
    }

    public TouchTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TouchTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnClickViewListener(OnClickViewListener listener) {
        mOnClickViewListener = listener;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int action = event.getAction();
        boolean flag = false;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                flag = true;
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
            case MotionEvent.ACTION_MOVE:
                flag = false;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(true);
                flag = false;
                if (action == MotionEvent.ACTION_UP && mOnClickViewListener != null) {
                    mOnClickViewListener.onClick(this);
                }
                break;
            default:
                break;
        }
        return flag;
    }

    public interface OnClickViewListener {
        void onClick(View view);
    }

}
