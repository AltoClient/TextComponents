package com.jacobtread.mck.chat

import com.jacobtread.mck.chat.events.ClickEvent
import com.jacobtread.mck.chat.events.HoverEvent
import com.jacobtread.mck.chat.types.LiteralText

abstract class TextBase : Text {
    override val siblings: ArrayList<Text> = ArrayList()
    override var chatStyle: ChatStyle = ChatStyle.EMPTY
        set(value) {
            field = value
            siblings.forEach { it.parent(this) }
        }

    override fun withFormat(formatting: Formatting): Text {
        chatStyle = chatStyle.withFormat(formatting)
        return this
    }

    override fun withHover(hoverEvent: HoverEvent): Text {
        chatStyle = chatStyle.withHoverEvent(hoverEvent)
        return this
    }

    override fun withClick(clickEvent: ClickEvent): Text {
        chatStyle = chatStyle.withClickEvent(clickEvent)
        return this
    }

    override fun withInsertion(insertion: String): Text {
        chatStyle = chatStyle.withInsertion(insertion)
        return this
    }

    override fun parent(text: Text) {
        chatStyle = chatStyle.withParent(text.chatStyle)
    }

    override fun append(text: Text): Text {
        text.chatStyle = text.chatStyle.withParent(chatStyle)
        siblings.add(text)
        return this
    }

    override fun append(text: String): Text {
        return append(LiteralText(text))
    }

    override fun iterator(): Iterator<Text> {
        return (listOf(this) + siblings).iterator()
    }

    override fun asString(): String {
        val output = StringBuilder()
        siblings.forEach { output.append(it.asString()) }
        return output.toString()
    }

    override fun formatted(): String {
        val output = StringBuilder()
        output.append(chatStyle.asFormatCode())
        output.append(asString())
        output.append(Formatting.RESET)
        siblings.forEach {
            val chatStyle = it.chatStyle.withParent(chatStyle)
            output.append(chatStyle.asFormatCode())
            output.append(it.asString())
            output.append(Formatting.RESET)
        }
        return output.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TextBase) return false
        return siblings == other.siblings && chatStyle == other.chatStyle
    }

    override fun hashCode(): Int {
        return 31 * chatStyle.hashCode() * siblings.hashCode()
    }
}