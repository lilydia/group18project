package com.uwaterloo.watodo;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class TaskRepository {
    private TaskDao taskDao;
    private LiveData<List<Task>> allTasks;
    private ReminderService reminderService;

    public TaskRepository(Application application){
        TaskDatabase database = TaskDatabase.getInstance(application);
        taskDao = database.taskDao();
        allTasks = taskDao.getAllTasks();
        reminderService = new ReminderService();
    }

    public void insert(Task task) {
        new InsertTaskAsyncTask(taskDao, reminderService).execute(task);
    }

    public void update(Task task) {
        new UpdateTaskAsyncTask(taskDao).execute(task);
    }

    public void delete(Task task) {
        new DeleteTaskAsyncTask(taskDao).execute(task);
    }

    public void deleteAllTasks() {
        new DeleteAllTasksAsyncTask(taskDao).execute();
    }

    public LiveData<List<Task>> getAllTasks() {
        return allTasks;
    }

    private static class InsertTaskAsyncTask extends AsyncTask<Task, Void, Void> {
        private TaskDao taskDao;
        private ReminderService reminderService;

        private InsertTaskAsyncTask(TaskDao taskDao, ReminderService reminderService) {
            this.taskDao = taskDao;
            this.reminderService = reminderService;
        }

        @Override
        protected Void doInBackground(Task... tasks) {
            taskDao.insert(tasks[0]);
            reminderService.newLocationReminder(tasks[0].getLatitude(), tasks[0].getLongitude(), tasks[0].getTitle());
            return null;
        }
    }

    private static class UpdateTaskAsyncTask extends AsyncTask<Task, Void, Void> {
        private TaskDao taskDao;

        private UpdateTaskAsyncTask(TaskDao taskDao) {
            this.taskDao = taskDao;
        }

        @Override
        protected Void doInBackground(Task... tasks) {
            taskDao.update(tasks[0]);
            return null;
        }
    }

    private static class DeleteTaskAsyncTask extends AsyncTask<Task, Void, Void> {
        private TaskDao taskDao;

        private DeleteTaskAsyncTask(TaskDao taskDao) {
            this.taskDao = taskDao;
        }

        @Override
        protected Void doInBackground(Task... tasks) {
            taskDao.delete(tasks[0]);
            return null;
        }
    }

    private static class DeleteAllTasksAsyncTask extends AsyncTask<Void, Void, Void> {
        private TaskDao taskDao;

        private DeleteAllTasksAsyncTask(TaskDao taskDao) {
            this.taskDao = taskDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            taskDao.deleteAllTasks();
            return null;
        }
    }
}
