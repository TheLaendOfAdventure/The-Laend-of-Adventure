package de.hdmstuttgart.thelaendofadventure.data.dao.datahelper

data class LocationGoal(
    val questID: Int,
    val questGoalID: Int,
    val currentGoalNumber: Int,
    val latitude: Double,
    val longitude: Double
)
