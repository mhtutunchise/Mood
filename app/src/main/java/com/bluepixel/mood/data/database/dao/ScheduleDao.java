package com.bluepixel.mood.data.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.bluepixel.mood.data.database.entity.ScheduleEntity;

import java.util.List;

@Dao
public interface ScheduleDao {

    @Query("SELECT * FROM schedules ORDER BY hour ASC, minute ASC")
    LiveData<List<ScheduleEntity>> observeAll();

    @Query("SELECT * FROM schedules ORDER BY hour ASC, minute ASC")
    List<ScheduleEntity> getAllBlocking();

    @Query("SELECT * FROM schedules WHERE enabled = 1")
    List<ScheduleEntity> getEnabledBlocking();

    @Query("SELECT * FROM schedules WHERE id = :id LIMIT 1")
    ScheduleEntity getByIdBlocking(long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(ScheduleEntity entity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ScheduleEntity> entities);

    @Update
    void update(ScheduleEntity entity);

    @Query("DELETE FROM schedules WHERE id = :id")
    void delete(long id);

    @Query("DELETE FROM schedules WHERE modeId = :modeId")
    void deleteByModeId(long modeId);

    @Query("DELETE FROM schedules")
    void clearAll();
}
