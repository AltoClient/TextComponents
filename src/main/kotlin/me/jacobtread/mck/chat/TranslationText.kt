package me.jacobtread.mck.chat

class TranslationText(val key: String, vararg formatArgs: String) : TextBase() {

    val variableRegex = Regex("%(?:(\\d+)\\$)?([A-Za-z%]|$)").toPattern()
    val children = ArrayList<Text>()
    var lastUpdate = -1L

    fun initializeFromFormat(format: String) {
        var offset = 0
        while(true) {
            val match = variableRegex.find()
        }
    }

    override fun copy(): Text {
        TODO("Not yet implemented")
    }
}