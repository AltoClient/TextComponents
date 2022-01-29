package com.jacobtread.alto.chat.types

import com.jacobtread.alto.chat.Formatting
import com.jacobtread.alto.chat.Text
import com.jacobtread.alto.chat.TextBase

data class LiteralText(val text: String) : TextBase() {

    override fun asString(): String {
        val output = StringBuilder(text)
        siblings.forEach { output.append(it.asString()) }
        return output.toString()
    }

    override fun formatted(): String {
        val output = StringBuilder()
        output.append(chatStyle.asFormatCode())
        output.append(text)
        output.append(Formatting.RESET)
        siblings.forEach {
            val chatStyle = it.chatStyle.withParent(chatStyle)
            output.append(chatStyle.asFormatCode())
            output.append(it.asString())
            output.append(Formatting.RESET)
        }
        return output.toString()
    }

    override fun copy(): Text {
        val component = LiteralText(text)
        component.chatStyle = chatStyle.copy()
        siblings.forEach { component.append(it.copy()) }
        return component
    }
}