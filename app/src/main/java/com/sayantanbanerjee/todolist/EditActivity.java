package com.sayantanbanerjee.todolist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.fragment.app.DialogFragment;
import androidx.loader.app.LoaderManager;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

public class EditActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    Button time;


    public void setTimeDialog(View view) {
        DialogFragment timePicker = new TimePickerFragment();
        timePicker.show(getSupportFragmentManager(), "time picker");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        getSupportActionBar().setTitle("Edit To-Do");
        time = (Button) findViewById(R.id.time);
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
            time.setText(Integer.toString(hour) + " : " + Integer.toString(minute));
        } else {
            if (hour == 0) {
                time.setText(Integer.toString(12) + " : " + Integer.toString(minute) + " AM");
            } else if (hour < 12 && hour > 0) {
                time.setText(Integer.toString(hour) + " : " + Integer.toString(minute) + " AM");
            } else if (hour == 12)
            {
                time.setText(Integer.toString(hour) + " : " + Integer.toString(minute) + " PM");
            }
            else
            {
                time.setText(Integer.toString(hour - 12) + " : " + Integer.toString(minute) + " PM");
            }
        }

    }
}
