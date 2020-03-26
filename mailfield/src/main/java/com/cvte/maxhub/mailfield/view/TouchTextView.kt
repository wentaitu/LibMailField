package com.cvte.maxhub.mailfield.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatTextView

/**
 * @author tuwentai
 * @email i_tuwentai@cvte.com
 * @date 2020-03-25
 * @description: 邮件地址Tag TextView
 */
class TouchTextView @JvmOverloads constructor(
    context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    private var mOnClickViewListener: OnClickViewListener? = null

    interface OnClickViewListener {
        fun onClick(view: View?)
    }

    fun setOnClickViewListener(listener: OnClickViewListener) {
        mOnClickViewListener = listener
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        when (val action = event.action) {
            MotionEvent.ACTION_DOWN -> {
                parent.requestDisallowInterceptTouchEvent(false)
                return true
            }

            MotionEvent.ACTION_MOVE -> return false

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                parent.requestDisallowInterceptTouchEvent(true)
                if (action == MotionEvent.ACTION_UP && mOnClickViewListener != null) {
                    mOnClickViewListener!!.onClick(this)
                }
            }
        }
        return false
    }

}