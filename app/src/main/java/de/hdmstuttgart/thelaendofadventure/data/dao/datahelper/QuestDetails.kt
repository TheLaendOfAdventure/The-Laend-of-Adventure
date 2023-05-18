package de.hdmstuttgart.thelaendofadventure.data.dao.datahelper

data class QuestDetails(
    val userQuestID: Int,
    val userID: Int,
    val questID: Int,
    val currentGoalNumber: Int,
    val name: String,
    val description: String,
    val targetGoalNumber: Int
)
