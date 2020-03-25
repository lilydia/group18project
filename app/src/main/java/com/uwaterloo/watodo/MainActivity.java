package com.uwaterloo.watodo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final int ADD_TASK_REQUEST = 1;
    public static final int EDIT_TASK_REQUEST = 2;
    private TaskViewModel taskViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton buttonAddTask = findViewById(R.id.button_add_task);
        buttonAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddEditTaskActivity.class);
                startActivityForResult(intent, ADD_TASK_REQUEST);
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        final TaskAdapter adapter = new TaskAdapter();
        recyclerView.setAdapter(adapter);

        taskViewModel = ViewModelProviders.of(this).get(TaskViewModel.class);
        taskViewModel.getAllTasks().observe(this, new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> tasks) {
//                Toast.makeText(MainActivity.this, "OnChanged",Toast.LENGTH_SHORT).show();
                adapter.submitList(tasks);
            }
        });

        // swipe to delete callback
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                taskViewModel.delete(adapter.getTaskAt(viewHolder.getAdapterPosition()));
                Toast.makeText(MainActivity.this, "Task deleted", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);
        
        adapter.setOnItemClickListener(new TaskAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Task task) {
                // pass data to ViewTaskActivity and start ViewTaskActivity
                Intent intent = new Intent(MainActivity.this, ViewTaskActivity.class);
//               Intent intent = new Intent(MainActivity.this, AddEditTaskActivity.class);
                intent.putExtra(ViewTaskActivity.EXTRA_ID, task.getId());
                intent.putExtra(ViewTaskActivity.EXTRA_TITLE, task.getTitle());
                intent.putExtra(ViewTaskActivity.EXTRA_DESCRIPTION, task.getDescription());
                intent.putExtra(ViewTaskActivity.EXTRA_PRIORITY, task.getPriority());
                intent.putExtra(ViewTaskActivity.EXTRA_COMPLETENESS, task.getCompleteness());
                intent.putExtra(ViewTaskActivity.EXTRA_YEAR, task.getDdlYear());
                intent.putExtra(ViewTaskActivity.EXTRA_MONTH, task.getDdlMonth());
                intent.putExtra(ViewTaskActivity.EXTRA_DAY, task.getDdlDay());
//                startActivityForResult(intent, EDIT_TASK_REQUEST);
                startActivityForResult(intent, EDIT_TASK_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_TASK_REQUEST && resultCode == RESULT_OK) {
            String title = data.getStringExtra(AddEditTaskActivity.EXTRA_TITLE);
            String description = data.getStringExtra(AddEditTaskActivity.EXTRA_DESCRIPTION);
            int priority = data.getIntExtra(AddEditTaskActivity.EXTRA_PRIORITY, 0);
            int year = data.getIntExtra(AddEditTaskActivity.EXTRA_YEAR,0);
            int month = data.getIntExtra(AddEditTaskActivity.EXTRA_MONTH,0);
            int date = data.getIntExtra(AddEditTaskActivity.EXTRA_DAY,0);

            Task task = new Task(title, description, "location added", null, 0, priority,year, month, date);
            taskViewModel.insert(task);

            Toast.makeText(this, "Task saved", Toast.LENGTH_SHORT).show();
        } else if (requestCode == EDIT_TASK_REQUEST && resultCode == RESULT_OK) {
            int id = data.getIntExtra(ViewTaskActivity.EXTRA_ID, -1);

            // validate id, but this control flow *should* never happen
            if (id == -1) {
                Toast.makeText(this, "Task can't be updated", Toast.LENGTH_SHORT).show();
                return;
            }

            String title = data.getStringExtra(ViewTaskActivity.EXTRA_TITLE);
            String description = data.getStringExtra(ViewTaskActivity.EXTRA_DESCRIPTION);
            int priority = data.getIntExtra(ViewTaskActivity.EXTRA_PRIORITY, 1);
            int year = data.getIntExtra(ViewTaskActivity.EXTRA_YEAR,0);
            int month = data.getIntExtra(ViewTaskActivity.EXTRA_MONTH,0);
            int date = data.getIntExtra(ViewTaskActivity.EXTRA_DAY,0);
            int completeness = data.getIntExtra(ViewTaskActivity.EXTRA_COMPLETENESS, 0);

            Task task = new Task(title, description, "location edited", null, completeness, priority,year,month,date);
            task.setId(id);
            taskViewModel.update(task);
            Toast.makeText(this, "Task updated", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Task not saved", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_all_tasks:
                taskViewModel.deleteAllTasks();
                Toast.makeText(this, "All tasks deleted", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
