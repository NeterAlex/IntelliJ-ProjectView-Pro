package com.github.neteralex.intellijprojectviewpro.model

import com.github.neteralex.intellijprojectviewpro.constant.GlobalConstants
import com.github.neteralex.intellijprojectviewpro.converter.ColorConverter
import com.intellij.util.xmlb.annotations.OptionTag
import java.awt.Color

data class CompressRule(
    var name: String = GlobalConstants.COMPRESS_DEFAULT_RULE_NAME,
    var pattern: String = GlobalConstants.COMPRESS_DEFAULT_RULE_PATTERN,
    @get:OptionTag(converter = ColorConverter::class)
    var itemBackground: Color? = null,
    @get:OptionTag(converter = ColorConverter::class)
    var itemForeground: Color? = null
)
