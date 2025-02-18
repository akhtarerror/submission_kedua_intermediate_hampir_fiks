package bangkit.mobiledev.storyappdicoding.database.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import bangkit.mobiledev.storyappdicoding.database.entity.RemoteKeysEntity

@Dao
interface RemoteKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<RemoteKeysEntity>)

    @Query("SELECT * FROM remote_keys WHERE id = :id")
    suspend fun getRemoteKeysId(id: String): RemoteKeysEntity?

    @Query("DELETE FROM remote_keys")
    suspend fun deleteRemoteKeys()
}