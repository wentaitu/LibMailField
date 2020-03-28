package com.cvte.maxhub.mailfield

import android.content.Context
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.ScrollView
import com.cvte.maxhub.mailfield.bean.EmailTag
import com.cvte.maxhub.mailfield.view.EmailAutoCompleteTextView
import com.cvte.maxhub.mailfield.view.MaxHeightScrollView
import kotlinx.android.synthetic.main.layout_mail_address_field.view.*

/**
 * @author: linrunyu
 * @email linrunyu@cvte.com
 * @date 2019-09-20
 * @description: 继承邮件地址框TagView，实现外部调用公共方法
 * 如：添加地址、获取所有地址、清空地址等
 */
class EmailTagView @JvmOverloads
        constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    TagView(context, attrs, defStyleAttr),
    TagView.TagListener, View.OnFocusChangeListener, View.OnTouchListener  {

    private var mMailRegex = Regex(
        "[\\w!#$%&'*+/=?^_`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?")

    private var mAutoTv: EmailAutoCompleteTextView = this.mailAutoCompleteTextView
    private var mTagChangeListener: OnTagChangeListener? = null

    /**
     * 供外部监听当前所有Tag数量，地址是否合理
     */
    interface OnTagChangeListener {
        fun onTagChange(count: Int,  hasInvalidTag: Boolean)
    }

    init {
        setTagListener(this)
        mAutoTv.onFocusChangeListener = this
        mAutoTv.setOnTouchListener(this)
    }

    /**
     * 设置邮箱后缀添加限制
     */
    fun setRecipientLimit(limit: String) {
        mRecipientLimit = limit
        mAutoTv.setRecipientLimit(limit, editTvSuffixColor)
        if (!TextUtils.isEmpty(limit)) {
            setTagLineFeedDistance(mAutoTv.paint.measureText(limit) + 0.5f)
        }
    }

    /**
     * 获取所有邮箱地址
     */
    fun getAllAddress(): String {
        var value = StringBuffer()
        val tags: List<EmailTag> = tags
        tags.forEach {
            value.append(it.text).append(";")
        }
        if (value.isNotEmpty()) {
            value.deleteCharAt(value.length - 1)
        }

        return value.toString()
    }

    /**
     * 获取Tag个数
     */
    fun getTagCount(): Int {
        return tags.size
    }

    /**
     * 获取是否有不正确的Tag
     */
    fun hasInvalidTag(): Boolean {
        return tags.any { !it.isEmailOk }
    }

    fun setOnTagChangeListener(listener: OnTagChangeListener) {
        mTagChangeListener = listener
    }

    fun getAutoTv() : EmailAutoCompleteTextView {
        return mAutoTv
    }

    private val mAutoCompleteTextWatch = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            // nop
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (TextUtils.isEmpty(mRecipientLimit) || s.isEmpty()) {
                return
            }
            // if text changed is on delete, then return. when adding, add recipient limit as suffix if '@' occurs
            if (before > 0) {
                return
            } else {
                val at = '@'
                if (s[s.length - 1] == at) {
                    val completeText = s.subSequence(0, s.length - 1).toString() + mRecipientLimit
                    mAutoTv.setText("")
                    addNewEmailTag(completeText)
                }
            }
        }

        override fun afterTextChanged(s: Editable) {
            updateEmailTagStatus()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        mAutoTv.addTextChangedListener(mAutoCompleteTextWatch)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mAutoTv.removeTextChangedListener(mAutoCompleteTextWatch)
    }

    /**
     * 当mAutoTv无输入，点击任意Tag，Tag删除，字符串转入AutoCompleteTextView
     */
    override fun onTagClick(tag: EmailTag, position: Int) {
        // 此变量预留，若回调时mAutoTv有输入，可记录已输入值直接生成新Tag，被点击Tag处于可编辑状态
        var str = mAutoTv.text.toString()
        if (!TextUtils.isEmpty(str) && str.indexOf('@') == -1 && !TextUtils.isEmpty(mRecipientLimit)) {
            str += mRecipientLimit
        }

        // 若有邮件后缀，则恢复可编辑状态时去掉后缀
        if (!TextUtils.isEmpty(mAutoTv.recipientLimit)) {
            mAutoTv.setText(tag.text.substring(0, tag.text.indexOf('@')))
        } else {
            mAutoTv.setText(tag.text)
        }
        mAutoTv.setSelection(mAutoTv.text.length)
        removeNoRefresh(position)
        val result = addNewEmailTag(str)
        if (!result) {
            refresh()
        }
        updateEmailTagStatus()
    }

    override fun onTagAdd(tag: String) {
        addNewEmailTag(tag)
        updateEmailTagStatus()
    }

    override fun onTagDeleted(tag: EmailTag, position: Int) {
        updateEmailTagStatus()
    }

    override fun onTagAllDeleted() {
        updateEmailTagStatus()
    }

    private fun isRightMailAdress(mailAddress: String): Boolean {
        return !TextUtils.isEmpty(mailAddress) && mMailRegex.matches(mailAddress) }

    /**
     * 添加新邮件Tag
     */
    private fun addNewEmailTag(mailAddress: String): Boolean {
        if (TextUtils.isEmpty(mailAddress)) {
            return false
        }
        val emailTag = EmailTag(mailAddress)

        // 取消重复邮件地址的输入
        val iterator = tags.iterator()
        while (iterator.hasNext()) {
            val tag = iterator.next()
            if(TextUtils.equals(emailTag.text, tag.text)) {
                iterator.remove()
            }
        }

        var isRecipientOk = isRightMailAdress(mailAddress)
        if (isRecipientOk) {
            val recipientLimit = mRecipientLimit
            recipientLimit?.let {
                isRecipientOk = mailAddress.contains(recipientLimit)
            }
        }
        emailTag.isEmailOk = isRecipientOk
        addTag(emailTag)
        this.post {
            (this.parent.parent as ScrollView).fullScroll(ScrollView.FOCUS_DOWN)
            mAutoTv.requestFocus()
        }
        return true
    }

    /**
     * 邮件输入框有无焦点可动态改变背景框，若有地址后缀则无焦点时隐藏后缀
     */
    override fun onFocusChange(view: View?, hasFocus: Boolean) {
        if (view === mAutoTv) {
            var scrollView = tagViewEmail.parent.parent as MaxHeightScrollView
            if (hasFocus) {
                mAutoTv.setRecipientLimit(mRecipientLimit, editTvSuffixColor)
                scrollView.setBackgroundResource(focusScrollViewBg)
            } else {
                scrollView.setBackgroundResource(loseFocusScrollViewBg)
                if (tags.isNotEmpty()) {
                    mAutoTv.setRecipientLimit(null, editTvSuffixColor)
                }
            }
        }
    }

    /**
     * 设置邮件输入框失去焦点，若存在输入则自动生成邮件地址Tag
     */
    fun changeFocusAutoAddTag() {
        if (!TextUtils.isEmpty(mAutoTv.text.toString())) {
            val completeText = mAutoTv.text.toString() + mRecipientLimit
            addNewEmailTag(completeText)
            mAutoTv.setText("")
            updateEmailTagStatus()
        }
    }

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP ||
                event.action == MotionEvent.ACTION_CANCEL) {
            when (view) {
                mAutoTv -> {
                    updateEmailTagStatus()
                }
            }
        }
        return false
    }

    private fun updateEmailTagStatus() {
        mTagChangeListener?.onTagChange(getTagCount(), hasInvalidTag())
    }

}
