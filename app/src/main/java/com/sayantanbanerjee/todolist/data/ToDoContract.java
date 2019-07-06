package com.sayantanbanerjee.todolist.data;

import android.net.Uri;
import android.provider.BaseColumns;

public final class ToDoContract {

    //constructor
    private ToDoContract(){}

    //adding URI
    public static final String CONTENT_AUTHORITY = "com.example.android.todo";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_TODO = "todo";


    public static final class ToDoEntry implements BaseColumns{

       // Uri to access the provider
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,PATH_TODO);

       //table name and column names
        public static final String TABLE_NAME = "ToDo";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_HEADING = "heading";
        public static final String COLUMN_MESSAGE = "message";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_TIME = "time";

    }
}
