package de.hdmstuttgart.thelaendofadventure.ui.helper

object StringHelper {
    fun strikethroughText(input: String): String {
        val stringBuilder = StringBuilder()

        for (char in input) {
            stringBuilder.append(char)
            stringBuilder.append('\u0336')
        }

        return stringBuilder.toString()
    }
}
