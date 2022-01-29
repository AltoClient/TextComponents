package com.jacobtread.alto.chat.events

import com.jacobtread.alto.chat.Text

data class HoverEvent(val action: Action, val value: Text) {
    enum class Action(val actionName: String) {
        SHOW_TEXT("show_text"),
        SHOW_ACHIEVEMENT("show_achievement"),
        SHOW_ITEM("show_item"),
        SHOW_ENTITY("show_entity");

        companion object {
            fun byName(name: String): Action? = when (name) {
                "show_text" -> SHOW_TEXT
                "show_achievement" -> SHOW_ACHIEVEMENT
                "show_item" -> SHOW_ITEM
                "show_entity" -> SHOW_ENTITY
                else -> null
            }
        }
    }
}