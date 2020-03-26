package com.cvte.maxhub.mailfield.view

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.widget.ScrollView
import com.cvte.maxhub.mailfield.config.MailFieldConfig

/**
 * @author heronghu
 * @email heronghu@cvte.com
 * @date 2018/4/19
 * @description: 最大高度ScrollView
 */
class MaxHeightScrollView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ScrollView(context, attrs, defStyleAttr) {

    private var mMaxHeight = dpToPx(context, MailFieldConfig.SCROLLVIEW_MAX_HEIGHT)

    fun setMaxHeight(maxHeightDp: Int) {
        mMaxHeight = dpToPx(context, maxHeightDp.toFloat())
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(
            widthMeasureSpec,
            MeasureSpec.makeMeasureSpec(mMaxHeight, MeasureSpec.AT_MOST))
    }

    /**
     * 解决滑动冲突：MaxHeightScrollView高度已到达最高，内部子控件可滑动，
     * 且外层布局也可滑动时，禁止外部滑动
     */
    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        if (measuredHeight >= mMaxHeight) {
            parent.requestDisallowInterceptTouchEvent(true)
        } else {
            parent.requestDisallowInterceptTouchEvent(false)
        }
        return super.onInterceptTouchEvent(event)
    }

    private fun dpToPx(context: Context, dp: Float): Int {
        val metrics = context.resources.displayMetrics
        return TypedValue.applyDimension(1, dp, metrics).toInt()
    }

}