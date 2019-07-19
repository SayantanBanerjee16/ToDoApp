package com.sayantanbanerjee.todolist.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.sayantanbanerjee.todolist.data.ToDoContract.ToDoEntry;

public class ToDoDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = ToDoDbHelper.class.getSimpleName();
    private static final String DATABASE_NAME = "ToDo.db";
    private static final int DATABASE_VERSION = 1;

    public ToDoDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQL_CREATE_PETS_TABLE =  "CREATE TABLE " + ToDoEntry.TABLE_NAME + " ("
                + ToDoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ToDoEntry.COLUMN_HEADING + " TEXT NOT NULL, "
                + ToDoEntry.COLUMN_MESSAGE + " TEXT NOT NULL, "
                + ToDoEntry.COLUMN_DATE + " TEXT NOT NULL, "
                + ToDoEntry.COLUMN_TIME + " TEXT NOT NULL, "
                + ToDoEntry.COLUMN_NOTIFICATION + " INTEGER NOT NULL);";
        db.execSQL(SQL_CREATE_PETS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
