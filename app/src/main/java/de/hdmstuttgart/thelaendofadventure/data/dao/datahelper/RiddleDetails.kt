package de.hdmstuttgart.thelaendofadventure.data.dao.datahelper

data class RiddleDetails(
    val actionID: Int,
    val question: String,
    val answer: String,
    val hint: String,
    val possibleAnswers: String
)
