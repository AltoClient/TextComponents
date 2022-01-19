package com.jacobtread.mck.chat

object AllowedChars {

    val SAVE_NOT_ALLOWED_CHARS = charArrayOf(
        '/', '\n', '\r', '\t', '\u0000',
        '\u000c', '`', '?', '*', '\\',
        '<', '>', '|', '\"', ':'
    )

    fun isAllowed(char: Char): Boolean {
        return char != com.jacobtread.mck.chat.Formatting.Companion.FORMAT_CODE
                && char.code > 32 && char.code != 127 // Delete char
    }

    @JvmStatic
    fun filter(value: String): String {
        return value.filter { com.jacobtread.mck.chat.AllowedChars.isAllowed(it) }
    }
}