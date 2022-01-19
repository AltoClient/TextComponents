package me.jacobtread.mck.chat.types

import me.jacobtread.mck.chat.Text
import me.jacobtread.mck.chat.TextBase
import me.jacobtread.mck.utils.providers.ScoreProvider

data class ScoreText(
    val name: String,
    val objective: String,
    var value: String = ""
) : TextBase() {

    companion object {
        // The provided which provides the score values this is internal server logic
        var scoreProvider: ScoreProvider? = null
    }

    override fun asString(): String {
        val scoreProvider = scoreProvider
        if (scoreProvider != null
            && value.isEmpty()
            && scoreProvider.canProvide()
        ) {
            value = scoreProvider.provide(name, objective)
        }
        return value
    }


    override fun copy(): Text {
        val scoreText = ScoreText(name, objective, value)
        scoreText.chatStyle = chatStyle.copy()
        siblings.forEach { scoreText.append(it.copy()) }
        return scoreText
    }
}