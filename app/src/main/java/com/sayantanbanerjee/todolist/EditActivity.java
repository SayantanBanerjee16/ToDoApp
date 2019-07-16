package com.sayantanbanerjee.todolist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.fragment.app.DialogFragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
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

public class EditActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, LoaderManager.LoaderCallbacks<Cursor> {

    Button time;
    Button date;

    String time_string;
    String date_string;
    String heading_string;
    String message_string;
    EditText heading;
    EditText message;

    Uri mCurrentToDoUri;

    int MONTH;
    int DAY;
    int YEAR;

    private static final int TODO_LOADER = 1;

    private DatePickerDialog.OnDateSetListener mDateSetListener;

    private void setDialogOfDate(int year, int month, int day) {
        DatePickerDialog dialog = new DatePickerDialog(
                EditActivity.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                mDateSetListener, year, month, day);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public void setDateDialog(View view) {

        if (mCurrentToDoUri == null) {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            setDialogOfDate(year, month, day);
        } else {
            setDialogOfDate(YEAR, MONTH - 1, DAY);
        }
    }


    public void setTimeDialog(View view) {
        DialogFragment timePicker = new TimePickerFragment();
        timePicker.show(getSupportFragmentManager(), "time picker");
    }

    public void insertIntoDatabase() {
        date_string = date_string.trim();
        time_string = time_string.trim();

        ContentValues values = new ContentValues();
        values.put(ToDoContract.ToDoEntry.COLUMN_HEADING, heading_string);
        values.put(ToDoContract.ToDoEntry.COLUMN_MESSAGE, message_string);
        values.put(ToDoContract.ToDoEntry.COLUMN_DATE, date_string);
        values.put(ToDoContract.ToDoEntry.COLUMN_TIME, time_string);

        Uri uri = getContentResolver().insert(ToDoContract.ToDoEntry.CONTENT_URI, values);
        if (uri == null) {
            Toast.makeText(this, "Error with inserting To Do",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "To Do Saved",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void updateIntoDatabase() {
        date_string = date_string.trim();
        time_string = time_string.trim();

        ContentValues values = new ContentValues();
        values.put(ToDoContract.ToDoEntry.COLUMN_HEADING, heading_string);
        values.put(ToDoContract.ToDoEntry.COLUMN_MESSAGE, message_string);
        values.put(ToDoContract.ToDoEntry.COLUMN_DATE, date_string);
        values.put(ToDoContract.ToDoEntry.COLUMN_TIME, time_string);

        Integer rowsAffected = getContentResolver().update(mCurrentToDoUri, values, null, null);
        if (rowsAffected == null) {
            Toast.makeText(this, "Error with updating To Do",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "To Do Updated",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        time = (Button) findViewById(R.id.timeButton);
        date = (Button) findViewById(R.id.dateButton);
        heading = (EditText) findViewById(R.id.heading);
        message = (EditText) findViewById(R.id.message);

        Intent intent = getIntent();
        mCurrentToDoUri = intent.getData();

        if (mCurrentToDoUri == null) {
            getSupportActionBar().setTitle("Add To-Do");

        } else {
            getSupportActionBar().setTitle("Edit To-Do");
            getSupportLoaderManager().initLoader(TODO_LOADER, null, this);
        }


        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month += 1;
                if(day<10)
                {
                    if(month<10)
                    {
                        date_string = "0" + day + " / 0" + month + " / " + year;
                    }else{
                        date_string = "0" + day + " / " + month + " / " + year;
                    }
                }else{
                    if(month<10)
                    {
                        date_string = day + " / 0" + month + " / " + year;
                    }else{
                        date_string = day + " / " + month + " / " + year;
                    }
                }

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
                if (TextUtils.isEmpty(heading_string) || TextUtils.isEmpty(message_string) || TextUtils.isEmpty(time_string) || TextUtils.isEmpty(date_string)) {
                    Toast.makeText(EditActivity.this, "No Field Should remain empty!", Toast.LENGTH_LONG).show();
                } else {

                    if (mCurrentToDoUri == null) {
                        insertIntoDatabase();
                    } else {
                        updateIntoDatabase();
                    }

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
            time_string = hour + " : " + minute;
        } else {
            if (hour == 0) {
                time_string = "12 : " + minute + " AM";
            } else if (hour < 12 && hour > 0) {
                time_string = hour + " : " + minute + " AM";
            } else if (hour == 12) {
                time_string = hour + " : " + minute + " PM";
            } else {
                time_string = (hour - 12) + " : " + minute + " PM";
            }
        }
        time.setText(time_string);

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ToDoContract.ToDoEntry.COLUMN_HEADING,
                ToDoContract.ToDoEntry.COLUMN_MESSAGE,
                ToDoContract.ToDoEntry.COLUMN_DATE,
                ToDoContract.ToDoEntry.COLUMN_TIME};

        return new CursorLoader(this, mCurrentToDoUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            int headingColumnIndex = cursor.getColumnIndex(ToDoContract.ToDoEntry.COLUMN_HEADING);
            int messageColumnIndex = cursor.getColumnIndex(ToDoContract.ToDoEntry.COLUMN_MESSAGE);
            int dateColumnIndex = cursor.getColumnIndex(ToDoContract.ToDoEntry.COLUMN_DATE);
            int timeColumnIndex = cursor.getColumnIndex(ToDoContract.ToDoEntry.COLUMN_TIME);

            heading_string = cursor.getString(headingColumnIndex);
            message_string = cursor.getString(messageColumnIndex);
            date_string = cursor.getString(dateColumnIndex);
            time_string = cursor.getString(timeColumnIndex);

            String day_string = Character.toString(date_string.charAt(0)) + Character.toString(date_string.charAt(1));
            String month_string = Character.toString(date_string.charAt(5)) + Character.toString(date_string.charAt(6));
            String year_string = Character.toString(date_string.charAt(10)) + Character.toString(date_string.charAt(11)) +
                    Character.toString(date_string.charAt(12)) + Character.toString(date_string.charAt(13));

            DAY = Integer.parseInt(day_string);
            MONTH = Integer.parseInt(month_string);
            YEAR = Integer.parseInt(year_string);
            
            heading.setText(heading_string);
            message.setText(message_string);
            date.setText(date_string);
            time.setText(time_string);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
