package de.hdmstuttgart.thelaendofadventure.data.dao.datahelper

import de.hdmstuttgart.thelaendofadventure.data.entity.QuestEntity

data class QuestWithUserLevel(
    val quest: List<QuestEntity>,
    val userLevel: Int
)
