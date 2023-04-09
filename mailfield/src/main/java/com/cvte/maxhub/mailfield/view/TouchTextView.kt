package com.cvte.maxhub.mailfield.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatTextView

/**
 * 邮件地址单个Tag TextView
 *
 * @author tuwentai
 * @date 2020/3/25
 */
class TouchTextView @JvmOverloads constructor(
    context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {
    private val TAG = "TouchTextView"
    private var mOnClickViewListener: OnClickViewListener? = null

    interface OnClickViewListener {
        fun onClick(view: View?)
    }

    fun setOnClickViewListener(listener: OnClickViewListener) {
        mOnClickViewListener = listener
    }

    /*
    ScrollView滑动时会拦截事件，故滑动时不会触发点击邮件item相关逻辑，
    一次点击抬起能收到ACTION_DOWN，ACTION_CANCEL，（ACTION_UP，ACTION_MOVE仅在无滑动时能收到）
     */
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        when (val action = event.action) {
            MotionEvent.ACTION_DOWN -> {
                // 此行代码在此没有作用?
                // parent.requestDisallowInterceptTouchEvent(false)
                Log.d(TAG, "ACTION_DOWN")
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                Log.d("twt", "ACTION_MOVE")
                return false
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                when (action) {
                    MotionEvent.ACTION_UP -> {
                        Log.d(TAG, "ACTION_UP")
                    }
                    MotionEvent.ACTION_CANCEL -> {
                        Log.d(TAG, "ACTION_CANCEL")
                    }
                    else -> {
                        Log.d(TAG, "OTHER")
                    }
                }
                //parent.requestDisallowInterceptTouchEvent(true)
                if (action == MotionEvent.ACTION_UP && mOnClickViewListener != null) {
                    mOnClickViewListener!!.onClick(this)
                    Log.d(TAG, "mOnClickViewListener")
                }
            }
        }
        return false
    }

}