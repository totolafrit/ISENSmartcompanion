package fr.isen.IMPROTA.isensmartcompanion.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface EventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: Event)

    @Query("SELECT * FROM events ORDER BY date ASC")
    suspend fun getAllEvents(): List<Event>

    @Query("SELECT * FROM events WHERE date = :selectedDate")
    suspend fun getEventsByDate(selectedDate: String): List<Event>
}
