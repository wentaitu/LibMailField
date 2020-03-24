package com.cvte.maxhub.mailfield;

import android.graphics.Color;

public class MailFieldConfig {
    //use dp and sp, not px

    /*----------------- separator TagView -----------------*/
    public static float DEFAULT_LINE_MARGIN = 2.67f;
    public static float DEFAULT_TAG_MARGIN = 2.67f;
    public static float DEFAULT_TAG_TEXT_PADDING_LEFT = 4;
    public static float DEFAULT_TAG_TEXT_PADDING_TOP = 4;
    public static float DEFAULT_TAG_TEXT_PADDING_RIGHT = 4;
    public static float DEFAULT_TAG_TEXT_PADDING_BOTTOM = 4;
    public static int TAG_ON_FOCUS_CHANGE_SCROLLVIEW_BG_RES_ID = R.drawable.email_scrollview_focus_back;
    public static int TAG_ON_OUT_FOCUS_CHANGE_SCROLLVIEW_BG_RES_ID = R.drawable.email_scrollview_not_focus_back;

    /*----------------- separator Tag Item -----------------*/
    public static float DEFAULT_TAG_TEXT_SIZE = 10f;
    public static float DEFAULT_TAG_DELETE_INDICATOR_SIZE = 12f;
    public static float DEFAULT_TAG_LAYOUT_BORDER_SIZE = 0f;
    public static float DEFAULT_TAG_RADIUS = 3;


    public static int DEFAULT_TAG_LAYOUT_BORDER_COLOR = Color.parseColor("#ffffff");
    public static String DEFAULT_TAG_DELETE_ICON = "";
    public static boolean DEFAULT_TAG_IS_DELETABLE = true;

    public static int TAG_ADDRESS_SUFFIX_LIMITATION_TEXT_COLOR = Color.parseColor("#BBBBBB");

    public static int DEFAULT_TAG_LAYOUT_COLOR = Color.parseColor("#EEEEEE");
    public static int DEFAULT_TAG_LAYOUT_COLOR_PRESS = Color.parseColor("#EEEEEE");
    public static int DEFAULT_TAG_TEXT_COLOR = Color.BLACK;
    public static int DEFAULT_TAG_DELETE_BG_RES_ID = R.drawable.email_delete_success_selector;
    public static int DEFAULT_TAG_DELETE_INDICATOR_COLOR = Color.parseColor("#000000");

    public static int TAG_WRONG_ADDRESS_LAYOUT_COLOR = Color.parseColor("#FF5555");
    public static int TAG_WRONG_ADDRESS_LAYOUT_COLOR_PRESS = Color.parseColor("#FF5555");
    public static int TAG_WRONG_ADDRESS_TEXT_COLOR = Color.WHITE;
    public static int TAG_WRONG_DELETE_BG_RES_ID = R.drawable.email_delete_error_selector;
    public static int TAG_WRONG_DELETE_INDICATOR_COLOR = Color.parseColor("#000000");

    /* ------------------ separator for other---------------*/
    public static int AutoCompleteTextView_ITEM_BG_COLOR = Color.parseColor("#189bf0");
    public static int AutoCompleteTextView_ITEM_TEXT_COLOR = Color.parseColor("#898989");
    public static float AutoCompleteTextView_TEXT_SIZE = DEFAULT_TAG_TEXT_SIZE;
    public static int AutoCompleteTextView_TEXT_COLOR = Color.parseColor("#262626");

    public static String[] MAIL_SUFFIXS = new String[] {"@cvte.com", "@qq.com", "@163.com", "@gmail.com"};
    public static float TAG_LINE_FEED_DISTANCE = 20f;

}
