package com.sayantanbanerjee.todolist.data;

import android.provider.BaseColumns;

public final class ToDoContract {

    //constructor
    private ToDoContract(){}

    public static final class ToDoEntry implements BaseColumns{

        public static final String TABLE_NAME = "ToDo";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_HEADING = "heading";
        public static final String COLUMN_MESSAGE = "message";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_TIME = "time";

    }
}
