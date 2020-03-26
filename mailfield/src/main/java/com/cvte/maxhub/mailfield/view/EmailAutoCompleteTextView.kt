package com.cvte.maxhub.mailfield.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.LinearLayout
import android.widget.TextView
import com.cvte.maxhub.mailfield.R
import com.cvte.maxhub.mailfield.config.MailFieldConfig

/**
 * @author tuwentai
 * @email i_tuwentai@cvte.com
 * @date 2020-03-25
 * @description: 字符输入AutoCompleteTextView，支持后缀
 */
class EmailAutoCompleteTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AutoCompleteTextView(context, attrs, defStyleAttr) {

    private var mListener: OnAutoCompleteClickListener? = null
    private var mLengthListener: OnAutoCompleteTextLengthListener? = null
    private var mEmailAutoAdapt: EmailAutoCompleteAdapter? = null
    var recipientLimit: String? = null
    private var mBeforeChangedText = ""
    private var mRecipientLimitColor = 0
    private var mFilterRegex  = Regex("^[a-zA-Z0-9_]+$")

    /**
     * onItemClick()用于准备添加新Tag时给上层View回调,
     * onDelete()用于字符输入时回退键监听：用于判断删除Tag or 删除字符
     */
    interface OnAutoCompleteClickListener {
        fun onItemClick(email: String)
        fun onDelete()
    }

    /**
     * 监听字符输入长度，用于判断是否应换行
     */
    interface OnAutoCompleteTextLengthListener {
        fun onLengthChange(length: Float)
    }

    fun setRecipientLimit(recipientLimit: String?, recipientLimitColor: Int) {
        this.recipientLimit = recipientLimit
        mRecipientLimitColor = recipientLimitColor
    }

    fun setAutoCompleteItemClickListener(listener: OnAutoCompleteClickListener?) {
        mListener = listener
    }

    fun setAutoCompleteTextLengthListener(listener: OnAutoCompleteTextLengthListener?) {
        mLengthListener = listener
    }

    fun refreshBeforeChangedText() {
        postDelayed(mRefreshTextRunnable, 200L)
    }

    fun destroy() {
        removeCallbacks(mRefreshTextRunnable)
        removeTextChangedListener(mTextWatcher)
    }

    init {
        setDropDownBackgroundResource(MailFieldConfig.AutoCompleteTextView_DROPDOWN_BG_RES_ID)
        mEmailAutoAdapt = EmailAutoCompleteAdapter(
            context, R.layout.email_auto_list_item, MailFieldConfig.MAIL_SUFFIXS)
        setAdapter(mEmailAutoAdapt)

        if (MailFieldConfig.NEED_SHOW_AUTO_COMPLETE) {
            this.threshold = 1
        } else {
            this.threshold = Int.MAX_VALUE
        }

        setOnKeyListener { _, keyCode, event ->
            enterKeyPressGenerateTag(keyCode, event)
            delete(keyCode, event)
            false
        }

        this.onItemClickListener = OnItemClickListener { _, view, _, _ ->
            if (mListener != null) {
                var textViewItem = (view as LinearLayout).getChildAt(0) as TextView
                var textAddress = textViewItem.text.toString()
                this@EmailAutoCompleteTextView.setText("")
                itemClick(textAddress)
            }
        }

        // Enter键触发添加Tag，不统一至OnKeyListener处理是由于Enter Key可能返回null
        setOnEditorActionListener(object : OnEditorActionListener {
            override fun onEditorAction(v: TextView, actionId: Int, event: KeyEvent?): Boolean {
                // 空字符不处理
                if (TextUtils.isEmpty(text)) {
                    return true
                }
                // 只处理up事件，避免添加多次
                if (event != null && event.action != KeyEvent.ACTION_UP) {
                    return true
                }
                if (mListener != null) {
                    var mail = text.toString()
                    if (!TextUtils.isEmpty(recipientLimit) && !mail.contains(recipientLimit!!)) {
                        mail += recipientLimit
                    }
                    this@EmailAutoCompleteTextView.setText("")
                    itemClick(mail)
                    this@EmailAutoCompleteTextView.isFocusable = true
                    this@EmailAutoCompleteTextView.requestFocus()
                }
                return true
            }
        })
    }

    private fun itemClick(text: String) {
        if (mListener != null) {
            mListener!!.onItemClick(text)
            refreshBeforeChangedText()
        }
    }

    /**
     * 物理键盘处理
     * 小键盘Enter键KeyEvent.KEYCODE_NUMPAD_ENTER生成Tag单独处理
     */
    private fun enterKeyPressGenerateTag(keyCode: Int, event: KeyEvent) {
        if (keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER ||
            keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
            // 只处理up事件，避免添加多次
            if (event.action != KeyEvent.ACTION_UP) {
                return
            }
            if (TextUtils.isEmpty(text)) {
                return
            }
            if (mListener != null) {
                var mail = text.toString()
                if (keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) {
                    mail = mail.replace("\n", "")
                }
                if (!TextUtils.isEmpty(recipientLimit) && !mail.contains(recipientLimit!!)) {
                    mail = mail + recipientLimit
                }
                this@EmailAutoCompleteTextView.setText("")
                itemClick(mail)
                this@EmailAutoCompleteTextView.isFocusable = true
                this@EmailAutoCompleteTextView.requestFocus()
            }
        }
    }

    private fun delete(keyCode: Int, event: KeyEvent) {
        if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_UP) {
            if (mListener != null && TextUtils.isEmpty(mBeforeChangedText)) {
                mListener!!.onDelete()
            }
            mBeforeChangedText = text.toString()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val hint = hint
        if (hint != null && hint.isNotEmpty()) {
            return
        }
        if (TextUtils.isEmpty(recipientLimit)) {
            return
        }
        val paint: Paint = paint
        val oldColor = paint.color
        paint.color = mRecipientLimitColor
        val width = paint.measureText(text.toString())
        val fontMetrics = paint.fontMetrics
        val y =
            height / 2 + (Math.abs(fontMetrics.ascent) - fontMetrics.descent) / 2
        canvas.drawText(recipientLimit, paddingLeft + width, y, paint)
        paint.color = oldColor
    }

    override fun replaceText(text: CharSequence) {
        var t = text.toString()
        val index = t.indexOf('@')
        if (index != -1) {
            t = t.substring(0, index)
        }
        super.replaceText(t + text)
    }

    /**
     * 该方法会在用户输入文本之后调用，将已输入的文本与adapter中的数据对比，
     * 若匹配adapter中数据的前半部分，那么adapter中的这条数据将会在下拉框中出现
     * @param text CharSequence
     * @param keyCode Int
     */
    override fun performFiltering(text: CharSequence, keyCode: Int) {
        //因为用户输入邮箱时，都是以字母，数字开始，而我们的adapter中只会提供以类似于"@163.com"
        //的邮箱后缀，因此在调用super.performFiltering时，传入的一定是以"@"开头的字符串
        val index = text.indexOf('@')
        if (index == -1) {
            if (mFilterRegex.matches(text)) {
                super.performFiltering("@", keyCode)
            } else {
                dismissDropDown() //当用户中途输入非法字符时，关闭下拉提示框
            }
        } else {
            super.performFiltering(text.substring(index), keyCode)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        addTextChangedListener(mTextWatcher)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        destroy()
    }

    private val mRefreshTextRunnable = Runnable {
        mBeforeChangedText = text.toString()
    }

    private val mTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            mBeforeChangedText = s.toString()
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (s.toString().contains(" ")) {
                val str = s.toString().split(" ").toTypedArray()
                val str1 = StringBuilder()
                for (i in str.indices) {
                    str1.append(str[i])
                }
                this@EmailAutoCompleteTextView.setText(str1.toString())
                this@EmailAutoCompleteTextView.setSelection(start)
            }
        }

        override fun afterTextChanged(s: Editable) {
            if (mLengthListener != null) {
                mLengthListener!!.onLengthChange(paint.measureText(s.toString() + recipientLimit))
            }
        }
    }

    private inner class EmailAutoCompleteAdapter(
        context: Context, layoutId: Int, pEmails: Array<String>
    ) : ArrayAdapter<String>(context, layoutId, pEmails) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var convertView = convertView
            val holder: ViewHolder
            if (convertView == null) {
                holder = ViewHolder()
                convertView = LayoutInflater.from(context).inflate(R.layout.email_auto_list_item, null)
                holder.emailTextView = convertView.findViewById(R.id.tv)
                holder.emailTextView!!.setTextColor(MailFieldConfig.AutoCompleteTextView_ITEM_TEXT_COLOR)
                convertView.tag = holder
            } else {
                holder = convertView.tag as ViewHolder
            }
            var strText = this@EmailAutoCompleteTextView.text.toString()
            val index = strText.indexOf('@')
            if (index != -1) {
                strText = strText.substring(0, index)
            }
            val strAddress = strText + getItem(position)
            holder.emailTextView!!.text = strAddress
            return convertView!!
        }

        internal inner class ViewHolder {
            var emailTextView: TextView? = null
        }
    }

}