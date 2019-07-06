package com.sayantanbanerjee.todolist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.fragment.app.DialogFragment;
import androidx.loader.app.LoaderManager;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;

public class EditActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    Button time;
    Button date;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        getSupportActionBar().setTitle("Edit To-Do");
        time = (Button) findViewById(R.id.timeButton);
        date = (Button) findViewById(R.id.dateButton);

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month+=1;
                date.setText(Integer.toString(day) + " / " + Integer.toString(month) + " / " + Integer.toString(year));
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
