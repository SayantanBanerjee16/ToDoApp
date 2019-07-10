package com.sayantanbanerjee.todolist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.fragment.app.DialogFragment;
import androidx.loader.app.LoaderManager;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.sayantanbanerjee.todolist.data.ToDoContract;

import java.util.Calendar;

public class EditActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    Button time;
    Button date;

    String time_string;
    String date_string;
    String heading_string;
    String message_string;
    EditText heading;
    EditText message;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    public void setDateDialog(View view){
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                EditActivity.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                mDateSetListener,year,month,day);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }


    public void setTimeDialog(View view) {
        DialogFragment timePicker = new TimePickerFragment();
        timePicker.show(getSupportFragmentManager(), "time picker");
    }

    public void insertIntoDatabase(){
        date_string = date_string.trim();
        time_string = time_string.trim();

        ContentValues values = new ContentValues();
        values.put(ToDoContract.ToDoEntry.COLUMN_HEADING,heading_string);
        values.put(ToDoContract.ToDoEntry.COLUMN_MESSAGE,message_string);
        values.put(ToDoContract.ToDoEntry.COLUMN_DATE,date_string);
        values.put(ToDoContract.ToDoEntry.COLUMN_TIME,time_string);

        Uri uri = getContentResolver().insert(ToDoContract.ToDoEntry.CONTENT_URI,values);
        if (uri == null) {
            Toast.makeText(this,"Error with inserting To Do",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "To Do Saved",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        getSupportActionBar().setTitle("Edit To-Do");
        time = (Button) findViewById(R.id.timeButton);
        date = (Button) findViewById(R.id.dateButton);
        heading = (EditText) findViewById(R.id.heading);
        message = (EditText) findViewById(R.id.message);

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month+=1;
                date_string = day + " / " + month + " / " + year;
                date.setText(date_string);
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.save:
                message_string = message.getText().toString().trim();
                heading_string = heading.getText().toString().trim();
                if(TextUtils.isEmpty(heading_string) || TextUtils.isEmpty(message_string) || TextUtils.isEmpty(time_string)|| TextUtils.isEmpty(date_string)){
                    Toast.makeText(EditActivity.this,"No Field Should remain empty!",Toast.LENGTH_LONG).show();
                }else{
                    insertIntoDatabase();
                    //exit activity
                    finish();
                }

                return true;

            case R.id.delete:

                return true;

            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(EditActivity.this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {

        if (DateFormat.is24HourFormat(EditActivity.this)) {
            time_string= hour + " : " + minute;
        } else {
            if (hour == 0) {
                time_string = "12 : " + minute + " AM";
            } else if (hour < 12 && hour > 0) {
                time_string = hour + " : " + minute + " AM";
            } else if (hour == 12)
            {
                time_string = hour + " : " + minute + " PM";
            }
            else
            {
                time_string = (hour-12) + " : " + minute + " PM";
            }
        }
        time.setText(time_string);

    }
}
