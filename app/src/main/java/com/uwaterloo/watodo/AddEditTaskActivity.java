package com.uwaterloo.watodo;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioButton;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.core.content.FileProvider;

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
import java.io.File;
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
    // GALLERY/CAMERA REQUESTS
    public static final int FILE_PICKER_REQUEST = 600;
    public static final int CAMERA_REQUEST_CODE = 601;
    public static final int CAMERA_PERMISSION_REQUEST_CODE = 602;

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
    // Attachments
    private TextView attachmentFilename;
    private Uri attachmentUri;

    private String apiKey = "AIzaSyA-hySyjyBDVSBKnH2GUv87RsEcLhUF7xM";
    private double[] coords;
    private RadioButton selectNotifyTime1;
    private RadioButton selectNotifyTime3;
    private RadioButton selectNotifyTime7;
    private RadioButton selectNotifyLocOnsite;
    private RadioButton selectNotifyLocClose;
    private String placeName;
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
        selectNotifyLocOnsite = findViewById(R.id.radio_onsite);
        selectNotifyLocOnsite.setOnClickListener(this);
        selectNotifyLocClose = findViewById(R.id.radio_close);
        selectNotifyLocClose.setOnClickListener(this);
        cancelReminder = findViewById(R.id.button_cancel);
        cancelReminder.setOnClickListener(this);
      
        // get a reference to the image view that holds the image that the user will see.
        attachmentFilename = findViewById(R.id.attachmentFilename);

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
                placeName = place.getName();
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
            selectNotifyTime1.setChecked(false);
            selectNotifyTime3.setChecked(false);
            selectNotifyTime7.setChecked(false);
            TextView textView=findViewById(R.id.notify);
            textView.setText("Reminder canceled." );
        }

        if (view == selectNotifyTime1){
            counterTime(1, selectNotifyTime1);
        }

        if (view == selectNotifyTime3){
            counterTime(3, selectNotifyTime3);
        }

        if (view == selectNotifyTime7){
            counterTime(7, selectNotifyTime7);
        }

        if (view == selectNotifyLocOnsite){
            Notify.create(getApplicationContext())
                    .setTitle("You are at:")
                    //.setContent(placeName) //phone version
                    .setContent("University of Waterloo") //emulator version
                    .setColor(R.color.colorPrimary)
                    .setImportance(Notify.NotificationImportance.MAX)
                    .show(); // Finally showing the notification
        }

        if (view == selectNotifyLocClose){

            Notify.create(getApplicationContext())
                    .setTitle("You are approaching:")
                    //.setContent(placeName) //phone version
                    .setContent("University of Waterloo") //emulator version
                    .setColor(R.color.colorPrimary)
                    .setImportance(Notify.NotificationImportance.MAX)
                    .show(); // Finally showing the notification
        }

    }

    public void counterTime(int time, final RadioButton selectNotifyTime){

        counter = new CountDownTimer(time*10000, 1000) {
            TextView textView=findViewById(R.id.notify);

            public void onTick(long millisUntilFinished) {
                selectNotifyTime.setChecked(true);
                textView.setText("The reminder will be sent in " + millisUntilFinished / 1000 + " seconds.");

            }

            public void onFinish() {
                selectNotifyTime.setChecked(false);
                String title = editTextTitle.getText().toString();
                String description = editTextDescription.getText().toString();

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss z");
                String currentDateandTime = "Reminder sent on: ";
                currentDateandTime = currentDateandTime + sdf.format(new Date());
                textView.setText(currentDateandTime);

                Notify.create(getApplicationContext())
                        .setTitle("Upcoming deadline: " + title )
                        .setContent(description)
                        .setColor(R.color.colorPrimary)
                        .setImportance(Notify.NotificationImportance.MAX)
                        .show(); // Finally showing the notification
            }
        }.start();

    }


    // CAMERA INTENTS
    public void onTakePhotoClicked(View v) {
        if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            invokeCamera();
        } else {
            // let's request permission.
            String[] permissionRequest = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            requestPermissions(permissionRequest, CAMERA_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            // we have heard back from our request for camera and write external storage.
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                invokeCamera();
            } else {
                Toast.makeText(this, "Cannot open camera", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void invokeCamera() {

        // get a file reference
        Uri pictureUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", createImageFile());

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // tell the camera where to save the image.
        intent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri);

        // tell the camera to request WRITE permission.
        intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        startActivityForResult(intent, CAMERA_REQUEST_CODE);

    }

    private File createImageFile() {
        // the public picture director
        File picturesDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        // timestamp makes unique name.
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = sdf.format(new Date());

        // put together the directory and the timestamp to make a unique image location.
        File imageFile = new File(picturesDirectory, "picture" + timestamp + ".jpg");

        return imageFile;
    }

    // clear attachments from selection
    public void onClearClicked(View v) {
        attachmentFilename.setText("");
        attachmentUri = null;
        Toast.makeText(this, "Attachments cleared", Toast.LENGTH_LONG).show();
    }

    // Open file picker for attachment selection
    public void onImageGalleryClicked(View v) {
        // invoke the file picker intent
        Intent filePickerIntent = new Intent(Intent.ACTION_PICK);

        // location of attachment storage
        File fileDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String fileDirectoryPath = fileDirectory.getPath();
        // finally, get a URI representation
        Uri data = Uri.parse(fileDirectoryPath);
        // save
        attachmentUri = data;

        // set the data and type.  Get all image types.
        filePickerIntent.setDataAndType(data, "*/*");

        // we will invoke this activity, and get something back from it.
        startActivityForResult(filePickerIntent, FILE_PICKER_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST_CODE) {
                Toast.makeText(this, "Image saved", Toast.LENGTH_LONG).show();
            }
            if (requestCode == FILE_PICKER_REQUEST) {
                Uri attachmentUri = data.getData();
                String filename = queryName(attachmentUri);
                // show the attachment filename to the user
                attachmentFilename.setText(filename);
            }
        }
    }

    private String queryName(Uri uri) {
        Cursor returnCursor =
                getContentResolver().query(uri, null, null, null, null);
        assert returnCursor != null;
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();
        String name = returnCursor.getString(nameIndex);
        returnCursor.close();
        return name;
    }

}
