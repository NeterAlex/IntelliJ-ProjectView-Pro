package com.github.neteralex.intellijprojectviewpro.state

import com.github.neteralex.intellijprojectviewpro.model.CompressRule

interface PluginState {
    val isCompressingEnabled: Boolean
    val isHidingEmptyGroup: Boolean
    val isHidingAllGroups: Boolean
    val isNameCaseSensitive: Boolean
    val compressRules: MutableList<CompressRule>
}