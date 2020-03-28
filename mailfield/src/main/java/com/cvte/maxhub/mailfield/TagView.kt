package com.cvte.maxhub.mailfield

import android.content.Context
import android.graphics.Typeface
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import com.cvte.maxhub.mailfield.bean.EmailTag
import com.cvte.maxhub.mailfield.config.MailFieldDefAttr
import com.cvte.maxhub.mailfield.view.EmailAutoCompleteTextView
import com.cvte.maxhub.mailfield.view.EmailAutoCompleteTextView.OnAutoCompleteClickListener
import com.cvte.maxhub.mailfield.view.EmailAutoCompleteTextView.OnAutoCompleteTextLengthListener
import com.cvte.maxhub.mailfield.view.TouchTextView
import com.cvte.maxhub.mailfield.view.TouchTextView.OnClickViewListener
import kotlin.math.min

/**
 * @author tuwentai
 * @email i_tuwentai@cvte.com
 * @date 2020-03-25
 * @description: 邮件地址框RelativeLayout，内部包含各个子Tag
 */
open class TagView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    // Email Tag List
    private val mTags: MutableList<EmailTag> = mutableListOf()

    private var mInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private var mTagListener: TagListener? = null
    protected var mRecipientLimit: String? = null
    // view width size param
    private var mWidth = 0
    // layout initialize flag
    private var mInitialized = false
    // 当处于删除Tag状态时，禁止AutoCompleteTextView RequestFocus
    // 否则将导致删除Tag时自动下滑到最底部
    private var mRequestFocusFlag = true
    // 当前未生成Tag，用于禁止AutoCompleteTextView首次输入不自动换行
    var isFirstInput = false

    // custom layout param
    var lineMargin = 0
    var tagMargin = 0
    var focusScrollViewBg = 0
    var loseFocusScrollViewBg = 0
    var tagTextSize = 0f         // 11
    var tagTextColor = 0
    var tagAbnormalTextColor = 0
    var tagTextPaddingLeft = 0
    var tagTextPaddingRight = 0
    var tagTextPaddingTop = 0
    var tagTexPaddingBottom = 0
    var tagNormalLayoutBg = 0
    var tagAbnormalLayoutBg = 0
    var tagNormalDeleteBg = 0
    var tagAbnormalDeleteBg = 0
    var tagIsDeletable = true
    var editTvDropdownBg = 0
    var editTvDropdownItemTextColor = 0
    var editTvTextSize = 0f
    var editTvTextColor = 0
    var editTvSuffixColor = 0
    var editTvHeight = 0
    var editTvShowDropdownList = false
    /**
     * AutoCompleteTextView添加至新行剩余距离最小值
     * @see addAutoCompleteTextViewToBelow
     */
    var editTvLineFeedDistance = 0

    fun setTagLineFeedDistance(size: Float) {
        editTvLineFeedDistance = dpToPx(context,size)
    }

    protected lateinit var mailAutoCompleteTextView: EmailAutoCompleteTextView
    private var mAutoCompleteParams: LayoutParams? = null

    /**
     * 增加Tag、重新编辑Tag、删除Tag的真实操作回调
     */
    interface TagListener {
        fun onTagAdd(tag: String)
        fun onTagClick(tag: EmailTag, position: Int)
        fun onTagDeleted(tag: EmailTag, position: Int)
        fun onTagAllDeleted()
    }

    init {
        viewTreeObserver.addOnGlobalLayoutListener {
            if (!mInitialized) {
                mInitialized = true
                drawTags()
            }
        }

        val typeArray = context.obtainStyledAttributes(attrs, R.styleable.TagView, defStyleAttr, defStyleAttr)
        lineMargin = typeArray.getDimension(R.styleable.TagView_lineMargin,
            dpToPx(this.context, MailFieldDefAttr.DEFAULT_LINE_MARGIN).toFloat()).toInt()
        tagMargin = typeArray.getDimension(R.styleable.TagView_tagMargin,
            dpToPx(this.context, MailFieldDefAttr.DEFAULT_TAG_MARGIN).toFloat()).toInt()
        focusScrollViewBg = typeArray.getResourceId(R.styleable.TagView_focusScrollViewBg,
            MailFieldDefAttr.TAG_ON_FOCUS_SCROLLVIEW_BG_RES_ID)
        loseFocusScrollViewBg = typeArray.getResourceId(R.styleable.TagView_loseFocusScrollViewBg,
            MailFieldDefAttr.TAG_ON_OUT_FOCUS_SCROLLVIEW_BG_RES_ID)

        tagTextSize = typeArray.getDimension(R.styleable.TagView_tagTextSize,
            spToPx(this.context, MailFieldDefAttr.DEFAULT_TAG_TEXT_SIZE).toFloat())
        tagTextColor = typeArray.getColor(R.styleable.TagView_tagTextColor,
            MailFieldDefAttr.DEFAULT_TAG_TEXT_COLOR)
        tagAbnormalTextColor = typeArray.getColor(R.styleable.TagView_tagAbnormalTextColor,
            MailFieldDefAttr.TAG_WRONG_ADDRESS_TEXT_COLOR)
        tagTextPaddingLeft = typeArray.getDimension(R.styleable.TagView_tagTextPaddingLeft,
            dpToPx(this.context, MailFieldDefAttr.DEFAULT_TAG_TEXT_PADDING_LEFT).toFloat()).toInt()
        tagTextPaddingRight = typeArray.getDimension(R.styleable.TagView_tagTextPaddingRight,
            dpToPx(this.context, MailFieldDefAttr.DEFAULT_TAG_TEXT_PADDING_RIGHT).toFloat()).toInt()
        tagTextPaddingTop = typeArray.getDimension(R.styleable.TagView_tagTextPaddingTop,
            dpToPx(this.context, MailFieldDefAttr.DEFAULT_TAG_TEXT_PADDING_TOP).toFloat()).toInt()
        tagTexPaddingBottom = typeArray.getDimension(R.styleable.TagView_tagTextPaddingBottom,
            dpToPx(this.context, MailFieldDefAttr.DEFAULT_TAG_TEXT_PADDING_BOTTOM).toFloat()).toInt()
        tagNormalLayoutBg = typeArray.getResourceId(R.styleable.TagView_tagNormalLayoutBg,
            MailFieldDefAttr.DEFAULT_TAG_LAYOUT_BG_RES_ID)
        tagAbnormalLayoutBg = typeArray.getResourceId(R.styleable.TagView_tagAbnormalLayoutBg,
            MailFieldDefAttr.TAG_WRONG_LAYOUT_BG_RES_ID)
        tagNormalDeleteBg = typeArray.getResourceId(R.styleable.TagView_tagNormalDeleteBg,
            MailFieldDefAttr.DEFAULT_TAG_DELETE_BG_RES_ID)
        tagAbnormalDeleteBg = typeArray.getResourceId(R.styleable.TagView_tagAbnormalDeleteBg,
            MailFieldDefAttr.TAG_WRONG_DELETE_BG_RES_ID)
        tagIsDeletable = typeArray.getBoolean(R.styleable.TagView_tagIsDeletable,
            MailFieldDefAttr.DEFAULT_TAG_IS_DELETABLE)

        editTvLineFeedDistance = typeArray.getDimension(R.styleable.TagView_editTvLineFeedDistance,
            dpToPx(this.context, MailFieldDefAttr.TAG_LINE_FEED_DISTANCE).toFloat()).toInt()
        editTvDropdownBg = typeArray.getResourceId(R.styleable.TagView_editTvDropdownBg,
            MailFieldDefAttr.AutoCompleteTextView_DROPDOWN_BG_RES_ID)
        editTvDropdownItemTextColor = typeArray.getColor(R.styleable.TagView_editTvDropdownItemTextColor,
            MailFieldDefAttr.AutoCompleteTextView_ITEM_TEXT_COLOR)
        editTvTextSize = typeArray.getDimension(R.styleable.TagView_editTvTextSize,
            spToPx(this.context, MailFieldDefAttr.AutoCompleteTextView_TEXT_SIZE).toFloat())
        editTvTextColor = typeArray.getColor(R.styleable.TagView_editTvTextColor,
            MailFieldDefAttr.AutoCompleteTextView_TEXT_COLOR)
        editTvSuffixColor = typeArray.getColor(R.styleable.TagView_editTvSuffixColor,
            MailFieldDefAttr.AutoCompleteTextView_SUFFIX_TEXT_COLOR)
        editTvHeight = typeArray.getDimension(R.styleable.TagView_editTvHeight,
            dpToPx(this.context, MailFieldDefAttr.AutoCompleteTextView_HEIGHT).toFloat()).toInt()
        editTvShowDropdownList = typeArray.getBoolean(R.styleable.TagView_editTvShowDropdownList,
            MailFieldDefAttr.AutoCompleteTextView_SHOW_DROPDOWN_LIST)
        typeArray.recycle()

        isFocusable = true
        isFocusableInTouchMode = true
        initAutoCompleteTextView()
    }

    private fun initAutoCompleteTextView() {
        mailAutoCompleteTextView = mInflater.inflate(R.layout.email_auto_layout, null) as EmailAutoCompleteTextView
        mailAutoCompleteTextView.isFocusable = true
        mailAutoCompleteTextView.isFocusableInTouchMode = true
        mailAutoCompleteTextView.setTextColor(editTvTextColor)
        mailAutoCompleteTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, editTvTextSize)
        mailAutoCompleteTextView.threshold = if (editTvShowDropdownList && mRecipientLimit != null) 1 else Int.MAX_VALUE
        mailAutoCompleteTextView.dropdownItemTextColor = editTvDropdownItemTextColor
        mailAutoCompleteTextView.setDropDownBackgroundResource(editTvDropdownBg)
        // mAutoCompleteTextView.setFilters(new InputFilter[] { new InputFilter.LengthFilter(32) });
        mailAutoCompleteTextView.setAutoCompleteItemClickListener(object :
            OnAutoCompleteClickListener {
            override fun onItemClick(email: String) {
                if (mTagListener != null) {
                    mTagListener!!.onTagAdd(email)
                }
            }

            override fun onDelete() {
                if (mTags.isEmpty()) {
                    return
                }
                val position = mTags.size - 1
                val tag = mTags[position]
                this@TagView.remove(position)
                if (mTagListener != null) {
                    mTagListener!!.onTagDeleted(tag, position)
                }
            }
        })
        mailAutoCompleteTextView.setAutoCompleteTextLengthListener(object :
            OnAutoCompleteTextLengthListener {
            override fun onLengthChange(length: Float) {
                if (length > mailAutoCompleteTextView.width) {
                    dynamicChangeAutoCompleteTextViewToBelow()
                }
            }
        })
        mailAutoCompleteTextView.requestFocus()
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        mWidth = width
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = measuredWidth
        if (width <= 0) return
        mWidth = measuredWidth
    }

    private fun drawTags() {
        if (!mInitialized) {
            return
        }
        // clear all TAG
        removeAllViews()
        // 记录本行所有Tag长度 用于判断
        var total = paddingLeft + paddingRight.toFloat()
        var listIndex = 1 // List Index
        var indexHeader = 1 // The header TAG of this line
        for (item in mTags) {
            val position = listIndex - 1
            // inflate TAG layout
            val tagLayout = mInflater.inflate(R.layout.email_tagview_item, null)
            tagLayout.id = listIndex
            if (item.isEmailOk) {
                tagLayout.setBackgroundResource(tagNormalLayoutBg)
            } else {
                tagLayout.setBackgroundResource(tagAbnormalLayoutBg)
            }
            var deleteWidth = 0f
            // deletable text
            val deletableView = tagLayout.findViewById<TextView>(R.id.tv_tag_item_delete)
            if (tagIsDeletable) {
                deletableView.visibility = View.VISIBLE
                if (item.isEmailOk) {
                    deletableView.setBackgroundResource(tagNormalDeleteBg)
                } else {
                    deletableView.setBackgroundResource(tagAbnormalDeleteBg)
                }
                deletableView.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                deletableView.setOnClickListener {
                    mRequestFocusFlag = false
                    this@TagView.remove(position)
                    mRequestFocusFlag = true
                    if (mTagListener != null) {
                        mTagListener!!.onTagDeleted(item, position)
                    }
                }
                // deletableView Padding (left & right)
                deleteWidth = deletableView.paddingLeft + deletableView.paddingRight + dpToPx(context, 18f).toFloat()
            } else {
                deletableView.visibility = View.GONE
            }
            // TAG text
            val addressTv: TouchTextView = tagLayout.findViewById(R.id.tv_tag_item_contain)
            addressTv.text = item.text
            addressTv.setPadding(tagTextPaddingLeft, tagTextPaddingTop, tagTextPaddingRight, tagTexPaddingBottom)
            if (item.isEmailOk) {
                addressTv.setTextColor(tagTextColor)
            } else {
                addressTv.setTextColor(tagAbnormalTextColor)
            }
            addressTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, tagTextSize)
            addressTv.setOnClickViewListener(object : OnClickViewListener {
                override fun onClick(view: View?) {
                    if (mTagListener != null && TextUtils.isEmpty(mailAutoCompleteTextView.text)) {
                        mTagListener!!.onTagClick(item, position)
                    }
                }
            })
            val maxTagViewWidth =
                (mWidth - deleteWidth - paddingLeft - paddingRight - tagMargin * 2).toInt()
            addressTv.maxWidth = maxTagViewWidth
            // calculate　of TAG layout width  padding (left & right)，不能直接使用float tagWidth = tagLayout.getWidth()
            val tagWidth = min(
                mWidth - tagMargin * 2.toFloat(),
                addressTv.paint.measureText(item.text) + tagTextPaddingLeft + tagTextPaddingRight + deleteWidth
            )
            val tagParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)

            when {
                (position == 0) -> {
                    // first tag added
                    tagParams.topMargin = lineMargin
                    tagParams.leftMargin = tagMargin
                    tagParams.rightMargin = tagMargin
                }
                (mWidth <= total + tagWidth + tagMargin * 2) -> {
                    //need to add in new line
                    tagParams.addRule(BELOW, indexHeader)
                    // 每次添加到新行则初始化此行被Tag占用的长度
                    total = paddingLeft + paddingRight.toFloat()
                    // 记录此行头一个Tag的ID
                    indexHeader = listIndex
                    tagParams.topMargin = lineMargin
                    tagParams.leftMargin = tagMargin
                    tagParams.rightMargin = tagMargin
                }
                else -> {
                    //no need to new line
                    tagParams.addRule(ALIGN_TOP, indexHeader)
                    tagParams.addRule(RIGHT_OF, listIndex - 1)
                    tagParams.rightMargin = tagMargin
                }
            }

            if (listIndex == mTags.size) {
                tagParams.bottomMargin = lineMargin
            }
            total += tagWidth + tagMargin
            addView(tagLayout, tagParams)
            listIndex++
        }

        if (mTags.size == 0) {
            isFirstInput = true
            initAddAutoCompleteTextView()
        } else if (!TextUtils.isEmpty(mailAutoCompleteTextView.text)) {
            // 处理重新编辑Tag情况
            val textLength =
                mailAutoCompleteTextView.paint.measureText(mailAutoCompleteTextView.text.toString()).toInt()
            if (TextUtils.isEmpty(mRecipientLimit)) {
                if (mWidth - total - tagMargin * 2 <= textLength) {
                    addAutoCompleteTextViewToBelow(indexHeader)
                } else {
                    addAutoCompleteTextViewToRight(indexHeader, listIndex - 1)
                }
            } else {
                if (mWidth - total - tagMargin * 2 <= editTvLineFeedDistance + textLength) {
                    addAutoCompleteTextViewToBelow(indexHeader)
                } else {
                    addAutoCompleteTextViewToRight(indexHeader, listIndex - 1)
                }
            }
        } else if (mWidth - total - tagMargin * 2 <= editTvLineFeedDistance) {
            // 不能直接使用mAutoCompleteTextView.getMeasuredWidth() <= tagLineFeedDistance判断换行
            // 此时新的AutoCompleteTextView还未变更，仍处于旧的宽度
            isFirstInput = false
            addAutoCompleteTextViewToBelow(indexHeader)
        } else {
            isFirstInput = false
            addAutoCompleteTextViewToRight(indexHeader, listIndex - 1)
        }
    }

    /**
     * 添加AutoCompleteTextView到结尾Tag右边
     * @param topId Int 上一行头部Tag id
     * @param leftId Int 本行结尾Tag id
     */
    private fun addAutoCompleteTextViewToRight(topId: Int, leftId: Int) {
        mAutoCompleteParams = LayoutParams(LayoutParams.MATCH_PARENT, editTvHeight)
        mAutoCompleteParams!!.rightMargin = tagMargin
        // mAutoCompleteParams.bottomMargin = lineMargin;
        mAutoCompleteParams!!.addRule(ALIGN_TOP, topId)
        mAutoCompleteParams!!.addRule(ALIGN_BOTTOM, topId)
        mAutoCompleteParams!!.addRule(RIGHT_OF, leftId)
        addView(mailAutoCompleteTextView, mAutoCompleteParams)
        mailAutoCompleteTextView.isFocusable = true
        if (!mRequestFocusFlag) return
        mailAutoCompleteTextView.requestFocus()
    }

    /**
     * 添加AutoCompleteTextView到结尾Tag下边
     * @param bottomId Int 本行头部Tag id
     */
    private fun addAutoCompleteTextViewToBelow(bottomId: Int) {
        mAutoCompleteParams = LayoutParams(LayoutParams.MATCH_PARENT, editTvHeight)
        mAutoCompleteParams!!.leftMargin = tagMargin
        mAutoCompleteParams!!.rightMargin = tagMargin
        // mAutoCompleteParams.bottomMargin = lineMargin;
        mAutoCompleteParams!!.addRule(BELOW, bottomId)
        addView(mailAutoCompleteTextView, mAutoCompleteParams)
        mailAutoCompleteTextView.isFocusable = true
        if (!mRequestFocusFlag) return
        mailAutoCompleteTextView.requestFocus()
    }

    /**
     * TagView初始化时首次添加AutoCompleteTextView
     */
    private fun initAddAutoCompleteTextView() {
        mAutoCompleteParams = LayoutParams(LayoutParams.MATCH_PARENT, editTvHeight)
        mAutoCompleteParams!!.leftMargin = tagMargin
        mAutoCompleteParams!!.rightMargin = tagMargin
        addView(mailAutoCompleteTextView, mAutoCompleteParams)
        mailAutoCompleteTextView.isFocusable = true
        if (!mRequestFocusFlag) return
        mailAutoCompleteTextView.requestFocus()
    }

    /**
     * AutoCompleteTextView未独占一行时，字符输入到达末尾即自动换行
     */
    private fun dynamicChangeAutoCompleteTextViewToBelow() {
        if (mAutoCompleteParams!!.getRule(BELOW) != 0 || isFirstInput) {
            return
        }
        mAutoCompleteParams!!.leftMargin = tagMargin
        mAutoCompleteParams!!.addRule(BELOW, mAutoCompleteParams!!.getRule(RIGHT_OF))
        mAutoCompleteParams!!.removeRule(ALIGN_TOP)
        mAutoCompleteParams!!.removeRule(ALIGN_BOTTOM)
        mAutoCompleteParams!!.removeRule(RIGHT_OF)
        requestLayout()
    }

    fun refresh() {
        drawTags()
    }

    fun addTagNoRefresh(tag: EmailTag) {
        mTags.add(tag)
    }

    fun addTag(tag: EmailTag) {
        mTags.add(tag)
        drawTags()
    }

    fun addTags(tags: Array<String>?) {
        if (tags == null) return
        for (item in tags) {
            val tag = EmailTag(item)
            addTag(tag)
        }
    }

    /**
     * get TAG list
     * @return mTags TagObject List
     */
    val tags: MutableList<EmailTag>
        get() = mTags

    fun setTagListener(tagListener: TagListener?) {
        mTagListener = tagListener
    }

    /**
     * remove TAG
     * @param position
     */
    fun remove(position: Int) {
        mTags.removeAt(position)
        drawTags()
    }

    fun removeNoRefresh(position: Int) {
        mTags.removeAt(position)
    }

    /**
     * Remove All Tags
     */
    fun removeAllTags() {
        mTags.clear()
        drawTags()
    }

    /**
     * 清除所有Tag&清除AutoCompleteTextView输入并回调给上层EmailTagView更新状态
     */
    fun clear() {
        mailAutoCompleteTextView.setText("")
        removeAllTags()
        mTagListener!!.onTagAllDeleted()
    }

    fun destroy() {
        mailAutoCompleteTextView.destroy()
    }

    private fun dpToPx(context: Context, dp: Float): Int {
        val metrics = context.resources.displayMetrics
        return TypedValue.applyDimension(1, dp, metrics).toInt()
    }

    private fun spToPx(context: Context, spValue: Float): Int {
        val metrics = context.resources.displayMetrics
        return TypedValue.applyDimension(2, spValue, metrics).toInt()
    }

}