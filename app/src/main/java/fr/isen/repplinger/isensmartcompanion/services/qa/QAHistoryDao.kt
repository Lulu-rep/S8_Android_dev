package fr.isen.repplinger.isensmartcompanion.services.qa

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import fr.isen.repplinger.isensmartcompanion.models.QAHistory

@Dao
interface QAHistoryDao {
    @Insert
    suspend fun insert(qaHistory: QAHistory)

    @Query("SELECT * FROM qa_history ORDER BY date DESC")
    suspend fun getAll(): List<QAHistory>

    @Delete
    suspend fun delete(qaHistory: QAHistory)

    @Query("DELETE FROM qa_history")
    suspend fun deleteAll()
}