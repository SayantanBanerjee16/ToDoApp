package com.sayantanbanerjee.todolist;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class EditActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        getSupportActionBar().setTitle("Edit To-Do");
    }
}
