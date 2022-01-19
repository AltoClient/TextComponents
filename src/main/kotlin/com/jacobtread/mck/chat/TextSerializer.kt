package com.jacobtread.mck.chat

import com.google.gson.*
import com.jacobtread.mck.chat.types.LiteralText
import com.jacobtread.mck.chat.types.ScoreText
import com.jacobtread.mck.chat.types.SelectorText
import com.jacobtread.mck.chat.types.TranslationText
import com.jacobtread.mck.utils.json.EnumTypeAdapterFactory
import com.jacobtread.mck.utils.json.expectString
import com.jacobtread.mck.utils.json.expectStringOrDefault
import java.lang.reflect.Type

class TextSerializer : JsonDeserializer<Text>, JsonSerializer<Text> {

    companion object {
        private val GSON: Gson = GsonBuilder()
            .registerTypeAdapter(Text::class.java, TextSerializer())
            .registerTypeAdapter(ChatStyle::class.java, ChatStyle.Serializer())
            .registerTypeAdapterFactory(EnumTypeAdapterFactory())
            .create()

        @JvmStatic
        fun serialize(text: Text): String = GSON.toJson(text)

        @JvmStatic
        fun deserialize(text: String): Text? = GSON.fromJson(text, Text::class.java)
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Text {
        return when (json) {
            is JsonPrimitive -> LiteralText(json.asString)
            is JsonArray -> {
                var root: Text? = null
                json.forEach {
                    val text = deserialize(it, it.javaClass, context)
                    if (root == null) root = text
                    else root!!.append(text)
                }
                if (root == null) throw JsonParseException("Text array had no root")
                root!!
            }
            is JsonObject -> {
                val text = if (json.has("text")) {
                    LiteralText(json.expectString("text"))
                } else if (json.has("translate")) {
                    val key = json.expectString("translate")
                    if (json.has("with")) {
                        val argArray = json["with"].asJsonArray
                        val args = arrayOfNulls<Any?>(argArray.size())
                        argArray.forEachIndexed { index, jsonElement ->
                            val obj = deserialize(jsonElement, typeOfT, context)
                            args[index] = if (obj is LiteralText
                                && obj.chatStyle == ChatStyle.EMPTY
                                && obj.siblings.isEmpty()
                            ) {
                                obj.asString()
                            } else {
                                obj
                            }
                        }
                        TranslationText(key, *args)
                    } else {
                        TranslationText(key)
                    }
                } else if (json.has("score")) {
                    val scoreObj = json["score"].asJsonObject
                    if (scoreObj.has("name") && scoreObj.has("objective")) {
                        val value = scoreObj.expectStringOrDefault("value", "")
                        ScoreText(
                            scoreObj.expectString("name"),
                            scoreObj.expectString("objective"),
                            value
                        )
                    } else throw JsonParseException("Score text missing name and objective")
                } else if (json.has("selector")) {
                    SelectorText(json.expectString("selector"))
                } else {
                    throw JsonParseException("Down know how to turn $json into text")
                }
                if (json.has("extra")) {
                    val extra = json["extra"].asJsonArray
                    if (extra.isEmpty) throw JsonParseException("Array of extra components was empty")
                    extra.forEach {
                        text.append(deserialize(it, typeOfT, context))
                    }
                }
                val chatStyle = context.deserialize<ChatStyle>(json, ChatStyle::class.java)
                if (chatStyle != null) {
                    text.chatStyle = chatStyle
                }
                text
            }
            else -> throw JsonParseException("Down know how to turn $json into text")
        }
    }

    override fun serialize(src: Text, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        if (src is LiteralText && src.chatStyle == ChatStyle.EMPTY && src.siblings.isEmpty()) {
            return JsonPrimitive(src.asString())
        } else {
            val root = JsonObject()
            if (src.chatStyle != ChatStyle.EMPTY) {
                val chatStyleEl = context.serialize(src.chatStyle)
                if (chatStyleEl is JsonObject) {
                    for ((key, value) in chatStyleEl.entrySet()) {
                        root.add(key, value)
                    }
                }
            }
            if (src.siblings.isNotEmpty()) {
                val siblingArray = JsonArray()
                for (sibling in src.siblings) {
                    siblingArray.add(serialize(sibling, sibling.javaClass, context))
                }
                root.add("extra", siblingArray)
            }
            if (src is LiteralText) {
                root.addProperty("text", src.asString())
            } else if (src is TranslationText) {
                root.addProperty("translate", src.key)
                if (src.formatArgs.isNotEmpty()) {
                    val argsArray = JsonArray()
                    src.formatArgs.forEach {
                        if (it is Text) {
                            argsArray.add(serialize(it, it.javaClass, context))
                        } else {
                            argsArray.add(JsonPrimitive(it?.toString() ?: "null"))
                        }
                    }
                    root.add("with", argsArray)
                }
            } else if (src is ScoreText) {
                val scoreObj = JsonObject()
                scoreObj.addProperty("name", src.name)
                scoreObj.addProperty("objective", src.objective)
                scoreObj.addProperty("value", src.value)
                root.add("score", scoreObj)
            } else if (src is SelectorText) {
                root.addProperty("selector", src.selector)
            } else {
                throw IllegalStateException("Dont know how to serialize $src")
            }
            return root
        }
    }
}
