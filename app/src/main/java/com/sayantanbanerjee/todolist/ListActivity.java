package com.sayantanbanerjee.todolist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sayantanbanerjee.todolist.data.ToDoContract;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int TODO_LOADER_ALL = 1;
    private static final int TODO_LOADER_TODAY = 2;
    ToDoCursorAdapter mCursorAdapter;

    View view;
    ListView listView;
    Boolean today;
    TextView textEmptySubtitle;

    String firstTime;

    private void deletePastToDo() {

        String where = "notification = 2";
        int rowsDeleted = (int) getContentResolver().delete(ToDoContract.ToDoEntry.CONTENT_URI, where, null);
        if (rowsDeleted == 0) {
            Toast.makeText(this, "There isn't any Past ToDo to be deleted!",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Deletion of " + Integer.toString(rowsDeleted) + " To Do Successfully",
                    Toast.LENGTH_SHORT).show();
        }

        mCursorAdapter.swapCursor(null);
        getSupportActionBar().setTitle("Your's All To-Do");
        today = false;
        textEmptySubtitle.setText("Get started by Adding a To-Do");
        getSupportLoaderManager().initLoader(TODO_LOADER_ALL, null, this);

    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure, you want to delete ALL Past ToDo?");
        builder.setCancelable(false);
        builder.setTitle("DELETE");
        builder.setIcon(android.R.drawable.ic_menu_delete);
        builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deletePastToDo();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        getSupportActionBar().setTitle("Your's All To-Do");

        view = (View) findViewById(R.id.empty_view);
        listView = (ListView) findViewById(R.id.list);
        listView.setEmptyView(view);

        textEmptySubtitle = (TextView) findViewById(R.id.empty_subtitle_text);

        mCursorAdapter = new ToDoCursorAdapter(this, null);
        listView.setAdapter(mCursorAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListActivity.this, EditActivity.class);
                startActivity(intent);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(ListActivity.this, ToDoActivity.class);
                Uri currentToDoUri = ContentUris.withAppendedId(ToDoContract.ToDoEntry.CONTENT_URI, l);
                intent.setData(currentToDoUri);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        mCursorAdapter.swapCursor(null);
        getSupportActionBar().setTitle("Your's All To-Do");
        today = false;
        textEmptySubtitle.setText("Get started by Adding a To-Do");
        getSupportLoaderManager().initLoader(TODO_LOADER_ALL, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_listactivity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.deletePast:
                showDeleteConfirmationDialog();
                return true;

            case R.id.todayToDo:
                getSupportActionBar().setTitle("Today's To-Do");
                mCursorAdapter.swapCursor(null);
                today = true;
                textEmptySubtitle.setText("Add a To-Do of current date (Today) to get Visible here");
                getSupportLoaderManager().initLoader(TODO_LOADER_TODAY, null, this);
                return true;

            case R.id.allToDo:
                getSupportActionBar().setTitle("Your's All To-Do");
                mCursorAdapter.swapCursor(null);
                today = false;
                textEmptySubtitle.setText("Get started by Adding a To-Do");
                getSupportLoaderManager().initLoader(TODO_LOADER_ALL, null, this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem menu_today = menu.findItem(R.id.todayToDo);
        MenuItem menu_all = menu.findItem(R.id.allToDo);
        if (today) {
            menu_all.setEnabled(true);
            menu_today.setEnabled(false);
        } else {
            menu_all.setEnabled(false);
            menu_today.setEnabled(true);
        }
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ToDoContract.ToDoEntry._ID,
                ToDoContract.ToDoEntry.COLUMN_HEADING,
                ToDoContract.ToDoEntry.COLUMN_DATE,
                ToDoContract.ToDoEntry.COLUMN_TIME,
                ToDoContract.ToDoEntry.COLUMN_NOTIFICATION};
        switch (id) {
            case TODO_LOADER_ALL:
                return new CursorLoader(this, ToDoContract.ToDoEntry.CONTENT_URI, projection, null, null, null);

            case TODO_LOADER_TODAY:
                Date dateObject = new Date();
                SimpleDateFormat dateFormatter = new SimpleDateFormat("dd / MM / YYYY");
                String current_date_string = dateFormatter.format(dateObject);
                String selection = ToDoContract.ToDoEntry.COLUMN_DATE + " =? ";
                String[] selectionArgs = new String[]{current_date_string};
                Log.i("Current date", current_date_string);
                return new CursorLoader(this, ToDoContract.ToDoEntry.CONTENT_URI, projection, selection, selectionArgs, null);
            default:
                return null;
        }

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        mCursorAdapter.swapCursor(data);
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}
