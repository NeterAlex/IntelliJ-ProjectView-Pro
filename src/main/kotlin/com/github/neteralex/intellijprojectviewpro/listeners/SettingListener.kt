package com.github.neteralex.intellijprojectviewpro.listeners

import com.github.neteralex.intellijprojectviewpro.services.SettingService
import com.intellij.util.messages.Topic
import java.util.EventListener

@FunctionalInterface
fun interface SettingListener : EventListener {
    companion object {
        @Topic.ProjectLevel
        val TOPIC = Topic(SettingListener::class.java)
    }

    fun settingsChanged(settings: SettingService)
}
