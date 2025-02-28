package fr.isen.IMPROTA.isensmartcompanion.ui.theme

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import fr.isen.IMPROTA.isensmartcompanion.data.Chat

@Dao
interface ChatDao {
    @Insert
    suspend fun insertChat(chat: Chat)

    @Query("SELECT * FROM chat_history ORDER BY timestamp DESC")
    suspend fun getAllChats(): List<Chat>
}
