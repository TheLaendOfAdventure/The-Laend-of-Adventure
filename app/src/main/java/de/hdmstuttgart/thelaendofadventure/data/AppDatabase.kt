package de.hdmstuttgart.thelaendofadventure.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import de.hdmstuttgart.thelaendofadventure.data.dao.*
import de.hdmstuttgart.thelaendofadventure.data.entity.*

@Database(
    entities = [
        UserQuestEntity::class,
        UserEntity::class,
        UserBadgeEntity::class,
        RiddleEntity::class,
        RiddleAnswersEntity::class,
        QuestGoalEntity::class,
        QuestEntity::class,
        LocationEntity::class,
        BadgeGoalEntity::class,
        BadgeEntity::class,
        ActionEntity::class,
        AchievementEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun questDao(): QuestDao
    abstract fun badgeDao(): BadgeDao
    abstract fun actionDao(): ActionDao

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, "app_database")
                    /**
                     * Setting this option in your app's database builder means that Room
                     * permanently deletes all data from the tables in your database when it
                     * attempts to perform a migration with no defined migration path.
                     */
                    .createFromAsset("database/Laend_of_Adventure.db")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
