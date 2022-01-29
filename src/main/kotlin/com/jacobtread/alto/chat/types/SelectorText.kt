package com.jacobtread.alto.chat.types

import com.jacobtread.alto.chat.Text
import com.jacobtread.alto.chat.TextBase

data class SelectorText(val selector: String) : TextBase() {

    override fun asString(): String = selector

    override fun copy(): Text {
        val component = SelectorText(selector)
        component.chatStyle = chatStyle.copy()
        siblings.forEach { component.append(it.copy()) }
        return component
    }
}