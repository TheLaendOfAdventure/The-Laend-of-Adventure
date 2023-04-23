package de.hdmstuttgart.thelaendofadventure.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import de.hdmstuttgart.thelaendofadventure.data.dao.*
import de.hdmstuttgart.thelaendofadventure.data.entity.*

@Database(
    entities = [
        UserEntity::class,
        QuestEntity::class,
        ActionEntity::class,
        RiddleEntity::class,
        LocationEntity::class,
        QuestGoalEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun questDao(): QuestDao
    abstract fun actionDao(): ActionDao
    abstract fun riddleDao(): RiddleDao
    abstract fun locationDao(): LocationDao
    abstract fun goalDao(): GoalDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, "app_database")
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}
