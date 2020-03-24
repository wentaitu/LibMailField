package com.cvte.maxhub.mailfield;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

@SuppressLint("AppCompatCustomView")
public class EmailAutoCompleteTextView extends AutoCompleteTextView {
    public static final String[] MAIL_SUFFIXS = new String[] {"@cvte.com", "@qq.com", "@163.com", "@gmail.com"};
    public static final boolean NEED_SHOW_AUTO_COMPLETE = false;

    private static final String TAG = EmailAutoCompleteTextView.class.getSimpleName();

    private static final int DELAY_MILLIS = 200;

    private OnAutoCompleteClickListener mListener;
    private OnAutoCompleteTextLengthListener mLengthListener;

    private EmailAutoCompleteAdapter mEmailAutoAdapt;

    private String mTextValue = "";
    private String mBeforeChangedText = "";
    private String mRecipientLimit;
    private int mRecipientLimitColor;

    public interface OnAutoCompleteClickListener {
        void onItemClick(String email);
        void onDelete();
    }

    public interface OnAutoCompleteTextLengthListener {
        void onLengthChange(float length);
    }

    public EmailAutoCompleteTextView(Context context) {
        super(context);
        init(context);
    }


    public EmailAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public EmailAutoCompleteTextView(Context context, AttributeSet attrs,
                                     int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public void setRecipientLimit(String recipientLimit, int recipientLimitColor) {
        this.mRecipientLimit = recipientLimit;
        this.mRecipientLimitColor = recipientLimitColor;
    }

    public String getRecipientLimit() {
        return mRecipientLimit;
    }

    public void setAutoCompleteItemClickListener(OnAutoCompleteClickListener listener) {
        mListener = listener;
    }

    public void setAutoCompleteTextLengthListener(OnAutoCompleteTextLengthListener listener) {
        mLengthListener = listener;
    }

    public void refreshBeforeChangedText() {
        postDelayed(mRefreshTextRunnable, DELAY_MILLIS);
    }

    public void destroy() {
        removeCallbacks(mRefreshTextRunnable);
        removeTextChangedListener(mTextWatcher);
    }

    private void init(final Context context) {
        this.setListSelection(0);
        mEmailAutoAdapt = new EmailAutoCompleteAdapter(context, R.layout.email_autocompletetextview_item,
                MAIL_SUFFIXS);
        this.setAdapter(mEmailAutoAdapt);
        if (NEED_SHOW_AUTO_COMPLETE) {
            this.setThreshold(1);
        } else {
            this.setThreshold(Integer.MAX_VALUE);
        }

        this.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                enterKeyPressGenerateTag(keyCode, event);
                delete(keyCode, event);
                return false;
            }
        });
        this.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mListener != null) {
                    itemClick(EmailAutoCompleteTextView.this.getText().toString());
                    EmailAutoCompleteTextView.this.setText("");
                    mTextValue = "";
                }
            }
        });
        this.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // 空字符不处理
                if (TextUtils.isEmpty(getText())) {
                    return true;
                }
                // 只处理up事件，避免添加多次
                if (event != null && event.getAction() != KeyEvent.ACTION_UP) {
                    return true;
                }
                if (mListener != null) {
                    if (NEED_SHOW_AUTO_COMPLETE) {
                        itemClick(mTextValue);
                        EmailAutoCompleteTextView.this.setText("");
                    } else {
                        String mail = getText().toString();
                        if (!TextUtils.isEmpty(mRecipientLimit) && !mail.contains(mRecipientLimit)) {
                            mail = mail + mRecipientLimit;
                        }
                        EmailAutoCompleteTextView.this.setText("");
                        itemClick(mail);
                    }

                    EmailAutoCompleteTextView.this.setFocusable(true);
                    EmailAutoCompleteTextView.this.requestFocus();
                    mTextValue = "";
                }
                return true;
            }
        });
    }

    private void itemClick(String text) {
        if (mListener == null) {
            return;
        }
        mListener.onItemClick(text);
        refreshBeforeChangedText();
    }

    /**
     * 物理键盘处理
     * 小键盘Enter键KeyEvent.KEYCODE_NUMPAD_ENTER生成Tag单独处理
     */
    private void enterKeyPressGenerateTag(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER || keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
            // 只处理up事件，避免添加多次
            if (event.getAction() != KeyEvent.ACTION_UP) {
                return;
            }
            if (TextUtils.isEmpty(getText())) {
                return;
            }
            if (mListener != null) {
                if (NEED_SHOW_AUTO_COMPLETE) {
                    itemClick(mTextValue);
                    EmailAutoCompleteTextView.this.setText("");
                } else {
                    String mail = getText().toString();
                    if (keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) {
                        mail = mail.replace("\n", "");
                    }
                    if (!TextUtils.isEmpty(mRecipientLimit) && !mail.contains(mRecipientLimit)) {
                        mail = mail + mRecipientLimit;
                    }
                    EmailAutoCompleteTextView.this.setText("");
                    itemClick(mail);
                }
                EmailAutoCompleteTextView.this.setFocusable(true);
                EmailAutoCompleteTextView.this.requestFocus();
                mTextValue = "";
            }
        }
    }

    private void delete(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_UP) {
            Log.i(TAG, "delete beforeChangedText is "+mBeforeChangedText);
            if (mListener != null && TextUtils.isEmpty(mBeforeChangedText)) {
                mListener.onDelete();
            }
            mBeforeChangedText = getText().toString();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        CharSequence hint = getHint();
        if (hint != null && hint.length() > 0) {
            return;
        }

        if (TextUtils.isEmpty(mRecipientLimit)) {
            return;
        }

        Paint paint = getPaint();
        int oldColor = paint.getColor();
        paint.setColor(mRecipientLimitColor);
        float width = paint.measureText(getText().toString());
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float y = getHeight() / 2 + (Math.abs(fontMetrics.ascent) - fontMetrics.descent) / 2;
        canvas.drawText(mRecipientLimit, getPaddingLeft() + width, y, paint);
        paint.setColor(oldColor);
    }

    @Override
    protected void replaceText(CharSequence text) {
        String t = this.getText().toString();
        int index = t.indexOf('@');
        if (index != -1) {
            t = t.substring(0, index);
        }
        super.replaceText(t + text);
    }


    @Override
    protected void performFiltering(CharSequence text, int keyCode) {
        //该方法会在用户输入文本之后调用，将已输入的文本与adapter中的数据对比，若它匹配
        //adapter中数据的前半部分，那么adapter中的这条数据将会在下拉框中出现
        String t = text.toString();
        mTextValue = t;
        //因为用户输入邮箱时，都是以字母，数字开始，而我们的adapter中只会提供以类似于"@163.com"
        //的邮箱后缀，因此在调用super.performFiltering时，传入的一定是以"@"开头的字符串
        int index = t.indexOf('@');
        if (index == -1) {
            if (t.matches("^[a-zA-Z0-9_]+$")) {
                super.performFiltering("@", keyCode);
            } else {
                this.dismissDropDown();//当用户中途输入非法字符时，关闭下拉提示框
            }
        } else {
            super.performFiltering(t.substring(index), keyCode);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.addTextChangedListener(mTextWatcher);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        destroy();
    }

    private Runnable mRefreshTextRunnable = new Runnable() {
        @Override
        public void run() {
            mBeforeChangedText = getText().toString();
            Log.i(TAG, "RefreshTextRunnable mBeforeChangedText is " + mBeforeChangedText);
        }
    };

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            mBeforeChangedText = s.toString();
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.toString().contains(" ")) {
                String[] str = s.toString().split(" ");
                StringBuilder str1 = new StringBuilder();
                for (int i = 0; i < str.length; i++) {
                    str1.append(str[i]);
                }
                EmailAutoCompleteTextView.this.setText(str1.toString());
                EmailAutoCompleteTextView.this.setSelection(start);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (mLengthListener != null) {
                mLengthListener.onLengthChange(getPaint().measureText(s.toString() + mRecipientLimit));
            }
        }
    };

    private class EmailAutoCompleteAdapter extends ArrayAdapter<String> {

        public EmailAutoCompleteAdapter(Context context, int layoutId, String[] pEmails) {
            super(context, layoutId, pEmails);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(getContext()).inflate(
                        R.layout.email_autocompletetextview_item, null);
                holder.mEmailTextView = convertView.findViewById(R.id.tv);
                holder.mEmailTextView.setTextColor(MailFieldConfig.AutoCompleteTextView_ITEM_TEXT_COLOR);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            String t = EmailAutoCompleteTextView.this.getText().toString();
            int index = t.indexOf('@');
            if (index != -1) {
                t = t.substring(0, index);
            }
            String str = t + getItem(position);
            holder.mEmailTextView.setText(str);

            if (position == 0) {
                holder.mEmailTextView.setBackgroundColor(MailFieldConfig.AutoCompleteTextView_ITEM_BG_COLOR);
                mTextValue = str;
            }
            return convertView;
        }

        class ViewHolder {
            TextView mEmailTextView;
        }

    }

}
