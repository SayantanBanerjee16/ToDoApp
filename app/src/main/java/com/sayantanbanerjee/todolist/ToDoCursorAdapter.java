package com.sayantanbanerjee.todolist;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.sayantanbanerjee.todolist.data.ToDoContract;

public class ToDoCursorAdapter extends CursorAdapter {
    public ToDoCursorAdapter(Context context, Cursor c){
        super(context,c,0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.listitem, viewGroup,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView heading_list = (TextView) view.findViewById(R.id.heading_list);
        TextView date_list = (TextView) view.findViewById(R.id.date_list);
        TextView time_list = (TextView) view.findViewById(R.id.time_list);

        int headingColIndex = cursor.getColumnIndex(ToDoContract.ToDoEntry.COLUMN_HEADING);
        int dateColIndex = cursor.getColumnIndex(ToDoContract.ToDoEntry.COLUMN_DATE);
        int timeColIndex = cursor.getColumnIndex(ToDoContract.ToDoEntry.COLUMN_TIME);

        String heading = cursor.getString(headingColIndex);
        String date = cursor.getString(dateColIndex);
        String time = cursor.getString(timeColIndex);

        heading_list.setText(heading);
        date_list.setText(date);
        time_list.setText(time);


    }
}
