package de.hdmstuttgart.thelaendofadventure.data

import android.content.Context
import de.hdmstuttgart.thelaendofadventure.data.offlinerepository.OfflineActionRepository
import de.hdmstuttgart.thelaendofadventure.data.offlinerepository.OfflineBadgeRepository
import de.hdmstuttgart.thelaendofadventure.data.offlinerepository.OfflineQuestRepository
import de.hdmstuttgart.thelaendofadventure.data.offlinerepository.OfflineUserRepository
import de.hdmstuttgart.thelaendofadventure.data.repository.ActionRepository
import de.hdmstuttgart.thelaendofadventure.data.repository.BadgeRepository
import de.hdmstuttgart.thelaendofadventure.data.repository.QuestRepository
import de.hdmstuttgart.thelaendofadventure.data.repository.UserRepository

interface AppContainer {
    val actionRepository: ActionRepository
    val badgeRepository: BadgeRepository
    val quesRepository: QuestRepository
    val userRepository: UserRepository
}

/**
 * [AppContainer] implementation that provides instance of [ActionRepository], [BadgeRepository],
 * [QuestRepository] & [UserRepository]
 */
class AppDataContainer(private val context: Context) : AppContainer {
    /**
     * Implementation for [ActionRepository]
     */
    override val actionRepository: ActionRepository by lazy {
        OfflineActionRepository(AppDatabase.getDatabase(context).actionDao())
    }

    /**
     * Implementation for [BadgeRepository]
     */
    override val badgeRepository: BadgeRepository by lazy {
        OfflineBadgeRepository(AppDatabase.getDatabase(context).badgeDao())
    }

    /**
     * Implementation for [QuestRepository]
     */
    override val quesRepository: QuestRepository by lazy {
        OfflineQuestRepository(AppDatabase.getDatabase(context).questDao())
    }

    /**
     * Implementation for [UserRepository]
     */
    override val userRepository: UserRepository by lazy {
        OfflineUserRepository(AppDatabase.getDatabase(context).userDao())
    }
}
