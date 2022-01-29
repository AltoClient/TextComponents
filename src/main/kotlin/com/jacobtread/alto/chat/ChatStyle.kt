package com.jacobtread.alto.chat

import com.google.gson.*
import com.jacobtread.alto.chat.events.ClickEvent
import com.jacobtread.alto.chat.events.HoverEvent
import java.lang.reflect.Type

data class ChatStyle(
    val color: Formatting?,
    val bold: Boolean?,
    val italic: Boolean?,
    val underlined: Boolean?,
    val strikethrough: Boolean?,
    val obfuscated: Boolean?,
    val clickEvent: ClickEvent?,
    val hoverEvent: HoverEvent?,
    val insertion: String?
) {
    companion object {
        val EMPTY = ChatStyle(null, null, null, null, null, null, null, null, null)
    }



    val isBold: Boolean get() = bold == true
    val isItalic: Boolean get() = italic == true
    val isUnderlined: Boolean get() = underlined == true
    val isObfuscated: Boolean get() = obfuscated == true
    val isStrikethrough: Boolean get() = strikethrough == true

    fun asFormatCode(): String {
        val output = StringBuilder()
        if (color != null) output.append(color)
        if (isBold) output.append(Formatting.BOLD)
        if (isItalic) output.append(Formatting.ITALIC)
        if (isUnderlined) output.append(Formatting.UNDERLINE)
        if (isObfuscated) output.append(Formatting.OBFUSCATED)
        if (isStrikethrough) output.append(Formatting.STRIKETHROUGH)
        return output.toString()
    }

    fun copy(): ChatStyle {
        return ChatStyle(
            color, bold, italic, underlined, strikethrough,
            obfuscated, clickEvent, hoverEvent, insertion
        )
    }

    fun withFormat(color: Formatting?): ChatStyle {
        val bold = if (color == Formatting.BOLD) true else this.bold
        val italic = if (color == Formatting.ITALIC) true else this.italic
        val underlined = if (color == Formatting.UNDERLINE) true else this.underlined
        val strikethrough = if (color == Formatting.STRIKETHROUGH) true else this.strikethrough
        val obfuscated = if (color == Formatting.OBFUSCATED) true else this.obfuscated
        if (color == Formatting.RESET) return EMPTY
        return ChatStyle(
            color, bold, italic, underlined, strikethrough,
            obfuscated, clickEvent, hoverEvent, insertion
        )
    }

    fun withBold(bold: Boolean): ChatStyle = ChatStyle(
        color, bold, italic, underlined, strikethrough,
        obfuscated, clickEvent, hoverEvent, insertion
    )

    fun withUnderline(underlined: Boolean): ChatStyle = ChatStyle(
        color, bold, italic, underlined, strikethrough,
        obfuscated, clickEvent, hoverEvent, insertion
    )

    fun withStrikethrough(strikethrough: Boolean): ChatStyle = ChatStyle(
        color, bold, italic, underlined, strikethrough,
        obfuscated, clickEvent, hoverEvent, insertion
    )

    fun withObfuscated(obfuscated: Boolean): ChatStyle = ChatStyle(
        color, bold, italic, underlined, strikethrough,
        obfuscated, clickEvent, hoverEvent, insertion
    )

    fun withClickEvent(clickEvent: ClickEvent): ChatStyle = ChatStyle(
        color, bold, italic, underlined, strikethrough,
        obfuscated, clickEvent, hoverEvent, insertion
    )

    fun withHoverEvent(hoverEvent: HoverEvent): ChatStyle = ChatStyle(
        color, bold, italic, underlined, strikethrough,
        obfuscated, clickEvent, hoverEvent, insertion
    )

    fun withInsertion(insertion: String): ChatStyle = ChatStyle(
        color, bold, italic, underlined, strikethrough,
        obfuscated, clickEvent, hoverEvent, insertion
    )

    fun withParent(parent: ChatStyle): ChatStyle = ChatStyle(
        parent.color ?: color, parent.bold ?: bold, parent.italic ?: italic,
        parent.underlined ?: underlined, parent.strikethrough ?: strikethrough,
        parent.obfuscated ?: obfuscated, parent.clickEvent ?: clickEvent,
        parent.hoverEvent ?: hoverEvent, parent.insertion ?: insertion
    )

    class Serializer : JsonDeserializer<ChatStyle>, JsonSerializer<ChatStyle> {

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ChatStyle {
            val root = json as JsonObject
            val color: Formatting? = if (root.has("color")) context.deserialize(root["color"], Formatting::class.java) else null
            val bold = if (root.has("bold")) root["bold"].asBoolean else null
            val italic = if (root.has("italic")) root["italic"].asBoolean else null
            val underlined = if (root.has("underlined")) root["underlined"].asBoolean else null
            val strikethrough = if (root.has("strikethrough")) root["strikethrough"].asBoolean else null
            val obfuscated = if (root.has("obfuscated")) root["obfuscated"].asBoolean else null
            val insertion = if (root.has("insertion")) root["insertion"].asString else null
            val clickEvent = deserializeClickEvent(root)
            val hoverEvent = deserializeHoverEvent(root, context)
            return ChatStyle(
                color,
                bold,
                italic,
                underlined,
                strikethrough,
                obfuscated,
                clickEvent,
                hoverEvent,
                insertion
            )
        }

        private fun deserializeClickEvent(root: JsonObject): ClickEvent? {
            if (!root.has("clickEvent")) return null
            val clickEvent = root.getAsJsonObject("clickEvent") ?: return null
            val name = clickEvent.getAsJsonPrimitive("action")?.asString ?: return null
            val action = ClickEvent.Action.byName(name) ?: return null
            val value = clickEvent.getAsJsonPrimitive("value")?.asString ?: return null
            return ClickEvent(action, value)
        }

        private fun deserializeHoverEvent(root: JsonObject, context: JsonDeserializationContext): HoverEvent? {
            if (!root.has("hoverEvent")) return null
            val hoverEvent = root.getAsJsonObject("hoverEvent") ?: return null
            val name = hoverEvent.getAsJsonPrimitive("action")?.asString ?: return null
            val action = HoverEvent.Action.byName(name) ?: return null
            val value = context.deserialize<Text>(hoverEvent["value"], Text::class.java) ?: return null
            return HoverEvent(action, value)
        }

        override fun serialize(src: ChatStyle, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            val root = JsonObject()
            if (src.color != null) root.add("color", context.serialize(src.color))
            if (src.bold != null) root.addProperty("bold", src.bold)
            if (src.italic != null) root.addProperty("italic", src.italic)
            if (src.underlined != null) root.addProperty("underlined", src.underlined)
            if (src.strikethrough != null) root.addProperty("strikethrough", src.strikethrough)
            if (src.obfuscated != null) root.addProperty("obfuscated", src.obfuscated)
            if (src.insertion != null) root.addProperty("insertion", src.insertion)
            val clickEventRaw = src.clickEvent
            if (clickEventRaw != null) {
                val clickEvent = JsonObject()
                clickEvent.addProperty("action", clickEventRaw.action.actionName)
                clickEvent.addProperty("value", clickEventRaw.value)
                root.add("clickEvent", clickEvent)
            }
            val hoverEventRaw = src.hoverEvent
            if (hoverEventRaw != null) {
                val hoverEvent = JsonObject()
                hoverEvent.addProperty("action", hoverEventRaw.action.actionName)
                hoverEvent.add("value", context.serialize(hoverEventRaw.value))
                root.add("hoverEvent", hoverEvent)
            }
            return root
        }

    }

}