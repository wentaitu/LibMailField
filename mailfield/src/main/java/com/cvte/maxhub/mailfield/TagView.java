package com.cvte.maxhub.mailfield;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class TagView extends RelativeLayout {

    // Email Tag List
    private List<EmailTag> mTags = new ArrayList<>();

    private LayoutInflater mInflater;
    private ViewTreeObserver mViewTreeObserver;

    private TagListener mTagListener;

    protected String mRecipientLimit;

    // view size param
    private int mWidth;

    // layout initialize flag
    private boolean mInitialized = false;

    // 当处于删除Tag状态时，禁止AutoCompleteTextView RequestFocus
    // 否则将导致删除Tag时自动下滑到最底部
    private boolean mRequestFocusFlag = true;

    // 当前未生成Tag，用于首次输入不换行
    boolean isFirstInput = false;

    // custom layout param
    int lineMargin;
    int tagMargin;
    int textPaddingLeft;
    int textPaddingRight;
    int textPaddingTop;
    int texPaddingBottom;

    /**
     * mAutoCompleteTextView 添加至新行的最小值
     * @see #addAutoCompleteTextViewToBelow(int)
     * 若已配置邮件后缀，则最小宽度为邮件后缀长度
     * @see EmailTagView #setRecipientLimit(String)
     */
    private int tagLineFeedDistance = dpToPx(getContext(), MailFieldConfig.TAG_LINE_FEED_DISTANCE);

    public static int dpToPx(Context context, float dp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int)TypedValue.applyDimension(1, dp, metrics);
    }

    public void setTagLineFeedDistance(int size) {
        tagLineFeedDistance = size;
    }

    private EmailAutoCompleteTextView mAutoCompleteTextView;
    private LayoutParams mAutoCompleteParams;

    public interface TagListener {
        void onTagAdd(String tag);
        void onTagClick(EmailTag tag, int position);
        void onTagDeleted(EmailTag tag, int position);
        void onTagAllDeleted();
    }

    public TagView(Context ctx) {
        super(ctx, null);
        initialize(ctx, null, 0);
    }

    public TagView(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
        initialize(ctx, attrs, 0);
    }

    public TagView(Context ctx, AttributeSet attrs, int defStyle) {
        super(ctx, attrs, defStyle);
        initialize(ctx, attrs, defStyle);
    }

    private void initialize(Context ctx, AttributeSet attrs, int defStyle) {
        mInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mViewTreeObserver = getViewTreeObserver();
        mViewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!mInitialized) {
                    mInitialized = true;
                    drawTags();
                }
            }
        });
        TypedArray typeArray = ctx.obtainStyledAttributes(attrs, R.styleable.TagView, defStyle, defStyle);
        this.lineMargin = (int) typeArray.getDimension(R.styleable.TagView_lineMargin, dpToPx(this.getContext(), MailFieldConfig.DEFAULT_LINE_MARGIN));
        this.tagMargin = (int) typeArray.getDimension(R.styleable.TagView_tagMargin, dpToPx(this.getContext(), MailFieldConfig.DEFAULT_TAG_MARGIN));
        this.textPaddingLeft = (int) typeArray.getDimension(R.styleable.TagView_textPaddingLeft, dpToPx(this.getContext(), MailFieldConfig.DEFAULT_TAG_TEXT_PADDING_LEFT));
        this.textPaddingRight = (int) typeArray.getDimension(R.styleable.TagView_textPaddingRight, dpToPx(this.getContext(), MailFieldConfig.DEFAULT_TAG_TEXT_PADDING_RIGHT));
        this.textPaddingTop = (int) typeArray.getDimension(R.styleable.TagView_textPaddingTop, dpToPx(this.getContext(), MailFieldConfig.DEFAULT_TAG_TEXT_PADDING_TOP));
        this.texPaddingBottom = (int) typeArray.getDimension(R.styleable.TagView_textPaddingBottom, dpToPx(this.getContext(), MailFieldConfig.DEFAULT_TAG_TEXT_PADDING_BOTTOM));
        typeArray.recycle();
        setFocusable(true);
        setFocusableInTouchMode(true);
        initAutoCompleteTextView();
    }

    private void initAutoCompleteTextView() {
        mAutoCompleteTextView = (EmailAutoCompleteTextView) mInflater.inflate(R.layout.email_auto_layout, null);
        mAutoCompleteTextView.setFocusable(true);
        mAutoCompleteTextView.setFocusableInTouchMode(true);
        mAutoCompleteTextView.setTextColor(MailFieldConfig.AutoCompleteTextView_TEXT_COLOR);
        mAutoCompleteTextView.setTextSize(MailFieldConfig.AutoCompleteTextView_TEXT_SIZE);
        //mAutoCompleteTextView.setFilters(new InputFilter[] { new InputFilter.LengthFilter(32) });
        mAutoCompleteTextView.setAutoCompleteItemClickListener(new EmailAutoCompleteTextView.OnAutoCompleteClickListener() {
            @Override
            public void onItemClick(String email) {
                if (mTagListener != null) {
                    mTagListener.onTagAdd(email);
                }
            }

            @Override
            public void onDelete() {
                if (mTags == null || mTags.isEmpty()) {
                    return;
                }
                int position = mTags.size() - 1;
                EmailTag tag = mTags.get(position);
                TagView.this.remove(position);
                if (mTagListener != null) {
                    mTagListener.onTagDeleted(tag, position);
                }
            }
        });

        mAutoCompleteTextView.setAutoCompleteTextLengthListener(new EmailAutoCompleteTextView.OnAutoCompleteTextLengthListener() {
            @Override
            public void onLengthChange(float length) {
                if (length > mAutoCompleteTextView.getWidth()) {
                    dynamicChangeAutoCompleteTextViewToBelow();
                }
            }
        });

        mAutoCompleteTextView.requestFocus();
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        mWidth = width;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        if (width <= 0) return;
        mWidth = getMeasuredWidth();
    }

    private void drawTags() {
        if (!mInitialized) {
            return;
        }
        // clear all TAG
        removeAllViews();
        // 记录本行所有Tag长度 用于判断
        float total = getPaddingLeft() + getPaddingRight();
        int listIndex = 1;     // List Index
        int index_header = 1;  // The header TAG of this line

        for (EmailTag item : mTags) {
            final int position = listIndex - 1;
            final EmailTag tag = item;
            // inflate TAG layout
            View tagLayout = mInflater.inflate(R.layout.email_tagview_item, null);
            tagLayout.setId(listIndex);
            tagLayout.setBackground(getSelector(tag));

            float deleteWidth = 0f;
            // deletable text
            TextView deletableView = tagLayout.findViewById(R.id.tv_tag_item_delete);
            if (tag.isDeletable) {
                deletableView.setVisibility(View.VISIBLE);
                deletableView.setBackgroundResource(tag.deleteBgResId);
                deletableView.setTextColor(tag.deleteIndicatorColor);
                deletableView.setTextSize(TypedValue.COMPLEX_UNIT_SP, tag.deleteIndicatorSize);
                deletableView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                deletableView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mRequestFocusFlag = false;
                        TagView.this.remove(position);
                        mRequestFocusFlag = true;
                        if (mTagListener != null) {
                            mTagListener.onTagDeleted(tag, position);
                        }
                    }
                });
                // deletableView Padding (left & right)
                deleteWidth = deletableView.getPaddingLeft() + deletableView.getPaddingRight() + dpToPx(getContext(), 18f);
            } else {
                deletableView.setVisibility(View.GONE);
            }

            // TAG text
            TouchTextView tagView = tagLayout.findViewById(R.id.tv_tag_item_contain);
            tagView.setText(tag.text);
            tagView.setPadding(textPaddingLeft, textPaddingTop, textPaddingRight, texPaddingBottom);
            tagView.setTextColor(tag.tagTextColor);
            tagView.setTextSize(tag.tagTextSize);
            tagView.setOnClickViewListener(new TouchTextView.OnClickViewListener() {
                @Override
                public void onClick(View view) {
                    if (mTagListener != null && TextUtils.isEmpty(mAutoCompleteTextView.getText())) {
                        mTagListener.onTagClick(tag, position);
                    }
                }
            });
            int maxTagViewWidth = (int) (mWidth - deleteWidth - getPaddingLeft() - getPaddingRight() - tagMargin * 2);
            tagView.setMaxWidth(maxTagViewWidth);
            // calculate　of TAG layout width  padding (left & right)，不能直接使用float tagWidth = tagLayout.getWidth()
            float tagWidth = Math.min(mWidth - tagMargin * 2, tagView.getPaint().measureText(tag.text) + textPaddingLeft + textPaddingRight + deleteWidth);

            LayoutParams tagParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

            if (position == 0) {
                // first tag added
                tagParams.topMargin = lineMargin;
                tagParams.leftMargin = tagMargin;
                tagParams.rightMargin = tagMargin;
            } else if (mWidth <= total + tagWidth + tagMargin * 2) {
                //need to add in new line
                tagParams.addRule(RelativeLayout.BELOW, index_header);
                // 每次添加到新行则初始化此行被Tag占用的长度
                total = getPaddingLeft() + getPaddingRight();
                // 记录此行头一个Tag的ID
                index_header = listIndex;
                tagParams.topMargin = lineMargin;
                tagParams.leftMargin = tagMargin;
                tagParams.rightMargin = tagMargin;
            } else {
                //no need to new line
                tagParams.addRule(RelativeLayout.ALIGN_TOP, index_header);
                tagParams.addRule(RelativeLayout.RIGHT_OF, listIndex - 1);
                tagParams.rightMargin = tagMargin;
            }

            if (listIndex == mTags.size()) {
                tagParams.bottomMargin = lineMargin;
            }
            total = total + tagWidth + tagMargin;
            addView(tagLayout, tagParams);
            listIndex++;
        }

        if (mTags.size() == 0) {
            isFirstInput = true;
            initAddAutoCompleteTextView();
        } else if (!TextUtils.isEmpty(mAutoCompleteTextView.getText().toString())) {
            // 处理重新编辑Tag情况
            int textLength = (int) mAutoCompleteTextView.getPaint().measureText(mAutoCompleteTextView.getText().toString());
            if (TextUtils.isEmpty(mRecipientLimit)) {
                if ((mWidth - total - tagMargin * 2) <= textLength) {
                    addAutoCompleteTextViewToBelow(index_header);
                } else {
                    addAutoCompleteTextViewToRight(index_header, listIndex - 1);
                }
            } else {
                if ((mWidth - total - tagMargin * 2) <= (tagLineFeedDistance + textLength)) {
                    addAutoCompleteTextViewToBelow(index_header);
                } else {
                    addAutoCompleteTextViewToRight(index_header, listIndex - 1);
                }
            }
        } else if ((mWidth - total - tagMargin * 2) <= tagLineFeedDistance) {
            // 不能使用mAutoCompleteTextView.getMeasuredWidth() <= tagLineFeedDistance，此时新的AutoCompleteTextView还未变更，仍处于旧的宽度
            isFirstInput = false;
            addAutoCompleteTextViewToBelow(index_header);
        } else {
            isFirstInput = false;
            addAutoCompleteTextViewToRight(index_header, listIndex - 1);
        }
    }

    private void addAutoCompleteTextViewToRight(int topId, int leftId) {
        mAutoCompleteParams = new LayoutParams(LayoutParams.MATCH_PARENT, dpToPx(getContext(), 20.33f));
        mAutoCompleteParams.rightMargin = tagMargin;
        // mAutoCompleteParams.bottomMargin = lineMargin;
        mAutoCompleteParams.addRule(RelativeLayout.ALIGN_TOP, topId);
        mAutoCompleteParams.addRule(RelativeLayout.ALIGN_BOTTOM, topId);
        mAutoCompleteParams.addRule(RelativeLayout.RIGHT_OF, leftId);
        addView(mAutoCompleteTextView, mAutoCompleteParams);
        mAutoCompleteTextView.setFocusable(true);
        if (!mRequestFocusFlag) return;
        mAutoCompleteTextView.requestFocus();
    }

    private void addAutoCompleteTextViewToBelow(int bottomId) {
        mAutoCompleteParams = new LayoutParams(LayoutParams.MATCH_PARENT, dpToPx(getContext(), 20.33f));
        mAutoCompleteParams.leftMargin = tagMargin;
        mAutoCompleteParams.rightMargin = tagMargin;
        // mAutoCompleteParams.bottomMargin = lineMargin;
        mAutoCompleteParams.addRule(RelativeLayout.BELOW, bottomId);
        addView(mAutoCompleteTextView, mAutoCompleteParams);
        mAutoCompleteTextView.setFocusable(true);
        if (!mRequestFocusFlag) return;
        mAutoCompleteTextView.requestFocus();
    }

    private void initAddAutoCompleteTextView() {
        mAutoCompleteParams = new LayoutParams(LayoutParams.MATCH_PARENT, dpToPx(getContext(), 20.33f));
        mAutoCompleteParams.leftMargin = tagMargin;
        mAutoCompleteParams.rightMargin = tagMargin;
        addView(mAutoCompleteTextView, mAutoCompleteParams);
        mAutoCompleteTextView.setFocusable(true);
        if (!mRequestFocusFlag) return;
        mAutoCompleteTextView.requestFocus();
    }

    @SuppressLint("NewApi")
    private void dynamicChangeAutoCompleteTextViewToBelow() {
        if (mAutoCompleteParams.getRule(RelativeLayout.BELOW) != 0 || isFirstInput) {
            return;
        }
        mAutoCompleteParams.leftMargin = tagMargin;
        mAutoCompleteParams.addRule(RelativeLayout.BELOW, mAutoCompleteParams.getRule(RelativeLayout.RIGHT_OF));
        mAutoCompleteParams.removeRule(RelativeLayout.ALIGN_TOP);
        mAutoCompleteParams.removeRule(RelativeLayout.ALIGN_BOTTOM);
        mAutoCompleteParams.removeRule(RelativeLayout.RIGHT_OF);
        requestLayout();
    }

    private Drawable getSelector(EmailTag tag) {
        if (tag.background != null) return tag.background;
        StateListDrawable states = new StateListDrawable();
        GradientDrawable gd_normal = new GradientDrawable();
        gd_normal.setColor(tag.layoutColor);
        gd_normal.setCornerRadius(tag.radius);
        if (tag.layoutBorderSize > 0) {
            gd_normal.setStroke(dpToPx(getContext(), tag.layoutBorderSize), tag.layoutBorderColor);
        }
        GradientDrawable gd_press = new GradientDrawable();
        gd_press.setColor(tag.layoutColorPress);
        gd_press.setCornerRadius(tag.radius);
        states.addState(new int[]{android.R.attr.state_pressed}, gd_press);
        //must add state_pressed first，or state_pressed will not take effect
        states.addState(new int[]{}, gd_normal);
        return states;
    }

    public void refresh() {
        drawTags();
    }

    public void addTagNoRefresh(EmailTag tag) {
        mTags.add(tag);
    }

    public void addTag(EmailTag tag) {
        mTags.add(tag);
        drawTags();
        mAutoCompleteTextView.refreshBeforeChangedText();
    }

    public void addTags(String[] tags) {
        if (tags == null) return;
        for (String item : tags) {
            EmailTag tag = new EmailTag(item);
            addTag(tag);
        }
    }

    /**
     * get TAG list
     * @return mTags TagObject List
     */
    public List<EmailTag> getTags() {
        return mTags;
    }

    /**
     * remove TAG
     * @param position
     */
    public void remove(int position) {
        mTags.remove(position);
        drawTags();
    }

    public void removeNoRefresh(int position) {
        mTags.remove(position);
    }

    public void removeAllTags() {
        mTags.clear();
        drawTags();
    }

    public void setTagListener(TagListener tagListener) {
        mTagListener = tagListener;
    }

    public EmailAutoCompleteTextView getMailAutoCompleteTextView() {
        return mAutoCompleteTextView;
    }

    public void clear() {
        mAutoCompleteTextView.setText("");
        removeAllTags();
        mTagListener.onTagAllDeleted();
    }

    public void destroy() {
        if (mAutoCompleteTextView == null) {
            return;
        }
        mAutoCompleteTextView.destroy();
    }

}
