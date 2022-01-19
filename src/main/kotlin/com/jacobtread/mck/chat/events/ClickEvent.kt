package com.jacobtread.mck.chat.events

data class ClickEvent(val action: Action, val value: String?) {
    enum class Action(val actionName: String, val allowedInChat: Boolean) {
        OPEN_URL("open_url", true),
        OPEN_FILE("open_file", false),
        RUN_COMMAND("run_command", true),
        SUGGEST_COMMAND("suggest_command", true),
        CHANGE_PAGE("change_page", true);

        companion object {
            fun byName(name: String): Action? = when (name) {
                "open_url" -> OPEN_URL
                "open_file" -> OPEN_FILE
                "run_command" -> RUN_COMMAND
                "suggest_command" -> SUGGEST_COMMAND
                "change_page" -> CHANGE_PAGE
                else -> null
            }
        }
    }
}