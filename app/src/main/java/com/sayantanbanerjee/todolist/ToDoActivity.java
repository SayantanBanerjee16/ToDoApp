package com.sayantanbanerjee.todolist;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.sayantanbanerjee.todolist.data.ToDoContract;

public class ToDoActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int TODO_LOADER = 1;
    private Uri mCurrentToDoUri;
    Switch nSwitch;

    TextView heading;
    TextView message;
    TextView date;
    TextView time;

    int MONTH;
    int DAY;
    int YEAR;
    int HOUR;
    int MINUTE;


    public void back(View view) {
        NavUtils.navigateUpFromSameTask(ToDoActivity.this);
    }

    public void edit(View view) {
        Intent intent = new Intent(ToDoActivity.this, EditActivity.class);
        intent.setData(mCurrentToDoUri);
        startActivity(intent);
    }

    private void deleteToDo() {
        int rowsDeleted = getContentResolver().delete(mCurrentToDoUri, null, null);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do);
        getSupportActionBar().hide();

        heading = (TextView) findViewById(R.id.heading_ToDo);
        message = (TextView) findViewById(R.id.message_ToDo);
        date = (TextView) findViewById(R.id.date_ToDo);
        time = (TextView) findViewById(R.id.time_ToDo);
        nSwitch = (Switch) findViewById(R.id.notificationSwitch);


        Intent intent = getIntent();
        mCurrentToDoUri = intent.getData();

        getSupportLoaderManager().initLoader(TODO_LOADER, null, this);
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
            int notificationColumnIndex = cursor.getColumnIndex(ToDoContract.ToDoEntry.COLUMN_TIME);

            int id_todo = cursor.getInt(idColumnIndex);
            String heading_todo = cursor.getString(headingColumnIndex);
            String message_todo = cursor.getString(messageColumnIndex);
            String date_todo = cursor.getString(dateColumnIndex);
            String time_todo = cursor.getString(timeColumnIndex);
            int notification_todo = cursor.getInt(notificationColumnIndex);

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
                if (time_todo.charAt(8) == 'A' && time_todo.charAt(8) == 'M') {
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

            if(notification_todo == 1){
                nSwitch.setChecked(true);
            }else
            {
                nSwitch.setChecked(false);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
