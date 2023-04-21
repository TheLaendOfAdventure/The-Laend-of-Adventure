package de.hdmstuttgart.thelaendofadventure.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.hdmstuttgart.thelaendofadventure.data.entity.LocationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLocation(location: LocationEntity)

    @Query("SELECT * FROM location WHERE locationID = :locationID")
    fun getLocationById(locationID: Int): Flow<LocationEntity>

    @Query("DELETE FROM location WHERE locationID = :locationID")
    suspend fun deleteLocationById(locationID: Int)

    @Query("DELETE FROM location")
    suspend fun deleteAllLocations()
}
