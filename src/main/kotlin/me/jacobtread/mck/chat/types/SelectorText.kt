package me.jacobtread.mck.chat.types

import me.jacobtread.mck.chat.Text
import me.jacobtread.mck.chat.TextBase

data class SelectorText(val selector: String) : TextBase() {

    override fun asString(): String = selector

    override fun copy(): Text {
        val component = SelectorText(selector)
        component.chatStyle = chatStyle.copy()
        siblings.forEach { component.append(it.copy()) }
        return component
    }
}