package com.cvte.maxhub.mailfield.bean

import android.graphics.drawable.Drawable
import com.cvte.maxhub.mailfield.config.MailFieldConfig

/**
 * @author tuwentai
 * @email i_tuwentai@cvte.com
 * @date 2020-03-25
 * @description: 邮件地址Tag Bean类
 */
data class EmailTag(var text: String) {
    var id: Int = 0
    var tagTextColor = MailFieldConfig.DEFAULT_TAG_TEXT_COLOR
    var tagTextSize = MailFieldConfig.DEFAULT_TAG_TEXT_SIZE
    var layoutColor = MailFieldConfig.DEFAULT_TAG_LAYOUT_COLOR
    var layoutColorPress = MailFieldConfig.DEFAULT_TAG_LAYOUT_COLOR_PRESS
    var isDeletable = MailFieldConfig.DEFAULT_TAG_IS_DELETABLE
    var deleteIndicatorColor = MailFieldConfig.DEFAULT_TAG_DELETE_INDICATOR_COLOR
    var deleteIndicatorSize = MailFieldConfig.DEFAULT_TAG_DELETE_INDICATOR_SIZE
    var radius = MailFieldConfig.DEFAULT_TAG_RADIUS
    var layoutBorderSize = MailFieldConfig.DEFAULT_TAG_LAYOUT_BORDER_SIZE
    var layoutBorderColor = MailFieldConfig.DEFAULT_TAG_LAYOUT_BORDER_COLOR
    var background: Drawable? = null
    var isEmailOk = true
    var deleteBgResId = MailFieldConfig.DEFAULT_TAG_DELETE_BG_RES_ID
}
