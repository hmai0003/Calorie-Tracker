package com.example.myapplication;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface DailyStepsDao {
    @Query("SELECT * FROM DailySteps")
    List<DailySteps> getAll();

    @Insert
    long insert(DailySteps dailySteps);

    @Delete
    void delete(DailySteps dailySteps);

    @Update(onConflict = REPLACE)
    public void updateUsers(DailySteps... dailySteps);

    @Query("DELETE FROM DailySteps")
    void deleteAll();

}
