package com.sayantanbanerjee.todolist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.sayantanbanerjee.todolist.data.ToDoContract;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ToDoActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int TODO_LOADER = 1;
    private Uri mCurrentToDoUri;
    Switch nSwitch;

    TextView heading;
    TextView message;
    TextView date;
    TextView time;
    TextView NotificationText;

    String heading_todo;
    String message_todo;
    String date_todo;
    String time_todo;
    int id_todo;
    int notification_todo;

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


    int isSwitchChecked;

    boolean firstTimeFlag;

    AlarmManager alarmManager;
    PendingIntent broadcast;
    Intent notificationIntent;

    private void updateIntoDatabaseNotification(int i) {
        ContentValues values = new ContentValues();
        values.put(ToDoContract.ToDoEntry.COLUMN_NOTIFICATION, i);
        getContentResolver().update(mCurrentToDoUri, values, null, null);
    }

    private void Notification() {
        Log.i("NOTIFICATION", " CREATED");
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        notificationIntent = new Intent(this, NotificationReceiver.class);
        notificationIntent.putExtra("ID", id_todo);
        notificationIntent.putExtra("Title", heading_todo);
        notificationIntent.putExtra("Message", message_todo);
        notificationIntent.putExtra("SwitchChecked", isSwitchChecked);
        broadcast = PendingIntent.getBroadcast(this, id_todo, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, DAY);
        cal.set(Calendar.MONTH, MONTH - 1);
        cal.set(Calendar.YEAR, YEAR);
        cal.set(Calendar.HOUR_OF_DAY, HOUR);
        cal.set(Calendar.MINUTE, MINUTE);
        cal.set(Calendar.SECOND, 0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), broadcast);
    }

    private void cancelNotification() {
        if (broadcast != null) {
            Log.i("NOTIFICATION", " CANCELLED");
            alarmManager.cancel(broadcast);
            broadcast = null;
        }

    }


    public void back(View view) {
        NavUtils.navigateUpFromSameTask(ToDoActivity.this);
    }

    public void edit(View view) {
        Intent intent = new Intent(ToDoActivity.this, EditActivity.class);
        intent.setData(mCurrentToDoUri);
        cancelNotification();
        updateIntoDatabaseNotification(0);
        startActivity(intent);
        this.finish();
    }

    private void deleteToDo() {
        int rowsDeleted = getContentResolver().delete(mCurrentToDoUri, null, null);
        cancelNotification();
        if (rowsDeleted == 0) {
            Toast.makeText(this, "Error with deleting To Do",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Deletion of To Do Successfully",
                    Toast.LENGTH_SHORT).show();
        }
        // Close the activity
        finish();

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

    public void delete(View view) {
        showDeleteConfirmationDialog();
    }

    public void updateIntoDatabase() {

        ContentValues values = new ContentValues();
        values.put(ToDoContract.ToDoEntry.COLUMN_HEADING, heading_todo);
        values.put(ToDoContract.ToDoEntry.COLUMN_MESSAGE, message_todo);
        values.put(ToDoContract.ToDoEntry.COLUMN_DATE, date_todo);
        values.put(ToDoContract.ToDoEntry.COLUMN_TIME, time_todo);
        values.put(ToDoContract.ToDoEntry.COLUMN_NOTIFICATION, isSwitchChecked);

        Integer rowsAffected = getContentResolver().update(mCurrentToDoUri, values, null, null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do);
        getSupportActionBar().hide();

        heading = (TextView) findViewById(R.id.heading_ToDo);
        message = (TextView) findViewById(R.id.message_ToDo);
        date = (TextView) findViewById(R.id.date_ToDo);
        time = (TextView) findViewById(R.id.time_ToDo);
        NotificationText = (TextView) findViewById(R.id.notificationText);
        nSwitch = (Switch) findViewById(R.id.notificationSwitch);

        Intent intent = getIntent();
        mCurrentToDoUri = intent.getData();

        if (mCurrentToDoUri == null) {
            int position_ID = intent.getIntExtra("ID", 0);
            mCurrentToDoUri = ContentUris.withAppendedId(ToDoContract.ToDoEntry.CONTENT_URI, position_ID);
        }

        getSupportLoaderManager().initLoader(TODO_LOADER, null, this);
        firstTimeFlag = true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        nSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!firstTimeFlag) {
                    if (isChecked) {
                        Log.i("IF: ", Boolean.toString(isChecked));
                        isSwitchChecked = 1;
                        updateIntoDatabase();
                        Notification();
                    } else {
                        Log.i("ELSE: ", Boolean.toString(isChecked));
                        isSwitchChecked = 0;
                        updateIntoDatabase();
                        cancelNotification();
                    }
                }
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ToDoContract.ToDoEntry._ID,
                ToDoContract.ToDoEntry.COLUMN_HEADING,
                ToDoContract.ToDoEntry.COLUMN_MESSAGE,
                ToDoContract.ToDoEntry.COLUMN_DATE,
                ToDoContract.ToDoEntry.COLUMN_TIME,
                ToDoContract.ToDoEntry.COLUMN_NOTIFICATION};

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
            int notificationColumnIndex = cursor.getColumnIndex(ToDoContract.ToDoEntry.COLUMN_NOTIFICATION);

            id_todo = cursor.getInt(idColumnIndex);
            heading_todo = cursor.getString(headingColumnIndex);
            message_todo = cursor.getString(messageColumnIndex);
            date_todo = cursor.getString(dateColumnIndex);
            time_todo = cursor.getString(timeColumnIndex);
            notification_todo = cursor.getInt(notificationColumnIndex);

            String day_string = Character.toString(date_todo.charAt(0)) + Character.toString(date_todo.charAt(1));
            String month_string = Character.toString(date_todo.charAt(5)) + Character.toString(date_todo.charAt(6));
            String year_string = Character.toString(date_todo.charAt(10)) + Character.toString(date_todo.charAt(11)) +
                    Character.toString(date_todo.charAt(12)) + Character.toString(date_todo.charAt(13));

            DAY = Integer.parseInt(day_string);
            MONTH = Integer.parseInt(month_string);
            YEAR = Integer.parseInt(year_string);

            String minute_string = Character.toString(time_todo.charAt(5)) + Character.toString(time_todo.charAt(6));
            MINUTE = Integer.parseInt(minute_string);

            String hour_string = Character.toString(time_todo.charAt(0)) + Character.toString(time_todo.charAt(1));
            int HOUR_TEMP = Integer.parseInt(hour_string);

            if (time_todo.length() == 10) {
                if (time_todo.charAt(8) == 'A' && time_todo.charAt(9) == 'M') {
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

            heading.setText(heading_todo);
            message.setText(message_todo);
            date.setText(date_todo);
            time.setText(time_todo);

            Log.i("NOTIFICATION TODO", Integer.toString(notification_todo));


            if (notification_todo != 2) {
                try {
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
                        updateIntoDatabaseNotification(2);
                        notification_todo = 2;
                    } else if (YEAR > YEAR_CURRENT) {
                    } else {
                        if (MONTH < MONTH_CURRENT) {
                            updateIntoDatabaseNotification(2);
                            notification_todo = 2;
                        } else if (MONTH > MONTH_CURRENT) {
                        } else {
                            if (DAY < DAY_CURRENT) {
                                updateIntoDatabaseNotification(2);
                                notification_todo = 2;
                            } else if (DAY > DAY_CURRENT) {
                            } else {
                                if (HOUR < HOUR_CURRENT) {
                                    updateIntoDatabaseNotification(2);
                                    notification_todo = 2;
                                } else if (HOUR > HOUR_CURRENT) {
                                } else {
                                    if(MINUTE <= MINUTE_CURRENT){
                                        updateIntoDatabaseNotification(2);
                                        notification_todo = 2;
                                    }
                                }
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (notification_todo == 2) {
                nSwitch.setEnabled(false);
                NotificationText.setText("To Do already occured!");
            } else {
                if (notification_todo == 1) {
                    nSwitch.setChecked(true);
                    isSwitchChecked = 1;
                    NotificationText.setText("Tap to disable Notification");
                    Log.i("DOWN", "IF");
                } else {
                    nSwitch.setChecked(false);
                    isSwitchChecked = 0;
                    NotificationText.setText("Tap to enable Notification");
                    Log.i("DOWN", "ELSE");
                }
                Log.i("LOAD FINISHED", Integer.toString(isSwitchChecked));
            }

            firstTimeFlag = false;
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
