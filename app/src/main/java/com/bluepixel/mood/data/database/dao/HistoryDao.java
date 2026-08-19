package com.bluepixel.mood.data.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.bluepixel.mood.data.database.entity.ModeHistoryEntity;

import java.util.List;

@Dao
public interface HistoryDao {

    @Query("SELECT * FROM mode_history ORDER BY startedAt DESC")
    LiveData<List<ModeHistoryEntity>> observeAll();

    @Insert
    long insert(ModeHistoryEntity entity);

    @Query("DELETE FROM mode_history")
    void clearAll();
}
