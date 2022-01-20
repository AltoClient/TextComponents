package com.jacobtread.mck.chat

object AllowedChars {

    val SAVE_NOT_ALLOWED_CHARS = charArrayOf(
        '/', '\n', '\r', '\t', '\u0000',
        '\u000c', '`', '?', '*', '\\',
        '<', '>', '|', '\"', ':'
    )

    fun isAllowed(char: Char): Boolean {
        return char.code != 167 && char.code >= 32 && char.code != 127
    }

    @JvmStatic
    fun filter(value: String): String {
        return value.filter { isAllowed(it) }
    }
}