package com.bluepixel.mood.data.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.bluepixel.mood.data.database.entity.ModeEntity;

import java.util.List;

@Dao
public interface ModeDao {

    @Query("SELECT * FROM modes WHERE enabled = 1 ORDER BY favorite DESC, sortOrder ASC, id ASC")
    LiveData<List<ModeEntity>> observeAll();

    @Query("SELECT * FROM modes WHERE favorite = 1 AND enabled = 1 ORDER BY sortOrder ASC, id ASC")
    LiveData<List<ModeEntity>> observeFavorites();

    @Query("SELECT * FROM modes ORDER BY sortOrder ASC, id ASC")
    List<ModeEntity> getAllBlocking();

    @Query("SELECT * FROM modes WHERE id = :id LIMIT 1")
    ModeEntity getByIdBlocking(long id);

    @Query("SELECT * FROM modes WHERE visualType = :visualType AND enabled = 1 ORDER BY builtIn DESC, id ASC LIMIT 1")
    ModeEntity getFirstByVisualTypeBlocking(String visualType);

    @Query("SELECT * FROM modes WHERE favorite = 1 AND enabled = 1 ORDER BY sortOrder ASC, id ASC LIMIT 1")
    ModeEntity getFirstFavoriteBlocking();

    @Query("SELECT COUNT(*) FROM modes")
    int countBlocking();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(ModeEntity entity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ModeEntity> entities);

    @Update
    void update(ModeEntity entity);

    /*
     * قبلاً شرط builtIn = 0 داشت و مودهای پیش‌فرض حذف نمی‌شدند.
     * از این به بعد همه حالت‌ها، چه پیش‌فرض چه شخصی، با id حذف می‌شوند.
     */
    @Query("DELETE FROM modes WHERE id = :id")
    int deleteById(long id);

    /*
     * برای سازگاری با کدهای قدیمی نگه داشته شده،
     * ولی دیگر فقط Custom را حذف نمی‌کند.
     */
    @Query("DELETE FROM modes WHERE id = :id")
    int deleteCustom(long id);

    @Query("DELETE FROM modes WHERE builtIn = 0")
    void deleteAllCustom();
}
