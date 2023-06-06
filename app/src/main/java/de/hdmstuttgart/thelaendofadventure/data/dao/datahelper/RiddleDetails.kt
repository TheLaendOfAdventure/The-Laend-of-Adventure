package de.hdmstuttgart.thelaendofadventure.data.dao.datahelper

data class RiddleDetails(
    val questID: Int,
    val goalNumber: Int,
    val actionID: Int,
    val question: String,
    val answer: String,
    val hint: String,
    val possibleAnswers: String
)
