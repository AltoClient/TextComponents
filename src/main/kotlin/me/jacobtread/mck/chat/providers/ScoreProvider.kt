package me.jacobtread.mck.chat.providers

interface ScoreProvider {

    fun canProvide(): Boolean

    fun provide(name: String, objective: String): String

}