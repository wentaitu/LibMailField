package com.cvte.maxhub.mailfield.config

import android.graphics.Color
import com.cvte.maxhub.mailfield.R

/**
 * @author tuwentai
 * @email i_tuwentai@cvte.com
 * @date 2020-03-25
 * @description: 邮件输入框默认样式
 */
object MailFieldConfig {

    //use dp and sp, not px
    /*----------------- separator TagView -----------------*/
    var DEFAULT_LINE_MARGIN = 2.67f
    var DEFAULT_TAG_MARGIN = 2.67f
    var DEFAULT_TAG_TEXT_PADDING_LEFT = 4f
    var DEFAULT_TAG_TEXT_PADDING_TOP = 4f
    var DEFAULT_TAG_TEXT_PADDING_RIGHT = 4f
    var DEFAULT_TAG_TEXT_PADDING_BOTTOM = 4f
    var TAG_ON_FOCUS_SCROLLVIEW_BG_RES_ID = R.drawable.email_scrollview_focus_back
    var TAG_ON_OUT_FOCUS_SCROLLVIEW_BG_RES_ID = R.drawable.email_scrollview_not_focus_back


    /*----------------- separator Tag Item -----------------*/
    // 默认地址Tag属性配置
    var DEFAULT_TAG_TEXT_SIZE = 10f
    // 邮件Tag删除按钮是否显示
    var DEFAULT_TAG_IS_DELETABLE = true
    var DEFAULT_TAG_TEXT_COLOR = Color.BLACK
    var DEFAULT_TAG_LAYOUT_BG_RES_ID = R.drawable.email_tag_item_normal_bg_selector
    var DEFAULT_TAG_DELETE_BG_RES_ID = R.drawable.email_delete_right_selector
    var TAG_WRONG_ADDRESS_TEXT_COLOR = Color.WHITE
    var TAG_WRONG_LAYOUT_BG_RES_ID = R.drawable.email_tag_item_abnormal_bg_selector
    var TAG_WRONG_DELETE_BG_RES_ID = R.drawable.email_delete_error_selector


    /* ------------------ separator for other---------------*/
    // 字符输入AutoCompleteTextView属性配置
    var AutoCompleteTextView_DROPDOWN_BG_RES_ID = R.drawable.bg_small_shadow
    var AutoCompleteTextView_ITEM_TEXT_COLOR = Color.parseColor("#898989")
    var AutoCompleteTextView_TEXT_SIZE = DEFAULT_TAG_TEXT_SIZE
    var AutoCompleteTextView_TEXT_COLOR = Color.parseColor("#262626")
    var AutoCompleteTextView_HEIGHT = 21.33f
    // 控制是否需要显示邮件后缀下拉列表
    val AutoCompleteTextView_SHOW_DROPDOWN_LIST = true
    var AutoCompleteTextView_SUFFIX_TEXT_COLOR = Color.parseColor("#BBBBBB")
    // 地址过长默认自动换行距离，若有地址后缀则此长度为后缀长度
    var TAG_LINE_FEED_DISTANCE = 20f
    // MaxHeightScrollView默认最高高度
    var SCROLLVIEW_MAX_HEIGHT = 104f

}