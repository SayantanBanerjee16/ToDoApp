package com.sayantanbanerjee.todolist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.fragment.app.DialogFragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.sayantanbanerjee.todolist.data.ToDoContract;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static java.sql.Types.NULL;

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

    int id_todo;
    int MONTH;
    int DAY;
    int YEAR;
    int HOUR;
    int MINUTE;

    int MONTH_CURRENT;
    int DAY_CURRENT;
    int YEAR_CURRENT;
    int HOUR_CURRENT;
    int MINUTE_CURRENT;

    private static final int TODO_LOADER = 1;
    private boolean mToDoHasChanged = false;

    private boolean checkDateAndTime() {

        Date dateObject = new Date();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd / MM / YYYY");
        SimpleDateFormat timeFormatter;
        if (time.length() == 10) {
            timeFormatter = new SimpleDateFormat("h : mm a");
        } else {
            timeFormatter = new SimpleDateFormat("HH : mm");
        }

        String current_date_string = dateFormatter.format(dateObject);
        String current_time_string = timeFormatter.format(dateObject);

        if (current_time_string.length() == 9) {
            current_time_string = "0" + current_time_string;
        }

        String day_string1 = Character.toString(current_date_string.charAt(0)) + Character.toString(current_date_string.charAt(1));
        String month_string1 = Character.toString(current_date_string.charAt(5)) + Character.toString(current_date_string.charAt(6));
        String year_string1 = Character.toString(current_date_string.charAt(10)) + Character.toString(current_date_string.charAt(11)) +
                Character.toString(current_date_string.charAt(12)) + Character.toString(current_date_string.charAt(13));

        DAY_CURRENT = Integer.parseInt(day_string1);
        MONTH_CURRENT = Integer.parseInt(month_string1);
        YEAR_CURRENT = Integer.parseInt(year_string1);

        String minute_string1 = Character.toString(current_time_string.charAt(5)) + Character.toString(current_time_string.charAt(6));
        MINUTE_CURRENT = Integer.parseInt(minute_string1);

        String HOUR_CURRENT_string = Character.toString(current_time_string.charAt(0)) + Character.toString(current_time_string.charAt(1));
        int HOUR_CURRENT_TEMP = Integer.parseInt(HOUR_CURRENT_string);

        if (current_time_string.length() == 10) {
            if (current_time_string.charAt(8) == 'a' && current_time_string.charAt(9) == 'm') {
                if (HOUR_CURRENT_TEMP == 12) {
                    HOUR_CURRENT = 0;
                } else {
                    HOUR_CURRENT = HOUR_CURRENT_TEMP;
                }
            } else {
                if (HOUR_CURRENT_TEMP == 12) {
                    HOUR_CURRENT = 12;
                } else {
                    HOUR_CURRENT = HOUR_CURRENT_TEMP + 12;
                }
            }
        } else {
            HOUR_CURRENT = HOUR_CURRENT_TEMP;
        }

        if (YEAR < YEAR_CURRENT) {
            return true;
        } else if (YEAR > YEAR_CURRENT) {
            return false;
        } else {
            if (MONTH < MONTH_CURRENT) {
                return true;
            } else if (MONTH > MONTH_CURRENT) {
                return false;
            } else {
                if (DAY < DAY_CURRENT) {
                    return true;
                } else if (DAY > DAY_CURRENT) {
                    return false;
                } else {
                    if (HOUR < HOUR_CURRENT) {
                        return true;
                    } else if (HOUR > HOUR_CURRENT) {
                        return false;
                    } else {
                        if(MINUTE <= MINUTE_CURRENT){
                            return true;
                        }else{
                            return false;
                        }
                    }
                }
            }
        }

    }

    private void showUnsavedChangesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Discard your changes and quit Editing?");
        builder.setPositiveButton("DISCARD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (mCurrentToDoUri == null) {
                    NavUtils.navigateUpFromSameTask(EditActivity.this);
                } else {
                    Intent intent = new Intent(EditActivity.this, ToDoActivity.class);
                    intent.setData(mCurrentToDoUri);
                    startActivity(intent);
                }
                finish();

            }
        });
        builder.setNegativeButton("KEEP EDITING", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private DatePickerDialog.OnDateSetListener mDateSetListener;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mToDoHasChanged = true;
            return false;
        }
    };

    private void Keyboard_management() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }


    private void deleteToDo() {
        int rowsDeleted = getContentResolver().delete(mCurrentToDoUri, null, null);
        Keyboard_management();
        if (rowsDeleted == 0) {
            Toast.makeText(this, "Error with deleting To Do",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Deletion of To Do Successfully",
                    Toast.LENGTH_SHORT).show();
        }
        Intent intent = new Intent(EditActivity.this, ListActivity.class);
        startActivity(intent);
        this.finish();

    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure, you want to delete it?");
        builder.setCancelable(false);
        builder.setTitle("DELETE");
        builder.setIcon(android.R.drawable.ic_menu_delete);
        builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteToDo();
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void setDialogOfDate(int year, int month, int day) {
        DatePickerDialog dialog = new DatePickerDialog(
                EditActivity.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                mDateSetListener, year, month, day);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public void setDateDialog(View view) {

        if (YEAR == NULL && DAY == NULL && MONTH == NULL) {
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
        if (HOUR != NULL && MINUTE != NULL) {
            Bundle bundle = new Bundle();
            bundle.putInt("hour", HOUR);
            bundle.putInt("minute", MINUTE);
            timePicker.setArguments(bundle);
        }
        timePicker.show(getSupportFragmentManager(), "time picker");
    }

    public int insertIntoDatabase() {
        date_string = date_string.trim();
        time_string = time_string.trim();

        ContentValues values = new ContentValues();
        values.put(ToDoContract.ToDoEntry.COLUMN_HEADING, heading_string);
        values.put(ToDoContract.ToDoEntry.COLUMN_MESSAGE, message_string);
        values.put(ToDoContract.ToDoEntry.COLUMN_DATE, date_string);
        values.put(ToDoContract.ToDoEntry.COLUMN_TIME, time_string);
        values.put(ToDoContract.ToDoEntry.COLUMN_NOTIFICATION, 0);

        Uri uri = getContentResolver().insert(ToDoContract.ToDoEntry.CONTENT_URI, values);

        long id = Long.parseLong(uri.getLastPathSegment());

        int ID = (int) id;

        if (uri == null) {
            Toast.makeText(this, "Error with inserting To Do",
                    Toast.LENGTH_SHORT).show();
        } else {
            long id_uri = ContentUris.parseId(uri);
            id_todo = (int) id_uri;
            Toast.makeText(this, "To Do Saved",
                    Toast.LENGTH_SHORT).show();
        }
        return ID;
    }

    public void updateIntoDatabase() {
        date_string = date_string.trim();
        time_string = time_string.trim();

        ContentValues values = new ContentValues();
        values.put(ToDoContract.ToDoEntry.COLUMN_HEADING, heading_string);
        values.put(ToDoContract.ToDoEntry.COLUMN_MESSAGE, message_string);
        values.put(ToDoContract.ToDoEntry.COLUMN_DATE, date_string);
        values.put(ToDoContract.ToDoEntry.COLUMN_TIME, time_string);
        values.put(ToDoContract.ToDoEntry.COLUMN_NOTIFICATION, 0);

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

        time.setOnTouchListener(mTouchListener);
        date.setOnTouchListener(mTouchListener);
        heading.setOnTouchListener(mTouchListener);
        message.setOnTouchListener(mTouchListener);

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

                DAY = day;
                MONTH = month;
                YEAR = year;
                if (day < 10) {
                    if (month < 10) {
                        date_string = "0" + day + " / 0" + month + " / " + year;
                    } else {
                        date_string = "0" + day + " / " + month + " / " + year;
                    }
                } else {
                    if (month < 10) {
                        date_string = day + " / 0" + month + " / " + year;
                    } else {
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem delete = menu.findItem(R.id.delete);
        delete.setIcon(android.R.drawable.ic_menu_delete);
        if (mCurrentToDoUri != null) {
            delete.setVisible(true);
        } else {
            delete.setVisible(false);
        }
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
                } else if (checkDateAndTime()) {
                    Keyboard_management();
                    Toast.makeText(EditActivity.this, "Date and Time are already in Past!", Toast.LENGTH_LONG).show();
                } else {
                    Keyboard_management();
                    if (mCurrentToDoUri == null) {
                        int ID = insertIntoDatabase();
                        Intent intent = new Intent(EditActivity.this, ToDoActivity.class);
                        Uri currentToDoUri = ContentUris.withAppendedId(ToDoContract.ToDoEntry.CONTENT_URI, ID);
                        intent.setData(currentToDoUri);
                        startActivity(intent);
                    } else {

                        updateIntoDatabase();
                        Intent intent = new Intent(EditActivity.this, ToDoActivity.class);
                        intent.setData(mCurrentToDoUri);
                        startActivity(intent);
                    }
                    //exit activity
                    finish();
                }
                return true;

            case R.id.delete:
                Keyboard_management();
                showDeleteConfirmationDialog();
                return true;

            case android.R.id.home:
                Keyboard_management();
                if (!mToDoHasChanged) {
                    if (mCurrentToDoUri == null) {
                        NavUtils.navigateUpFromSameTask(EditActivity.this);
                    } else {
                        Intent intent = new Intent(EditActivity.this, ToDoActivity.class);
                        intent.setData(mCurrentToDoUri);
                        startActivity(intent);
                    }
                    finish();
                } else {
                    showUnsavedChangesDialog();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onBackPressed() {
        if (!mToDoHasChanged) {
            if (mCurrentToDoUri == null) {
                super.onBackPressed();
                return;
            } else {
                Intent intent = new Intent(EditActivity.this, ToDoActivity.class);
                intent.setData(mCurrentToDoUri);
                startActivity(intent);
                finish();
            }

        } else {
            showUnsavedChangesDialog();
        }

    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {

        if (DateFormat.is24HourFormat(EditActivity.this)) {
            if (hour < 10) {
                if (minute < 10) {
                    time_string = "0" + hour + " : 0" + minute;
                } else {
                    time_string = "0" + hour + " : " + minute;
                }
            } else {
                if (minute < 10) {
                    time_string = hour + " : 0" + minute;
                } else {
                    time_string = hour + " : " + minute;
                }
            }
        } else {
            if (hour == 0) {
                if (minute < 10) {
                    time_string = 12 + " : 0" + minute + " AM";
                } else {
                    time_string = 12 + " : " + minute + " AM";
                }
            } else if (hour < 10) {
                if (minute < 10) {
                    time_string = "0" + hour + " : 0" + minute + " AM";
                } else {
                    time_string = "0" + hour + " : " + minute + " AM";
                }
            } else if (hour < 12) {
                if (minute < 10) {
                    time_string = hour + " : 0" + minute + " AM";
                } else {
                    time_string = hour + " : " + minute + " AM";
                }
            } else if (hour == 12) {
                if (minute < 10) {
                    time_string = 12 + " : 0" + minute + " PM";
                } else {
                    time_string = 12 + " : " + minute + " PM";
                }
            } else {
                if (minute < 10) {
                    if (hour - 12 < 10) {
                        time_string = "0" + (hour - 12) + " : 0" + minute + " PM";
                    } else {
                        time_string = (hour - 12) + " : 0" + minute + " PM";
                    }
                } else {
                    if (hour - 12 < 10) {
                        time_string = "0" + (hour - 12) + " : " + minute + " PM";
                    } else {
                        time_string = (hour - 12) + " : " + minute + " PM";
                    }
                }
            }
        }
        time.setText(time_string);

        HOUR = hour;
        MINUTE = minute;

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ToDoContract.ToDoEntry._ID,
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
            int idColumnIndex = cursor.getColumnIndex(ToDoContract.ToDoEntry._ID);
            int headingColumnIndex = cursor.getColumnIndex(ToDoContract.ToDoEntry.COLUMN_HEADING);
            int messageColumnIndex = cursor.getColumnIndex(ToDoContract.ToDoEntry.COLUMN_MESSAGE);
            int dateColumnIndex = cursor.getColumnIndex(ToDoContract.ToDoEntry.COLUMN_DATE);
            int timeColumnIndex = cursor.getColumnIndex(ToDoContract.ToDoEntry.COLUMN_TIME);

            id_todo = cursor.getInt(idColumnIndex);
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

            String minute_string = Character.toString(time_string.charAt(5)) + Character.toString(time_string.charAt(6));
            MINUTE = Integer.parseInt(minute_string);

            String hour_string = Character.toString(time_string.charAt(0)) + Character.toString(time_string.charAt(1));
            int HOUR_TEMP = Integer.parseInt(hour_string);

            if (time_string.length() == 10) {
                if (time_string.charAt(8) == 'A' && time_string.charAt(9) == 'M') {
                    if (HOUR_TEMP == 12) {
                        HOUR = 0;
                    } else {
                        HOUR = HOUR_TEMP;
                    }
                } else {
                    if (HOUR_TEMP == 12) {
                        HOUR = 12;
                    } else {
                        HOUR = HOUR_TEMP + 12;
                    }
                }
            } else {
                HOUR = HOUR_TEMP;
            }


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
