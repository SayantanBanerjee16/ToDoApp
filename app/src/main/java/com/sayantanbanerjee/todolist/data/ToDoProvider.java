package com.sayantanbanerjee.todolist.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import android.content.Context;


import com.sayantanbanerjee.todolist.data.ToDoContract.ToDoEntry;

import java.util.Objects;

public class ToDoProvider extends ContentProvider {
    private ToDoDbHelper mDbHelper;

    //Add Uri Matcher
    private static final int TODO = 100;
    private static final int TODO_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(ToDoContract.CONTENT_AUTHORITY,ToDoContract.PATH_TODO,TODO);
        sUriMatcher.addURI(ToDoContract.CONTENT_AUTHORITY,ToDoContract.PATH_TODO + "/#",TODO_ID);
    }


    @Override
    public boolean onCreate() {
        mDbHelper = new ToDoDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String s1) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor;

        int match = sUriMatcher.match(uri);

        switch(match){
            case TODO:
                cursor = db.query(ToDoEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,s1);
                break;

            case TODO_ID:
                selection = ToDoEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(ToDoEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,s1);
                break;

            default:
                throw new IllegalArgumentException(uri + "INVALID");
        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);
        switch (match){
            case TODO:
                return ToDoEntry.CONTENT_LIST_TYPE;

            case TODO_ID:
                return ToDoEntry.CONTENT_ITEM_TYPE;

            default:
                throw new IllegalStateException("Unknown URI" + uri + " with match" + match);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int match = sUriMatcher.match(uri);

        switch (match){
            case TODO:

                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                long id = db.insert(ToDoEntry.TABLE_NAME,null,values);

                if(id == -1){
                    Log.i("Error: "," Insertion failed");
                    return null;
                }else{
                    getContext().getContentResolver().notifyChange(uri,null);
                    return ContentUris.withAppendedId(uri,id);
                }
                default:
                    throw new IllegalArgumentException("Insertion Failed!");
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TODO:
                rowsDeleted = database.delete(ToDoEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TODO_ID:
                selection = ToDoEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(ToDoEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TODO:
                return updateToDo(uri, contentValues, selection, selectionArgs);
            case TODO_ID:
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = ToDoEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateToDo(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateToDo(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.size() == 0) {
            return 0;
        }
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsUpdated = database.update(ToDoEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
