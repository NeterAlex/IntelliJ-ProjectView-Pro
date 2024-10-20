package com.github.neteralex.intellijprojectviewpro.model

import com.github.neteralex.intellijprojectviewpro.constant.PluginConstants
import com.github.neteralex.intellijprojectviewpro.converter.ColorConverter
import com.intellij.util.xmlb.annotations.OptionTag
import java.awt.Color

data class CompressRule(
    var name: String = PluginConstants.COMPRESS_DEFAULT_RULE_NAME,
    var pattern: String = PluginConstants.COMPRESS_DEFAULT_RULE_PATTERN,
    @get:OptionTag(converter = ColorConverter::class)
    var itemBackground: Color? = null,
    @get:OptionTag(converter = ColorConverter::class)
    var itemForeground: Color? = null
)
