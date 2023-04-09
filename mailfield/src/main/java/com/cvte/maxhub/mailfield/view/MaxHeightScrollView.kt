package com.cvte.maxhub.mailfield.view

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.widget.ScrollView
import com.cvte.maxhub.mailfield.R
import com.cvte.maxhub.mailfield.config.MailFieldDefAttr

/**
 * 最大高度ScrollView
 *
 * @author tuwentai
 * @date 2020/3/19
 */
class MaxHeightScrollView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ScrollView(context, attrs, defStyleAttr) {

    private var mMaxHeight = 0

    init {
        val typeArray = context.obtainStyledAttributes(attrs, R.styleable.MaxHeightScrollView, defStyleAttr, defStyleAttr)
        mMaxHeight = typeArray.getDimension(
            R.styleable.MaxHeightScrollView_maxHeight,
            dpToPx(this.context, MailFieldDefAttr.SCROLLVIEW_MAX_HEIGHT).toFloat()).toInt()
        typeArray.recycle()
    }

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
     *
     * todo 邮件ScrollView滑动到底部应该允许外部布局可滑动(邮件框外层也可滑动的情况)
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