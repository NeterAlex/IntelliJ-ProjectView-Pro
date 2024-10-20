package com.github.neteralex.intellijprojectviewpro.services


import com.github.neteralex.intellijprojectviewpro.constant.GlobalConstants
import com.github.neteralex.intellijprojectviewpro.model.CompressRule
import com.github.neteralex.intellijprojectviewpro.state.GlobalState
import com.intellij.openapi.components.*
import com.intellij.util.xmlb.annotations.OptionTag
import java.io.File

@Service(Service.Level.PROJECT)
@State(name = "ProjectViewProSettings", storages = [Storage(GlobalConstants.CONFIG_FILE_NAME)])
class SettingService() : GlobalState, BaseState(), PersistentStateComponent<SettingService> {
    @get:OptionTag("IS_COMPRESSING_ENABLED")
    override var isCompressingEnabled by property(true)

    @get:OptionTag("IS_HIDING_EMPTY_GROUP")
    override var isHidingEmptyGroup by property(true)

    @get:OptionTag("IS_HIDING_ALL_GROUPS")
    override var isHidingAllGroups by property(false)

    @get:OptionTag("IS_NAME_CASE_SENSITIVE")
    override var isNameCaseSensitive by property(true)

    @get:OptionTag("COMPRESS_RULES")
    override var compressRules by list<CompressRule>()

    init {
        val tempCompressRules = mutableListOf<CompressRule>()
        tempCompressRules.add(CompressRule(".gitignore", analyseGitIgnorePatterns().joinToString(" "), null, null))
    }

    private fun analyseGitIgnorePatterns(): List<String> {
        val currentPath = System.getProperty("user.dir")
        val ignoreFile = File("$currentPath/.gitignore")
        return if (ignoreFile.exists()) ignoreFile.readLines()
            .filter { it.isNotBlank() && !it.startsWith("#") }
        else emptyList()
    }

    override fun getState() = this

    override fun loadState(state: SettingService) = copyFrom(state)
}