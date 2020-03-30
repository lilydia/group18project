package com.uwaterloo.watodo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


public class ViewTaskActivity extends AppCompatActivity {
    public static final String EXTRA_ID = "com.uwaterloo.watodo.EXTRA_ID";
    public static final String EXTRA_TITLE = "com.uwaterloo.watodo.EXTRA_TITLE";
    public static final String EXTRA_DESCRIPTION = "com.uwaterloo.watodo.EXTRA_DESCRIPTION";
    public static final String EXTRA_COMPLETENESS = "com.uwaterloo.watodo.EXTRA_COMPLETENESS";
    public static final String EXTRA_LOCATION = "com.uwaterloo.watodo.EXTRA_LOCATION";
    public static final String EXTRA_PRIORITY = "com.uwaterloo.watodo.EXTRA_PRIORITY";
    public static final String EXTRA_YEAR = "com.uwaterloo.watodo.EXTRA_YEAR";
    public static final String EXTRA_MONTH = "com.uwaterloo.watodo.EXTRA_MONTH";
    public static final String EXTRA_DAY = "com.uwaterloo.watodo.EXTRA_DAY";

    private TextView viewTaskTitle;
    private TextView viewTaskDdl;
    private TextView viewTaskLocation;
    private RatingBar viewTaskPriority;
    private SeekBar viewTaskCompleteness;
    private TextView viewTaskCompPercentage;
    private TextView viewTaskDescription;
    private CardView editTaskCard;
    private int id, ddlYear, ddlMonth, ddlDay, priority, completeness;
    private String title, location, description;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_task);

        viewTaskTitle = findViewById(R.id.view_task_title);
        viewTaskDdl = findViewById(R.id.view_task_deadline);
        viewTaskLocation = findViewById(R.id.view_task_location);
        viewTaskPriority = findViewById(R.id.view_task_priority);
        viewTaskCompleteness = findViewById(R.id.view_task_completeness_slider);
        viewTaskCompPercentage = findViewById(R.id.completeness_percentage);
        viewTaskDescription = findViewById(R.id.view_task_description);
        editTaskCard = findViewById(R.id.edit_task_card);

        viewTaskCompleteness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                viewTaskCompPercentage.setText("Completeness: " + progress + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

                getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        setTitle("Task Information");

        // retrieve and display selected task data
        Intent intent = getIntent();

        id = intent.getIntExtra(EXTRA_ID, -1);
        title = intent.getStringExtra(EXTRA_TITLE);
        viewTaskTitle.setText(title);
        ddlYear = intent.getIntExtra(EXTRA_YEAR, 0);
        ddlMonth = intent.getIntExtra(EXTRA_MONTH, 0);
        ddlDay = intent.getIntExtra(EXTRA_DAY, 0);
        String dateText;
        if (ddlYear != 0 && ddlMonth != 0 && ddlDay != 0) {
            dateText = ddlYear + "." + ddlMonth + "." + ddlDay;
        } else {
            dateText = "No Deadline";
        }
        viewTaskDdl.setText(dateText);
        location = intent.getStringExtra(EXTRA_LOCATION);
        viewTaskLocation.setText(location);
        priority = intent.getIntExtra(EXTRA_PRIORITY, 0);
        viewTaskPriority.setRating(priority);
        completeness = intent.getIntExtra(EXTRA_COMPLETENESS, 0);
        viewTaskCompleteness.setProgress(completeness);
        description = intent.getStringExtra(EXTRA_DESCRIPTION);
        viewTaskDescription.setText(description);

        editTaskCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create new intent and put needed Extra value in
                Intent intent = new Intent(ViewTaskActivity.this, AddEditTaskActivity.class);
                intent.putExtra(AddEditTaskActivity.EXTRA_ID, id);
                intent.putExtra(AddEditTaskActivity.EXTRA_TITLE, title);
                intent.putExtra(AddEditTaskActivity.EXTRA_DESCRIPTION, description);
                intent.putExtra(AddEditTaskActivity.EXTRA_PRIORITY, priority);
                intent.putExtra(AddEditTaskActivity.EXTRA_YEAR, ddlYear);
                intent.putExtra(AddEditTaskActivity.EXTRA_MONTH, ddlMonth);
                intent.putExtra(AddEditTaskActivity.EXTRA_DAY, ddlDay);
                startActivityForResult(intent, MainActivity.EDIT_TASK_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // when data is saved from AddEditTaskActivity, retrieve and display the new data
        if (resultCode == RESULT_OK) {
            id = data.getIntExtra(AddEditTaskActivity.EXTRA_ID, -1);

            // validate id, but this control flow *should* never happen
            if (id == -1) {
                Toast.makeText(this, "Task can't be updated", Toast.LENGTH_SHORT).show();
                return;
            }

            title = data.getStringExtra(AddEditTaskActivity.EXTRA_TITLE);
            description = data.getStringExtra(AddEditTaskActivity.EXTRA_DESCRIPTION);
            priority = data.getIntExtra(AddEditTaskActivity.EXTRA_PRIORITY, 0);
            ddlYear = data.getIntExtra(AddEditTaskActivity.EXTRA_YEAR, 0);
            ddlMonth = data.getIntExtra(AddEditTaskActivity.EXTRA_MONTH, 0);
            ddlDay = data.getIntExtra(AddEditTaskActivity.EXTRA_DAY, 0);

            // update the displayed data
            viewTaskTitle.setText(title);
            String dateText;
            if (ddlYear != 0 && ddlMonth != 0 && ddlDay != 0) {
                dateText = ddlYear + "." + ddlMonth + "." + ddlDay;
            } else {
                dateText = "No Deadline";
            }
            viewTaskDdl.setText(dateText);
            viewTaskLocation.setText(location);
            viewTaskPriority.setRating(priority);
            viewTaskDescription.setText(description);
        }

    }

    private void saveTask() {
        //pass all data back to MainActivity
        Intent ndata = new Intent();
        ndata.putExtra(EXTRA_TITLE, title);
        ndata.putExtra(EXTRA_DESCRIPTION, description);
        ndata.putExtra(EXTRA_PRIORITY, priority);
        completeness = viewTaskCompleteness.getProgress();
        ndata.putExtra(EXTRA_COMPLETENESS, completeness);
        ndata.putExtra(EXTRA_YEAR, ddlYear);
        ndata.putExtra(EXTRA_MONTH, ddlMonth);
        ndata.putExtra(EXTRA_DAY, ddlDay);
        ndata.putExtra(EXTRA_ID, id);

        setResult(RESULT_OK, ndata);
        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_task_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_task:
                saveTask();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
