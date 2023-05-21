package de.hdmstuttgart.thelaendofadventure.data.dao.datahelper

data class BadgeDetails(
    val badgeID: Int,
    val name: String,
    val description: String,
    val imagePath: String,
    val targetGoalNumber: Int,
    val currentGoalNumber: Int
)
