package me.jacobtread.mck.chat.types

import me.jacobtread.mck.chat.Text
import me.jacobtread.mck.chat.TextBase

data class LiteralText(val text: String) : TextBase() {

    override fun asString(): String = text

    override fun copy(): Text {
        val component = LiteralText(text)
        component.chatStyle = chatStyle.copy()
        siblings.forEach { component.append(it.copy()) }
        return component
    }
}