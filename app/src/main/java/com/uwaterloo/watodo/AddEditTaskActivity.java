package com.uwaterloo.watodo;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.application.isradeleon.notify.Notify;

import java.util.Arrays;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddEditTaskActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String EXTRA_ID = "com.uwaterloo.watodo.EXTRA_ID";
    public static final String EXTRA_TITLE = "com.uwaterloo.watodo.EXTRA_TITLE";
    public static final String EXTRA_DESCRIPTION = "com.uwaterloo.watodo.EXTRA_DESCRIPTION";
    public static final String EXTRA_PRIORITY = "com.uwaterloo.watodo.EXTRA_PRIORITY";
    public static final String EXTRA_YEAR = "com.uwaterloo.watodo.EXTRA_YEAR";
    public static final String EXTRA_MONTH = "com.uwaterloo.watodo.EXTRA_MONTH";
    public static final String EXTRA_DAY = "com.uwaterloo.watodo.EXTRA_DAY";
    public static final String EXTRA_COORDS = "com.uwaterloo.watodo.EXTRA_COORDS";

    public CountDownTimer counter = new CountDownTimer(300,100) {
        @Override
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {
        }
    };


    private EditText editTextTitle;
    private EditText editTextDescription;
    private RatingBar numberPickerPriority;
    private EditText selectDate;
    private int ddlYear, ddlMonth, ddlDay;
    private int mYear, mMonth, mDay;
    private String apiKey = "AIzaSyA-hySyjyBDVSBKnH2GUv87RsEcLhUF7xM";
    private double[] coords;
    private RadioButton selectNotifyTime1;
    private RadioButton selectNotifyTime3;
    private RadioButton selectNotifyTime7;
    private Button cancelReminder;
  
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        coords = new double[2];
        coords[0] = 0;
        coords[1] = 0;

        // Initialize Places.
        Places.initialize(getApplicationContext(), apiKey);

        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);


        editTextTitle = findViewById(R.id.edit_text_title);
        editTextDescription = findViewById(R.id.edit_text_description);
  
        numberPickerPriority = findViewById(R.id.priority_stars);
  
        selectDate = findViewById(R.id.date);
        selectDate.setOnClickListener(this);

        //reminder time selection
        selectNotifyTime1 = findViewById(R.id.radio_1_day);
        selectNotifyTime1.setOnClickListener(this);
        selectNotifyTime3 = findViewById(R.id.radio_3_days);
        selectNotifyTime3.setOnClickListener(this);
        selectNotifyTime7 = findViewById(R.id.radio_7_days);
        selectNotifyTime7.setOnClickListener(this);
        cancelReminder = findViewById(R.id.button_cancel);
        cancelReminder.setOnClickListener(this);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        // flow for editing an existing task
        Intent intent = getIntent();

        if (intent.hasExtra(EXTRA_ID)) {
            setTitle("Edit Task");
            editTextTitle.setText(intent.getStringExtra(EXTRA_TITLE));
            editTextDescription.setText(intent.getStringExtra(EXTRA_DESCRIPTION));
            numberPickerPriority.setRating(intent.getIntExtra(EXTRA_PRIORITY,0));
            ddlYear = intent.getIntExtra(EXTRA_YEAR,0);
            ddlMonth = intent.getIntExtra(EXTRA_MONTH,0);
            ddlDay = intent.getIntExtra(EXTRA_DAY,0);
            coords = intent.getDoubleArrayExtra(EXTRA_COORDS);
            String dateText = "";
            if (ddlYear != 0 && ddlMonth != 0 && ddlDay != 0) {
                dateText = ddlYear+"-"+ddlMonth+"-"+ddlDay;
            }
            selectDate.setText(dateText);
//            numberPickerPriority.setRating(Integer.parseInt(intent.getStringExtra(EXTRA_PRIORITY)));
        } else {
            setTitle("Add Task");
        }

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i("AddEditTask", "Place: " + place.getName() + ", " + place.getId());
                Log.i("AddEditTask", "Lat, Long: " + place.getLatLng());
                coords = new double[2];
                coords[0] = place.getLatLng().latitude;
                coords[1] = place.getLatLng().longitude;
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("AddEditTask", "An error occurred while selecting a Place: " + status);
            }
        });
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
        data.putExtra(EXTRA_COORDS, coords);

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
        if (view == cancelReminder){
            counter.cancel();
            TextView textView=findViewById(R.id.notify);
            textView.setText("Reminder canceled." );
        }

        if (view == selectNotifyTime1){
            counterTime(10);
        }

        if (view == selectNotifyTime3){
            counterTime(30);
        }

        if (view == selectNotifyTime7){
            counterTime(70);
        }

    }

    public void counterTime(int time){

        counter = new CountDownTimer(time*1000, 1000) {
            TextView textView=findViewById(R.id.notify);

            public void onTick(long millisUntilFinished) {
                textView.setText("The reminder will be sent in " + millisUntilFinished / 1000 + " seconds.");

            }

            public void onFinish() {
                String title = editTextTitle.getText().toString();
                String description = editTextDescription.getText().toString();

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
                String currentDateandTime = "Reminder sent on: ";
                currentDateandTime = currentDateandTime + sdf.format(new Date());
                textView.setText(currentDateandTime);

                Notify.create(getApplicationContext())
                        .setTitle(title)
                        .setContent(description)
                        .setColor(R.color.colorPrimary)
                        .show(); // Finally showing the notification
            }
        }.start();

    }


}
