package com.uwaterloo.watodo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import java.util.Calendar;

public class AddEditTaskActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String EXTRA_ID = "com.uwaterloo.watodo.EXTRA_ID";
    public static final String EXTRA_TITLE = "com.uwaterloo.watodo.EXTRA_TITLE";
    public static final String EXTRA_DESCRIPTION = "com.uwaterloo.watodo.EXTRA_DESCRIPTION";
//    public static final String EXTRA_COMPLETENESS = "com.uwaterloo.watodo.EXTRA_COMPLETENESS";
    public static final String EXTRA_PRIORITY = "com.uwaterloo.watodo.EXTRA_PRIORITY";
    public static final String EXTRA_YEAR = "com.uwaterloo.watodo.EXTRA_YEAR";
    public static final String EXTRA_MONTH = "com.uwaterloo.watodo.EXTRA_MONTH";
    public static final String EXTRA_DAY = "com.uwaterloo.watodo.EXTRA_DAY";


    private EditText editTextTitle;
    private EditText editTextDescription;
    private RatingBar numberPickerPriority;
    private EditText selectDate;
    private int ddlYear, ddlMonth, ddlDay;
    private int mYear, mMonth, mDay;
  
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);


        editTextTitle = findViewById(R.id.edit_text_title);
        editTextDescription = findViewById(R.id.edit_text_description);
  
        numberPickerPriority = findViewById(R.id.priority_stars);
  
        selectDate = findViewById(R.id.date);
        selectDate.setOnClickListener(this);


        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        // flow for editing an existing task
        Intent intent = getIntent();

        if (intent.hasExtra(EXTRA_ID)) {
            setTitle("Edit Task");
            editTextTitle.setText(intent.getStringExtra(EXTRA_TITLE));
            editTextDescription.setText(intent.getStringExtra(EXTRA_DESCRIPTION));
            ddlYear = intent.getIntExtra(EXTRA_YEAR,0);
            ddlMonth = intent.getIntExtra(EXTRA_MONTH,0);
            ddlDay = intent.getIntExtra(EXTRA_DAY,0);
            String dateText = "";
            if (ddlYear != 0 && ddlMonth != 0 && ddlDay != 0) {
                dateText = ddlYear+"-"+ddlMonth+"-"+ddlDay;
            }
            selectDate.setText(dateText);
//            numberPickerPriority.setRating(Integer.parseInt(intent.getStringExtra(EXTRA_PRIORITY)));
        } else {
            setTitle("Add Task");
        }
    }

    private void saveTask() {
        String title = editTextTitle.getText().toString();
        String description = editTextDescription.getText().toString();
        int priority = (int) numberPickerPriority.getRating();
        
        if (title.trim().isEmpty() || description.trim().isEmpty()) {
            Toast.makeText(this, "Please insert a title and description", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent data = new Intent();
        data.putExtra(EXTRA_TITLE, title);
        data.putExtra(EXTRA_DESCRIPTION, description);
        data.putExtra(EXTRA_PRIORITY, priority);
        data.putExtra(EXTRA_YEAR, ddlYear);
        data.putExtra(EXTRA_MONTH, ddlMonth);
        data.putExtra(EXTRA_DAY, ddlDay);

        // add id only if the task has been edited (existing task; not new)
        int id = getIntent().getIntExtra(EXTRA_ID, -1);
        if (id != -1) {
            data.putExtra(EXTRA_ID, id);
        }

        setResult(RESULT_OK, data);
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

    @Override
    public void onClick(View view) {
        if (view == selectDate) {
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            selectDate.setText(year + "-" + (monthOfYear+1) + "-" + dayOfMonth);
                            ddlYear = year;
                            ddlMonth = monthOfYear+1;
                            ddlDay = dayOfMonth;

                        }
                    }, mYear, mMonth, mDay);

            datePickerDialog.show();
        }
    }
}
