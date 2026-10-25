package com.baaki.app.data.local.dao

import androidx.room.*
import com.baaki.app.data.local.entity.ActivityEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityEventDao {
    @Query("SELECT * FROM activity_events ORDER BY timestamp DESC")
    fun getAllActivities(): Flow<List<ActivityEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: ActivityEventEntity)
}
