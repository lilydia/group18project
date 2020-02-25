package com.uwaterloo.watodo;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TaskDao {

    @Insert
    void insert(Task task);

    @Update
    void update(Task task);

    @Delete
    void delete(Task task);

    @Query("DELETE FROM Task")
    void deleteAllTasks();

    @Query("SELECT * FROM Task ORDER BY priority DESC")
    LiveData<List<Task>> getAllTasks();
}
