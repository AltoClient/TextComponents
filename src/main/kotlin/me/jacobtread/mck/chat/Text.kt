package me.jacobtread.mck.chat

interface Text : Iterable<Text> {

    /**
     * chatStyle The chat styling for this text possibly null
     */
    var chatStyle: ChatStyle

    /**
     * siblings The list of siblings attached to this text
     */
    val siblings: ArrayList<Text>

    /**
     * Appends the provided text component to the siblings
     * of this component
     *
     * @param text The component to append
     * @return The current text component
     */
    fun append(text: Text): Text

    /**
     * Appends raw text as a sibling for the current
     * component
     *
     * @param text The text to append
     * @return The current text component
     */
    fun append(text: String): Text

    /**
     * Used to get the formatted version of this
     * text which includes formatting codes
     *
     * @return The formatted text
     */
    fun formatted(): String

    /**
     * Used to get the combined string of all
     * the siblings with all special formatting
     * codes removed
     *
     * @return The combined string
     */
    fun asString(): String

    /**
     * Creates a deep copy of this text component
     *
     * @return The copy of this text
     */
    fun copy(): Text

}