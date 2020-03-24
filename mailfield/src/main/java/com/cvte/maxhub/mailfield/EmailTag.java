package com.cvte.maxhub.mailfield;

import android.graphics.Color;
import android.graphics.drawable.Drawable;

/**
 * Email Tag Entity
 */
public class EmailTag {

    public int id;
    public String text;
    public int tagTextColor;
    public float tagTextSize;
    public int layoutColor;
    public int layoutColorPress;
    public boolean isDeletable;
    public int deleteIndicatorColor;
    public float deleteIndicatorSize;
    public float radius;
    public String deleteIcon;
    public float layoutBorderSize;
    public int layoutBorderColor;
    public Drawable background;
    public boolean isEmailOk = true;
    public int deleteBgResId;

    public EmailTag(String text) {
        init(0, text, MailFieldConfig.DEFAULT_TAG_TEXT_SIZE, MailFieldConfig.DEFAULT_TAG_LAYOUT_COLOR, MailFieldConfig.DEFAULT_TAG_LAYOUT_COLOR_PRESS,
                MailFieldConfig.DEFAULT_TAG_IS_DELETABLE, MailFieldConfig.DEFAULT_TAG_DELETE_INDICATOR_COLOR, MailFieldConfig.DEFAULT_TAG_DELETE_INDICATOR_SIZE, MailFieldConfig.DEFAULT_TAG_RADIUS, MailFieldConfig.DEFAULT_TAG_DELETE_ICON, MailFieldConfig.DEFAULT_TAG_LAYOUT_BORDER_SIZE, MailFieldConfig.DEFAULT_TAG_LAYOUT_BORDER_COLOR, MailFieldConfig.DEFAULT_TAG_DELETE_BG_RES_ID);
    }

    public EmailTag(String text, int color) {
        init(0, text, MailFieldConfig.DEFAULT_TAG_TEXT_SIZE, color, MailFieldConfig.DEFAULT_TAG_LAYOUT_COLOR_PRESS, MailFieldConfig.DEFAULT_TAG_IS_DELETABLE,
                MailFieldConfig.DEFAULT_TAG_DELETE_INDICATOR_COLOR, MailFieldConfig.DEFAULT_TAG_DELETE_INDICATOR_SIZE, MailFieldConfig.DEFAULT_TAG_RADIUS, MailFieldConfig.DEFAULT_TAG_DELETE_ICON, MailFieldConfig.DEFAULT_TAG_LAYOUT_BORDER_SIZE, MailFieldConfig.DEFAULT_TAG_LAYOUT_BORDER_COLOR, MailFieldConfig.DEFAULT_TAG_DELETE_BG_RES_ID);
    }

    public EmailTag(String text, String color) {
        init(0, text, MailFieldConfig.DEFAULT_TAG_TEXT_SIZE, Color.parseColor(color), MailFieldConfig.DEFAULT_TAG_LAYOUT_COLOR_PRESS,
                MailFieldConfig.DEFAULT_TAG_IS_DELETABLE, MailFieldConfig.DEFAULT_TAG_DELETE_INDICATOR_COLOR, MailFieldConfig.DEFAULT_TAG_DELETE_INDICATOR_SIZE, MailFieldConfig.DEFAULT_TAG_RADIUS, MailFieldConfig.DEFAULT_TAG_DELETE_ICON, MailFieldConfig.DEFAULT_TAG_LAYOUT_BORDER_SIZE, MailFieldConfig.DEFAULT_TAG_LAYOUT_BORDER_COLOR, MailFieldConfig.DEFAULT_TAG_DELETE_BG_RES_ID);
    }

    public EmailTag(int id, String text) {
        init(id, text, MailFieldConfig.DEFAULT_TAG_TEXT_SIZE, MailFieldConfig.DEFAULT_TAG_LAYOUT_COLOR, MailFieldConfig.DEFAULT_TAG_LAYOUT_COLOR_PRESS,
                MailFieldConfig.DEFAULT_TAG_IS_DELETABLE, MailFieldConfig.DEFAULT_TAG_DELETE_INDICATOR_COLOR, MailFieldConfig.DEFAULT_TAG_DELETE_INDICATOR_SIZE, MailFieldConfig.DEFAULT_TAG_RADIUS, MailFieldConfig.DEFAULT_TAG_DELETE_ICON, MailFieldConfig.DEFAULT_TAG_LAYOUT_BORDER_SIZE, MailFieldConfig.DEFAULT_TAG_LAYOUT_BORDER_COLOR, MailFieldConfig.DEFAULT_TAG_DELETE_BG_RES_ID);
    }

    public EmailTag(int id, String text, int color) {
        init(id, text, MailFieldConfig.DEFAULT_TAG_TEXT_SIZE, color, MailFieldConfig.DEFAULT_TAG_LAYOUT_COLOR_PRESS, MailFieldConfig.DEFAULT_TAG_IS_DELETABLE,
                MailFieldConfig.DEFAULT_TAG_DELETE_INDICATOR_COLOR, MailFieldConfig.DEFAULT_TAG_DELETE_INDICATOR_SIZE, MailFieldConfig.DEFAULT_TAG_RADIUS, MailFieldConfig.DEFAULT_TAG_DELETE_ICON, MailFieldConfig.DEFAULT_TAG_LAYOUT_BORDER_SIZE, MailFieldConfig.DEFAULT_TAG_LAYOUT_BORDER_COLOR, MailFieldConfig.DEFAULT_TAG_DELETE_BG_RES_ID);
    }

    public EmailTag(int id, String text, String color) {
        init(id, text, MailFieldConfig.DEFAULT_TAG_TEXT_SIZE, Color.parseColor(color), MailFieldConfig.DEFAULT_TAG_LAYOUT_COLOR_PRESS,
                MailFieldConfig.DEFAULT_TAG_IS_DELETABLE, MailFieldConfig.DEFAULT_TAG_DELETE_INDICATOR_COLOR, MailFieldConfig.DEFAULT_TAG_DELETE_INDICATOR_SIZE, MailFieldConfig.DEFAULT_TAG_RADIUS, MailFieldConfig.DEFAULT_TAG_DELETE_ICON, MailFieldConfig.DEFAULT_TAG_LAYOUT_BORDER_SIZE, MailFieldConfig.DEFAULT_TAG_LAYOUT_BORDER_COLOR, MailFieldConfig.DEFAULT_TAG_DELETE_BG_RES_ID);
    }

    private void init(int id, String text, float tagTextSize, int layout_color, int layout_color_press, boolean isDeletable, int deleteIndicatorColor,
                      float deleteIndicatorSize, float radius, String deleteIcon, float layoutBorderSize, int layoutBorderColor, int deleteBgResId) {
        this.id = id;
        this.text = text;
        this.tagTextColor = MailFieldConfig.DEFAULT_TAG_TEXT_COLOR;
        this.tagTextSize = tagTextSize;
        this.layoutColor = layout_color;
        this.layoutColorPress = layout_color_press;
        this.isDeletable = isDeletable;
        this.deleteIndicatorColor = deleteIndicatorColor;
        this.deleteIndicatorSize = deleteIndicatorSize;
        this.radius = radius;
        this.deleteIcon = deleteIcon;
        this.layoutBorderSize = layoutBorderSize;
        this.layoutBorderColor = layoutBorderColor;
        this.deleteBgResId = deleteBgResId;
    }

}
