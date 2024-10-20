package com.github.neteralex.intellijprojectviewpro.converter

import com.intellij.ui.JBColor
import com.intellij.util.xmlb.Converter
import java.awt.Color

class ColorConverter : Converter<Color>() {
    override fun fromString(p0: String): Color? {
        return runCatching { JBColor.decode(p0) }.getOrNull()
    }

    override fun toString(p0: Color): String? {
        return p0.rgb.toString()
    }
}