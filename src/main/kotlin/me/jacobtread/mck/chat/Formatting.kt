package me.jacobtread.mck.chat

import java.util.regex.Pattern

enum class Formatting(
    val formatName: String,
    val code: Char,
    val index: Int = -1,
    val fancyStyling: Boolean = false
) {
    BLACK("BLACK", '0', 0),
    DARK_BLUE("DARK_BLUE", '1', 1),
    DARK_GREEN("DARK_GREEN", '2', 2),
    DARK_AQUA("DARK_AQUA", '3', 3),
    DARK_RED("DARK_RED", '4', 4),
    DARK_PURPLE("DARK_PURPLE", '5', 5),
    GOLD("GOLD", '6', 6),
    GRAY("GRAY", '7', 7),
    DARK_GRAY("DARK_GRAY", '8', 8),
    BLUE("BLUE", '9', 9),
    GREEN("GREEN", 'a', 10),
    AQUA("AQUA", 'b', 11),
    RED("RED", 'c', 12),
    LIGHT_PURPLE("LIGHT_PURPLE", 'd', 13),
    YELLOW("YELLOW", 'e', 14),
    WHITE("WHITE", 'f', 15),
    OBFUSCATED("OBFUSCATED", 'k', -1, true),
    BOLD("BOLD", 'l', -1, true),
    STRIKETHROUGH("STRIKETHROUGH", 'm', -1, true),
    UNDERLINE("UNDERLINE", 'n', -1, true),
    ITALIC("ITALIC", 'o', -1, true),
    RESET("RESET", 'r', -1);

    val controlString = "\u00a7" + code
    val friendlyName = name.lowercase()
    val isColor = !fancyStyling && this.index != -1

    override fun toString(): String {
        return controlString
    }

    operator fun plus(value: String): String {
        return controlString + value
    }

    operator fun plus(value: Formatting): String {
        return controlString + value.controlString
    }


    companion object {
        const val FORMAT_CODE = '\u00a7'
        private val FORMATTING_CODE_PATTERN: Pattern = Pattern.compile("(?i)" + '\u00a7' + "[0-9A-FK-OR]")
        private val NAME_MAPPING = HashMap<String, Formatting>()

        init {
            for (value in values()) {
                NAME_MAPPING[getSafeName(value.formatName)] = value
            }
        }

        @JvmStatic
        fun getValueByName(friendlyName: String?): Formatting {
            return if (friendlyName == null) RESET else NAME_MAPPING[getSafeName(friendlyName)] ?: RESET
        }


        @JvmStatic
        fun getValidValues(allowColor: Boolean, allowFancy: Boolean): Collection<String> {
            val list: MutableList<String> = ArrayList()
            for (chatFormatting in values()) {
                if ((!chatFormatting.isColor || allowColor) && (!chatFormatting.fancyStyling || allowFancy)) {
                    list.add(chatFormatting.friendlyName)
                }
            }
            return list
        }


        private fun getSafeName(name: String): String {
            return name.lowercase().replace(Regex("[^a-z]"), "")
        }

        fun getColorToSettings(chatColor: Boolean, text: String, colors: Boolean): String? {
            return if (!colors && !chatColor) {
                getTextWithoutFormat(text)
            } else text
        }

        @JvmStatic
        fun byColorIndex(colorIndex: Int): Formatting {
            return if (colorIndex != -1) {
                for (chatFormatting in values()) {
                    if (chatFormatting.index == colorIndex) {
                        return chatFormatting
                    }
                }
                RESET
            } else RESET
        }

        @JvmStatic
        fun getTextWithoutFormat(text: String?): String? {
            text ?: return null
            return FORMATTING_CODE_PATTERN.matcher(text).replaceAll("")
        }

        fun isCharColor(char: Char): Boolean {
            return char.code in 48..57 || char.code in 97..102 || char.code in 65..70
        }

        fun isCharSpecial(char: Char): Boolean {
            return char.code in 107..111 || char.code in 75..79 || char.code == 114 || char.code == 82
        }

        fun getFormat(text: String): String {
            val output = StringBuilder()
            var i = -1
            val length = text.length
            while (true) {
                i = text.indexOf(FORMAT_CODE, i + 1)
                if (i == -1) break
                if (i < length - 1) {
                    val char = text[i + 1]
                    if (isCharColor(char)) {
                        output.clear()
                        output.append(FORMAT_CODE).append(char)
                    } else if (isCharSpecial(char)) {
                        output.append(FORMAT_CODE).append(char)
                    }
                }
            }
            return output.toString()
        }

    }

}

fun String.bold(): String {
    return Formatting.BOLD + this
}

fun String.italic(): String {
    return Formatting.ITALIC + this
}

fun String.white(): String {
    return Formatting.WHITE + this
}

fun String.green(): String {
    return Formatting.GREEN + this
}

fun String.red(): String {
    return Formatting.RED + this
}

fun String.darkRed(): String {
    return Formatting.DARK_RED + this
}

fun String.gray(): String {
    return Formatting.GRAY + this
}

fun String.black(): String {
    return Formatting.BLACK + this
}

fun String.blue(): String {
    return Formatting.BLUE + this
}

fun String.yellow(): String {
    return Formatting.YELLOW + this
}

fun String.darkGray(): String {
    return Formatting.DARK_GRAY + this
}