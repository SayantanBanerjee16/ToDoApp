package com.sayantanbanerjee.todolist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sayantanbanerjee.todolist.data.ToDoContract;

public class ListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int TODO_LOADER = 1;
    ToDoCursorAdapter mCursorAdapter;

    View view;
    ListView listView;

    private void deletePastToDo() {

        String where = "notification = 2";
        int rowsDeleted = (int) getContentResolver().delete(ToDoContract.ToDoEntry.CONTENT_URI, where, null);
        if (rowsDeleted == 0) {
            Toast.makeText(this, "Error with deleting To Do",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Deletion of " + Integer.toString(rowsDeleted) + " To Do Successfully",
                    Toast.LENGTH_SHORT).show();
        }

    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure, you want to delete it?");
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
        getSupportActionBar().setTitle("Your To-Do's");

        view = (View) findViewById(R.id.empty_view);
        listView = (ListView) findViewById(R.id.list);
        listView.setEmptyView(view);

        mCursorAdapter = new ToDoCursorAdapter(this,null);
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

        getSupportLoaderManager().initLoader(TODO_LOADER, null, this);

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

                return true;

            case R.id.allToDo:
                getSupportLoaderManager().initLoader(TODO_LOADER, null, this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ToDoContract.ToDoEntry._ID,
                ToDoContract.ToDoEntry.COLUMN_HEADING,
                ToDoContract.ToDoEntry.COLUMN_DATE,
                ToDoContract.ToDoEntry.COLUMN_TIME,
                ToDoContract.ToDoEntry.COLUMN_NOTIFICATION};

        return new CursorLoader(this, ToDoContract.ToDoEntry.CONTENT_URI,projection,null,null,null);
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
