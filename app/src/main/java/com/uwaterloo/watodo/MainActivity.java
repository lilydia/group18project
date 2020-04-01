package com.uwaterloo.watodo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final int ADD_TASK_REQUEST = 1;
    public static final int EDIT_TASK_REQUEST = 2;
    private TaskViewModel taskViewModel;
    private Observer<List<Task>> tasksObserver;
    public static GeofencingClient geofencingClient;
    private static final String[] INITIAL_PERMS={
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
    };
    private static Context context;
    /**
     * permissions request code
     */
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;

    /**
     * Permissions that need to be explicitly requested from end user.
     */
    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[] {
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION
            , Manifest.permission.INTERNET};

    /**
     * Checks the dynamically-controlled permissions and requests missing permissions from end user.
     */
    protected void checkPermissions() {
        final List<String> missingPermissions = new ArrayList<String>();
        // check all required dynamic permissions
        for (final String permission : REQUIRED_SDK_PERMISSIONS) {
            final int result = ContextCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }
        if (!missingPermissions.isEmpty()) {
            // request all missing permissions
            final String[] permissions = missingPermissions
                    .toArray(new String[missingPermissions.size()]);
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            final int[] grantResults = new int[REQUIRED_SDK_PERMISSIONS.length];
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED);
            onRequestPermissionsResult(REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS,
                    grantResults);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                for (int index = permissions.length - 1; index >= 0; --index) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                        // exit the app if one permission is not granted
                        Toast.makeText(this, "Required permission '" + permissions[index]
                                + "' not granted, exiting", Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }
                }
                // all permissions were granted
                //initialize();
                break;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainActivity.context = getApplicationContext();

        checkPermissions();

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

        tasksObserver = new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> tasks) {
                adapter.submitList(tasks);
            }
        };
        taskViewModel = ViewModelProviders.of(this).get(TaskViewModel.class);
        taskViewModel.getAllTasksByPriority().observe(this, tasksObserver);

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
                // temporary coord for fixing
                double[] temp = new double[2];
                temp[0] = 0;
                temp[1] = 0;
                intent.putExtra(ViewTaskActivity.EXTRA_COORDS, temp);
                startActivityForResult(intent, EDIT_TASK_REQUEST);
                // TODO: Add coords here and on the ViewTask screen UI as well on a map
            }
        });
        geofencingClient = LocationServices.getGeofencingClient(this);


        RecyclerView GroupRecyclerView = findViewById(R.id.group_recycler_view);
        LinearLayoutManager GroupLayoutManager = new LinearLayoutManager(this);
        GroupLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        GroupRecyclerView.setLayoutManager(GroupLayoutManager);
        GroupRecyclerView.setHasFixedSize(true);

        final GroupAdapter groupAdapter = new GroupAdapter();
        GroupRecyclerView.setAdapter(groupAdapter);

        taskViewModel.getAllGroup().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                groupAdapter.submitList(strings);
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
            double[] coords = data.getDoubleArrayExtra(AddEditTaskActivity.EXTRA_COORDS);


            Log.i("MAIN", "ADD Coords in Main: " + coords[0] + ", " + coords[1]);

            Task task = new Task(title, description, coords[0], coords[1], null, 0, priority,year, month, date);
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
            double[] coords = data.getDoubleArrayExtra(ViewTaskActivity.EXTRA_COORDS);


            Log.i("MAIN", "UPDATE Coords in Main: " + coords[0] + ", " + coords[1]);

            Task task = new Task(title, description, coords[0], coords[1], null, completeness, priority,year,month,date);
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
            case R.id.rank_by_completeness:
                taskViewModel.getAllTasksByComp().observe(this, tasksObserver);
                Toast.makeText(MainActivity.this, "Ranked by completeness", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.rank_by_priority:
                taskViewModel.getAllTasksByPriority().observe(this, tasksObserver);
                Toast.makeText(MainActivity.this, "Ranked by priority", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.rank_by_deadline:
                taskViewModel.getAllTasksByDdl().observe(this, tasksObserver);
                Toast.makeText(MainActivity.this, "Ranked by deadline", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public static Context getAppContext() {
        return MainActivity.context;
    }
}
