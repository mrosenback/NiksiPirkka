package com.example.shareyourbestadvice;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface AdviceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Advice... advices);

    @Update
    void update(Advice... advices);

    @Query("DELETE FROM advices")
    void deleteAllAdvices();

    @Query("SELECT * FROM advices")
    LiveData<List<Advice>> getAllAdvices();

    @Query("SELECT * FROM advices WHERE id = :id")
    Advice getAdviceWithId(int id);
}
