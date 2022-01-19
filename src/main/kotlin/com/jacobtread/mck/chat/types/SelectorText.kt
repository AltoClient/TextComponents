package com.jacobtread.mck.chat.types

import com.jacobtread.mck.chat.Text
import com.jacobtread.mck.chat.TextBase

data class SelectorText(val selector: String) : TextBase() {

    override fun asString(): String = selector

    override fun copy(): Text {
        val component = SelectorText(selector)
        component.chatStyle = chatStyle.copy()
        siblings.forEach { component.append(it.copy()) }
        return component
    }
}