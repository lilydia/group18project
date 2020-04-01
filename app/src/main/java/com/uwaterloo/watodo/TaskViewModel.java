package com.uwaterloo.watodo;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class TaskViewModel extends AndroidViewModel {
    private TaskRepository repository;

    public TaskViewModel(@NonNull Application application) {
        super(application);
        repository = new TaskRepository(application);
    }

    public void insert(Task task) {
        repository.insert(task);
    }

    public void update(Task task) {
        repository.update(task);
    }

    public void delete(Task task) {
        repository.delete(task);
    }

    public void deleteAllTasks() {
        repository.deleteAllTasks();
    }

    public LiveData<List<Task>> getAllTasksByPriority() { return repository.getAllTasksByPriority(); }

    public LiveData<List<Task>> getAllTasksByComp() {
        return repository.getAllTasksByComp();
    }

    public LiveData<List<Task>> getAllTasksByDdl() {
        return repository.getAllTasksByDdl();
    }

    public LiveData<List<String>> getAllGroup() {return repository.getAllGroup();}
}
