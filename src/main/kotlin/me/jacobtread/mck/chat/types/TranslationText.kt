package me.jacobtread.mck.chat.types

import me.jacobtread.mck.chat.ChatStyle
import me.jacobtread.mck.chat.Text
import me.jacobtread.mck.chat.TextBase
import me.jacobtread.mck.utils.providers.TranslationProvider

class TranslationText(val key: String, vararg val formatArgs: Any?) : TextBase() {

    companion object {
        // This is stored as a core part of MC so must be abstracted
        @JvmStatic
        var translationProvider: TranslationProvider? = null
    }

    // Regex pattern to match format args such as %1 %2
    val variableRegex = Regex("%(?:(\\d+)\\$)?([A-Za-z%]|$)").toPattern()
    val children = ArrayList<Text>()
    var lastParsed = -1L

    override var chatStyle: ChatStyle
        get() = super.chatStyle
        set(value) {
            super.chatStyle = value
            formatArgs.forEach {
                if (it is Text) {
                    it.parent(this)
                }
            }
            children.forEach { it.parent(this) }
        }

    /**
     * If any of the provided args are text components
     * make sure to parent their chat styles
     */
    init {
        formatArgs.forEach {
            if (it is Text) {
                it.parent(this)
            }
        }
    }

    @Synchronized
    fun ensureParsed() {
        val translationProvider = translationProvider
            ?: throw IllegalStateException("No translation provider initialized for translating text!!")
        val time = System.currentTimeMillis()
        if (time == lastParsed) return
        lastParsed = time
        children.clear()
        try {
            // Parse the format provided
            parse(translationProvider.translate(key))
        } catch (e: IllegalArgumentException) {
            // Clear the children from the failed parse
            children.clear()
            // This second one is not caught for exceptions so that its raised
            parse(translationProvider.translate(key))
        }
    }

    override fun asString(): String {
        ensureParsed()
        val output = StringBuilder()
        children.forEach { output.append(it.asString()) }
        return output.toString()
    }

    override fun iterator(): Iterator<Text> {
        ensureParsed()
        return (children + siblings).iterator()
    }

    fun parse(format: String) {
        var variableIndex = 0
        val matcher = variableRegex.matcher(format)
        var lastEnd = 0
        while (true) {
            val match = matcher.find(lastEnd)
            if (!match) break
            val start = matcher.start()
            val end = matcher.end()
            if (start > lastEnd) {
                val textBefore = LiteralText(format.substring(lastEnd, start))
                textBefore.parent(this)
                children.add(textBefore)
            }
            val formatValue = matcher.group(2)
            val afterText = format.substring(start, end)
            if (formatValue == "%" || afterText == "%%") {
                val text = LiteralText("%")
                text.parent(this)
                children.add(text)
            } else if (formatValue == "s") {
                val formatIndexRaw = matcher.group(1)
                val index = if (formatIndexRaw != null) {
                    formatIndexRaw.toInt() - 1
                } else {
                    variableIndex++
                }
                if (index < formatArgs.size) {
                    children.add(getText(index))
                } else {
                    throw IllegalArgumentException("Index out of bounds. only provided ${formatArgs.size} format arguments")
                }
            } else {
                throw IllegalArgumentException("Unsupported format: '$formatValue'")
            }
            lastEnd = end
        }
        if (lastEnd < format.length) {
            val textAfter = LiteralText(format.substring(lastEnd))
            textAfter.parent(this)
            children.add(textAfter)
        }
    }

    private fun getText(index: Int): Text {
        val formatArg = formatArgs[index]
        return if (formatArg is Text) {
            formatArg
        } else {
            val text = LiteralText(formatArg?.toString() ?: "null")
            text.parent(this)
            text
        }
    }

    override fun copy(): Text {
        val args = Array(formatArgs.size) {
            val value = formatArgs[it]
            if (value is Text) value.copy()
            else value
        }
        val copy = TranslationText(key, args)
        copy.chatStyle = chatStyle.copy()
        siblings.forEach { copy.append(it.copy()) }
        return copy
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TranslationText) return false
        return siblings == other.siblings
                && formatArgs.contentEquals(other.formatArgs)
                && children == other.children
                && chatStyle == other.chatStyle
    }

    override fun hashCode(): Int {
        var i = super.hashCode()
        i = 31 * i + key.hashCode()
        i = 31 * i + formatArgs.contentHashCode()
        return i
    }
}