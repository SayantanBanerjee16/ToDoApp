package com.sayantanbanerjee.todolist;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.sayantanbanerjee.todolist.data.ToDoContract;

import java.text.SimpleDateFormat;
import java.util.Date;


public class ToDoCursorAdapter extends CursorAdapter {

    int notification;
    int id;

    public ToDoCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    private void updateIntoDatabaseNotification(Context context) {
        ContentValues values = new ContentValues();
        values.put(ToDoContract.ToDoEntry.COLUMN_NOTIFICATION, 2);
        Uri mCurrentToDoUri = ContentUris.withAppendedId(ToDoContract.ToDoEntry.CONTENT_URI,id);
        context.getContentResolver().update(mCurrentToDoUri, values, null, null);
        Log.i("UPDATION :",Integer.toString(id));
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.listitem, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView heading_list = (TextView) view.findViewById(R.id.heading_list);
        TextView date_list = (TextView) view.findViewById(R.id.date_list);
        TextView time_list = (TextView) view.findViewById(R.id.time_list);

        int idColIndex = cursor.getColumnIndex(ToDoContract.ToDoEntry._ID);
        int headingColIndex = cursor.getColumnIndex(ToDoContract.ToDoEntry.COLUMN_HEADING);
        int dateColIndex = cursor.getColumnIndex(ToDoContract.ToDoEntry.COLUMN_DATE);
        int timeColIndex = cursor.getColumnIndex(ToDoContract.ToDoEntry.COLUMN_TIME);
        int notificationColIndex = cursor.getColumnIndex(ToDoContract.ToDoEntry.COLUMN_NOTIFICATION);

        id = cursor.getInt(idColIndex);
        String heading = cursor.getString(headingColIndex);
        String date = cursor.getString(dateColIndex);
        String time = cursor.getString(timeColIndex);
        notification = cursor.getInt(notificationColIndex);

        if (notification != 2) {
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
                Date date_current = dateFormatter.parse(current_date_string);
                String current_time_string = timeFormatter.format(dateObject);
                Date time_current = timeFormatter.parse(current_time_string);

                Date date_compare = dateFormatter.parse(date);
                Date time_compare = timeFormatter.parse(time);

                Log.i("date compare", date);
                Log.i("date current", current_date_string);
                Log.i("time compare", time);
                Log.i("time current", current_time_string);

                Log.i("date compare", String.valueOf(date_compare));
                Log.i("date current", String.valueOf(date_current));
                Log.i("time compare", String.valueOf(time_compare));
                Log.i("time current", String.valueOf(time_current));

                if (date_current.compareTo(date_compare) >= 0) {
                    if (time_current.compareTo(time_compare) >= 0) {
                        updateIntoDatabaseNotification(context);
                        notification = 2;
                    }
                }
                Log.i("UPDATION :",Integer.toString(notification));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        heading_list.setText(heading);
        date_list.setText(date);
        time_list.setText(time);


    }
}
