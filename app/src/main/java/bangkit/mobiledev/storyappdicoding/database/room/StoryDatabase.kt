package bangkit.mobiledev.storyappdicoding.database.room

import androidx.room.Database
import androidx.room.RoomDatabase
import bangkit.mobiledev.storyappdicoding.database.entity.RemoteKeysEntity
import bangkit.mobiledev.storyappdicoding.database.entity.StoryEntity

@Database(entities = [StoryEntity::class, RemoteKeysEntity::class], version = 1, exportSchema = false)

abstract class StoryDatabase: RoomDatabase() {
    abstract fun storyDao(): StoryDao
    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object {
        @Volatile
        private var INSTANCE: StoryDatabase? = null

        fun getInstance(context: android.content.Context): StoryDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = androidx.room.Room.databaseBuilder(
                    context.applicationContext,
                    StoryDatabase::class.java,
                    "story_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}