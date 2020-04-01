package com.uwaterloo.watodo;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;



@Database(entities = Task.class, version = 2)
public abstract class TaskDatabase extends RoomDatabase {

    private static TaskDatabase instance;

    public abstract TaskDao taskDao();

    public static synchronized TaskDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    TaskDatabase.class, "task_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    // following code is for adding sample tasks when database is created, might be deleted later
    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(instance).execute();
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
        private TaskDao taskDao;

        private PopulateDbAsyncTask(TaskDatabase db) {
            taskDao = db.taskDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            taskDao.insert(new Task( "Title 1", "Description 1", 0, 0, null, 0,1, 2020, 3,20));
            taskDao.insert(new Task( "Title 2", "Description 2", 0, 0, null, 25,2, 2020, 3,20));
            taskDao.insert(new Task( "Title 3", "Description 3", 0, 0, null, 50,3, 2020, 3,20));
            return null;
        }
    }
}
