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
        Uri mCurrentToDoUri = ContentUris.withAppendedId(ToDoContract.ToDoEntry.CONTENT_URI, id);
        context.getContentResolver().update(mCurrentToDoUri, values, null, null);
        Log.i("UPDATION :", Integer.toString(id));
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.listitem, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        int MONTH_CURRENT;
        int DAY_CURRENT;
        int YEAR_CURRENT;
        int HOUR_CURRENT;
        int MINUTE_CURRENT;

        int MONTH_COMPARE;
        int DAY_COMPARE;
        int YEAR_COMPARE;
        int HOUR_COMPARE;
        int MINUTE_COMPARE;

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
                String current_time_string = timeFormatter.format(dateObject);

                if (current_time_string.length() == 9) {
                    current_time_string = "0" + current_time_string;
                }


                String day_string = Character.toString(current_date_string.charAt(0)) + Character.toString(current_date_string.charAt(1));
                String month_string = Character.toString(current_date_string.charAt(5)) + Character.toString(current_date_string.charAt(6));
                String year_string = Character.toString(current_date_string.charAt(10)) + Character.toString(current_date_string.charAt(11)) +
                        Character.toString(current_date_string.charAt(12)) + Character.toString(current_date_string.charAt(13));

                DAY_CURRENT = Integer.parseInt(day_string);
                MONTH_CURRENT = Integer.parseInt(month_string);
                YEAR_CURRENT = Integer.parseInt(year_string);

                String minute_string = Character.toString(current_time_string.charAt(5)) + Character.toString(current_time_string.charAt(6));
                MINUTE_CURRENT = Integer.parseInt(minute_string);

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

                String day_string1 = Character.toString(date.charAt(0)) + Character.toString(date.charAt(1));
                String month_string1 = Character.toString(date.charAt(5)) + Character.toString(date.charAt(6));
                String year_string1 = Character.toString(date.charAt(10)) + Character.toString(date.charAt(11)) +
                        Character.toString(date.charAt(12)) + Character.toString(date.charAt(13));

                DAY_COMPARE = Integer.parseInt(day_string1);
                MONTH_COMPARE = Integer.parseInt(month_string1);
                YEAR_COMPARE = Integer.parseInt(year_string1);

                String minute_string_1 = Character.toString(time.charAt(5)) + Character.toString(time.charAt(6));
                MINUTE_COMPARE = Integer.parseInt(minute_string_1);

                String hour_string = Character.toString(time.charAt(0)) + Character.toString(time.charAt(1));
                int HOUR_COMPARE_TEMP = Integer.parseInt(hour_string);

                if (time.length() == 10) {
                    if (time.charAt(8) == 'A' && time.charAt(9) == 'M') {
                        if (HOUR_COMPARE_TEMP == 12) {
                            HOUR_COMPARE = 0;
                        } else {
                            HOUR_COMPARE = HOUR_COMPARE_TEMP;
                        }
                    } else {
                        if (HOUR_COMPARE_TEMP == 12) {
                            HOUR_COMPARE = 12;
                        } else {
                            HOUR_COMPARE = HOUR_COMPARE_TEMP + 12;
                        }
                    }
                } else {
                    HOUR_COMPARE = HOUR_COMPARE_TEMP;
                }

                if (YEAR_COMPARE < YEAR_CURRENT) {
                    updateIntoDatabaseNotification(context);
                    notification = 2;
                } else if (YEAR_COMPARE > YEAR_CURRENT) {
                } else {
                    if (MONTH_COMPARE < MONTH_CURRENT) {
                        updateIntoDatabaseNotification(context);
                        notification = 2;
                    } else if (MONTH_COMPARE > MONTH_CURRENT) {
                    } else {
                        if (DAY_COMPARE < DAY_CURRENT) {
                            updateIntoDatabaseNotification(context);
                            notification = 2;
                        } else if (DAY_COMPARE > DAY_CURRENT) {
                        } else {
                            if (HOUR_COMPARE < HOUR_CURRENT) {
                                updateIntoDatabaseNotification(context);
                                notification = 2;
                            } else if (HOUR_COMPARE > HOUR_CURRENT) {
                            } else {
                                if(MINUTE_COMPARE <= MINUTE_CURRENT){
                                    updateIntoDatabaseNotification(context);
                                    notification = 2;
                                }
                            }
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        heading_list.setText(heading);
        date_list.setText(date);
        time_list.setText(time);


    }
}
