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

    // this method might be modified to delete certain tasks later
    @Query("DELETE FROM task_table")
    void deleteAllTasks();

    @Query("SELECT * FROM task_table ORDER BY priority DESC")
    LiveData<List<Task>> getAllTasksByPriority();

    @Query("SELECT * FROM task_table ORDER BY completeness DESC")
    LiveData<List<Task>> getAllTasksByComp();

    @Query("SELECT * FROM task_table ORDER BY ddlYear,ddlMonth,ddlDay ASC")
    LiveData<List<Task>> getAllTasksByDdl();

    @Query("SELECT DISTINCT groupTag FROM task_table WHERE groupTag IS NOT NULL")
    LiveData<List<String>> getAllGroup();
}
